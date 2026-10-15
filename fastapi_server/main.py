import io
import json
import logging
import os
import re
import sys
from pathlib import Path
from typing import List, Optional

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(encoding="utf-8")

import numpy as np
import torch
import torch.nn.functional as F
from fastapi import FastAPI, File, HTTPException, UploadFile, status
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from PIL import Image

try:
    from model import DenseNet121
except ImportError:
    from fastapi_server.model import DenseNet121

# Setup logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s"
)
logger = logging.getLogger("teacare_fastapi")

app = FastAPI(
    title="TeaCare - Tea Leaf Disease Recognition API",
    description="FastAPI Backend for TeaLeafBD 7-class disease detection using DenseNet-121",
    version="2.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ---------------------------------------------------------------------------
# 1. GROUND TRUTH CLASS ORDER & VIETNAMESE MAPPING
# ---------------------------------------------------------------------------
# Nguồn chính xác 100% từ training notebook DenseNet121.ipynb & TeaLeafBD dataset:
# Thư mục gốc dataset: sorted(os.listdir(DATA_DIR))
SOURCE_CLASSES = [
    "1. Tea algal leaf spot",
    "2. Brown Blight",
    "3. Gray Blight",
    "4. Helopeltis",
    "5. Red spider",
    "6. Green mirid bug",
    "7. Healthy leaf"
]

VIETNAMESE_TRANSLATIONS = {
    "Tea algal leaf spot": "Đốm lá do tảo",
    "Brown Blight": "Bệnh cháy nâu",
    "Gray Blight": "Bệnh cháy xám",
    "Helopeltis": "Bọ xít muỗi hại chè",
    "Red spider": "Nhện đỏ",
    "Green mirid bug": "Bọ xít xanh",
    "Healthy leaf": "Lá chè khỏe mạnh"
}

def clean_class_name(raw_name: str) -> str:
    """Tách tiền tố số và dấu chấm khỏi tên lớp nguồn (VD: '1. Tea algal leaf spot' -> 'Tea algal leaf spot')"""
    return re.sub(r"^\d+\.\s*", "", raw_name).strip()

CLASS_METADATA = []
for idx, src_name in enumerate(SOURCE_CLASSES):
    clean_name = clean_class_name(src_name)
    vi_name = VIETNAMESE_TRANSLATIONS.get(clean_name, clean_name)
    is_healthy = clean_name.lower() == "healthy leaf"
    CLASS_METADATA.append({
        "class_id": idx,
        "source_class": src_name,
        "class_name": clean_name,
        "disease_vi": vi_name,
        "is_healthy": is_healthy
    })

logger.info("Đã đồng bộ thứ tự 7 lớp TeaLeafBD:")
for item in CLASS_METADATA:
    logger.info(f"  [ID {item['class_id']}] {item['source_class']} -> '{item['class_name']}' ({item['disease_vi']})")

# ---------------------------------------------------------------------------
# 2. LOCATE & LOAD MODEL CHECKPOINT
# ---------------------------------------------------------------------------
CURRENT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = CURRENT_DIR.parent

POSSIBLE_WEIGHTS_PATHS = [
    PROJECT_ROOT / "nhandienlache" / "best_densenet121 (1).pth",
    PROJECT_ROOT / "nhandienlache" / "best_densenet121.pth",
    CURRENT_DIR / "best_densenet121 (1).pth",
    CURRENT_DIR / "best_densenet121.pth",
]

MODEL_PATH = None
for p in POSSIBLE_WEIGHTS_PATHS:
    if p.exists():
        MODEL_PATH = p
        break

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
model = DenseNet121(num_classes=len(CLASS_METADATA))

if MODEL_PATH:
    logger.info(f"Đang nạp trọng số mô hình từ: {MODEL_PATH} (Thiết bị: {device})")
    state_dict = torch.load(str(MODEL_PATH), map_location=device)
    model.load_state_dict(state_dict, strict=True)
    logger.info("Mô hình DenseNet121 đã nạp thành công 100% trọng số!")
else:
    logger.error("CẢNH BÁO: Không tìm thấy file checkpoint mô hình! Vui lòng kiểm tra đường dẫn.")

model.to(device)
model.eval()

# ---------------------------------------------------------------------------
# 3. PREPROCESSING (KHỚP 100% VỚI VAL_TEST_TRANSFORM CỦA NOTEBOOK)
# ---------------------------------------------------------------------------
# transforms.Resize((224, 224))
# transforms.ToTensor()
# transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
IMAGENET_MEAN = np.array([0.485, 0.456, 0.406], dtype=np.float32)
IMAGENET_STD = np.array([0.229, 0.224, 0.225], dtype=np.float32)

def preprocess_image(image_bytes: bytes) -> torch.Tensor:
    try:
        image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    except Exception as e:
        raise HTTPException(status_code=400, detail=f"Không thể đọc file ảnh: {str(e)}")

    image = image.resize((224, 224), Image.Resampling.BILINEAR)
    img_arr = np.array(image, dtype=np.float32) / 255.0
    img_arr = (img_arr - IMAGENET_MEAN) / IMAGENET_STD
    img_arr = np.transpose(img_arr, (2, 0, 1))
    tensor = torch.tensor(img_arr, dtype=torch.float32).unsqueeze(0)
    return tensor.to(device)

# ---------------------------------------------------------------------------
# 4. SCHEMAS
# ---------------------------------------------------------------------------
class ClassItem(BaseModel):
    class_id: int
    source_class: str
    class_name: str
    disease_vi: str
    is_healthy: bool

class ClassesResponse(BaseModel):
    classes: List[ClassItem]

class PredictionItem(BaseModel):
    class_id: int
    class_name: str
    disease_vi: str
    confidence: float

class PredictResponse(BaseModel):
    class_id: int
    class_name: str
    disease_vi: str
    confidence: float
    is_healthy: bool
    top3_predictions: List[PredictionItem]
    all_predictions: List[PredictionItem]

class HealthResponse(BaseModel):
    status: str
    model_loaded: bool
    device: str
    num_classes: int
    model_path: str

class ModelInfoResponse(BaseModel):
    model_name: str
    architecture: str
    num_classes: int
    classes: List[ClassItem]
    preprocessing: dict
    device: str

# ---------------------------------------------------------------------------
# 5. ENDPOINTS
# ---------------------------------------------------------------------------
@app.get("/health", response_model=HealthResponse)
def health_check():
    return {
        "status": "ok",
        "model_loaded": MODEL_PATH is not None and MODEL_PATH.exists(),
        "device": str(device),
        "num_classes": len(CLASS_METADATA),
        "model_path": str(MODEL_PATH) if MODEL_PATH else "Not found"
    }

@app.get("/classes", response_model=ClassesResponse)
def get_classes():
    return {"classes": CLASS_METADATA}

@app.get("/model-info", response_model=ModelInfoResponse)
def get_model_info():
    return {
        "model_name": "TeaLeafBD DenseNet-121",
        "architecture": "DenseNet121 (features + Linear(1024, 7))",
        "num_classes": len(CLASS_METADATA),
        "classes": CLASS_METADATA,
        "preprocessing": {
            "image_mode": "RGB",
            "input_size": [224, 224],
            "normalization_mean": [0.485, 0.456, 0.406],
            "normalization_std": [0.229, 0.224, 0.225]
        },
        "device": str(device)
    }

@app.post("/predict", response_model=PredictResponse)
async def predict_leaf_disease(file: UploadFile = File(...)):
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File tải lên không phải là ảnh hợp lệ.")

    content = await file.read()
    if len(content) == 0:
        raise HTTPException(status_code=400, detail="File ảnh rỗng.")

    tensor = preprocess_image(content)

    with torch.inference_mode():
        logits = model(tensor)
        probs = F.softmax(logits, dim=1)[0]

    logits_list = logits[0].cpu().tolist()
    probs_np = probs.cpu().numpy()
    best_id = int(np.argmax(probs_np))
    best_conf = float(probs_np[best_id])

    best_meta = CLASS_METADATA[best_id]
    best_name = best_meta["class_name"]
    best_vi = best_meta["disease_vi"]
    is_healthy = best_meta["is_healthy"]

    sorted_indices = np.argsort(probs_np)[::-1]
    all_predictions = [
        PredictionItem(
            class_id=int(idx),
            class_name=CLASS_METADATA[idx]["class_name"],
            disease_vi=CLASS_METADATA[idx]["disease_vi"],
            confidence=round(float(probs_np[idx]), 4)
        )
        for idx in sorted_indices
    ]

    top3_predictions = all_predictions[:3]

    logger.info(
        f"Inference result: file='{file.filename}' -> Predicted: [{best_id}] {best_name} ({best_vi}) "
        f"with confidence {best_conf * 100:.2f}%. Healthy: {is_healthy}"
    )
    logger.info(f"  Logits: {[round(x, 4) for x in logits_list]}")
    logger.info(f"  Top 3: {[(p.class_name, f'{p.confidence * 100:.2f}%') for p in top3_predictions]}")

    return PredictResponse(
        class_id=best_id,
        class_name=best_name,
        disease_vi=best_vi,
        confidence=round(best_conf, 4),
        is_healthy=is_healthy,
        top3_predictions=top3_predictions,
        all_predictions=all_predictions
    )

if __name__ == "__main__":
    import uvicorn
    print("Starting TeaCare FastAPI server at http://0.0.0.0:8000 ...")
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=False)
