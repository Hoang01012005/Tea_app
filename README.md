# 🍃 TeaCare - Hệ Thống Nhận Diện Bệnh & Chăm Sóc Lá Chè

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green?logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin%20%7C%20Python-blue?logo=kotlin" alt="Kotlin / Python" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Backend-FastAPI-009688?logo=fastapi" alt="FastAPI" />
  <img src="https://img.shields.io/badge/AI%20Model-DenseNet--121-EE4C2C?logo=pytorch" alt="PyTorch DenseNet-121" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-orange" alt="MVVM" />
</p>

**TeaCare** là giải pháp nông nghiệp thông minh kết hợp ứng dụng di động Android hiện đại và mô hình Học sâu (Deep Learning). Ứng dụng hỗ trợ bà con nông dân và người trồng chè nhận diện nhanh chóng các loại sâu bệnh hại lá chè qua hình ảnh, từ đó đưa ra phác đồ điều trị kịp thời và quản lý lịch trình chăm sóc khoa học.

---

## 🌟 Tính Năng Nổi Bật

- 📷 **Chẩn đoán bệnh tức thì qua Camera / Thư viện**: 
  - Chụp ảnh trực tiếp hoặc chọn ảnh lá chè từ máy.
  - Phân tích trực tuyến thông qua FastAPI Backend với mô hình **DenseNet-121**.
  - Hiển thị mức độ tin cậy (%), kết quả chi tiết và **Top 3** khả năng cao nhất.
- 📚 **Cẩm nang sâu bệnh (Disease Library)**:
  - Tra cứu thông tin chi tiết từng loại bệnh hại và sâu hại phổ biến trên cây chè.
  - Cung cấp triệu chứng nhận biết, nguyên nhân phát sinh và các biện pháp phòng ngừa, điều trị nông học / hóa học.
- 🗓️ **Quản lý lịch chăm sóc (Care Schedule)**:
  - Lên lịch tưới nước, bón phân, làm cỏ, phun thuốc phòng ngừa.
  - Lưu trữ offline với **Room Database**, đánh dấu trạng thái hoàn thành dễ dàng.
- 📜 **Lịch sử chẩn đoán (Scan History)**:
  - Tự động lưu trữ các lần quét kèm hình ảnh, thời gian và kết quả chẩn đoán.
  - Theo dõi tiến trình phục hồi của vườn chè theo thời gian.
- ⚙️ **Tùy biến kết nối linh hoạt**:
  - Hỗ trợ đổi địa chỉ IP / Port server trực tiếp ngay trong ứng dụng (dễ dàng chuyển đổi giữa máy ảo Android Emulator và điện thoại thật cùng mạng Wi-Fi LAN).

---

## 🔬 Mô Hình Deep Learning & Các Lớp Nhận Diện

Mô hình phân loại sử dụng kiến trúc mạng **DenseNet-121**, được huấn luyện trên bộ dữ liệu chuẩn **TeaLeafBD** nhận diện chính xác 7 trạng thái lá chè:

| ID | Tên lớp (Tiếng Anh) | Tên bệnh / Sâu hại (Tiếng Việt) | Loại |
|:--:|:--------------------|:--------------------------------|:----:|
| `0` | **Tea algal leaf spot** | Đốm lá do tảo | Bệnh hại |
| `1` | **Brown Blight** | Bệnh cháy nâu lá | Bệnh hại |
| `2` | **Gray Blight** | Bệnh cháy xám lá | Bệnh hại |
| `3` | **Helopeltis** | Bọ xít muỗi hại chè | Côn trùng |
| `4` | **Red spider** | Nhện đỏ hại chè | Sâu hại |
| `5` | **Green mirid bug** | Bọ xít xanh | Côn trùng |
| `6` | **Healthy leaf** | Lá chè khỏe mạnh | Bình thường |

---

## 🏗️ Cấu Trúc Dự Án

```text
TeaCare/
├── app/                              # Mã nguồn ứng dụng Android
│   ├── src/main/
│   │   ├── java/com/example/teacare/
│   │   │   ├── data/                 # Room DB, Retrofit Client, Models, Repositories
│   │   │   ├── ui/                   # Jetpack Compose UI Screens, Components, Theme, Navigation
│   │   │   └── viewmodel/            # Android MVVM ViewModels
│   │   ├── res/                      # Resource layouts, mipmaps, strings, colors, network config
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── fastapi_server/                   # Backend API nhận diện (Python FastAPI)
│   ├── main.py                       # Endpoint xử lý inference (/predict, /health, /classes)
│   ├── model.py                      # Định nghĩa kiến trúc DenseNet-121 PyTorch
│   ├── classes.json                  # Metadata 7 lớp bệnh
│   ├── requirements.txt              # Thư viện Python phụ thuộc
│   └── run_server.bat                # Script chạy nhanh server trên Windows
│
├── nhandienlache/                    # Chứa trọng số mô hình đã huấn luyện
│   └── best_densenet121 (1).pth      # Checkpoint PyTorch model weights (~28.5 MB)
│
├── .gitignore
└── README.md
```

---

## 🛠️ Công Nghệ Sử Dụng

### 📱 Android Client
- **Ngôn ngữ**: Kotlin (Java 17 target).
- **Giao diện**: Jetpack Compose, Material 3, Material Icons Extended.
- **Kiến trúc**: MVVM (Model - View - ViewModel) Clean Architecture.
- **Cơ sở dữ liệu cục bộ**: Room Database + Kotlin Coroutines & Flow.
- **Mạng (Networking)**: Retrofit 2, OkHttp3 Logging Interceptor, Gson Converter.
- **Xử lý ảnh**: Coil Compose (Async Image Loading).
- **Lưu trữ cấu hình**: AndroidX DataStore Preferences.

### 🖥️ AI Backend Server
- **Framework**: FastAPI, Uvicorn, Pydantic v2.
- **AI Framework**: PyTorch (Inference mode, hỗ trợ cả CPU và CUDA GPU).
- **Xử lý ảnh**: Pillow (PIL), NumPy.

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### 1. Khởi động AI Backend (FastAPI)

#### Yêu cầu:
- Python 3.9+ (khuyên dùng Python 3.10 hoặc 3.11).

#### Cài đặt và chạy:
```powershell
# Di chuyển vào thư mục fastapi_server
cd fastapi_server

# (Khuyến nghị) Tạo và kích hoạt môi trường ảo:
python -m venv venv
.\venv\Scripts\activate

# Cài đặt các thư viện cần thiết:
pip install -r requirements.txt

# Khởi chạy server:
python main.py
# Hoặc click đúp file run_server.bat
```

Sau khi khởi chạy thành công:
- Server chạy tại: `http://127.0.0.1:8000`
- Tài liệu API tương tác (Swagger UI): `http://127.0.0.1:8000/docs`
- Kiểm tra trạng thái: `http://127.0.0.1:8000/health`

---

### 2. Cài đặt và Chạy Android App

1. Mở thư mục dự án bằng **Android Studio** (Koala / Ladybug hoặc mới hơn).
2. Chờ Android Studio đồng bộ xong **Gradle** (Sync Project with Gradle Files).
3. **Cấu hình địa chỉ IP kết nối**:
   - **Nếu dùng máy ảo Android Emulator**: 
     - Mặc định truy cập localhost của máy tính qua IP: `http://10.0.2.2:8000`
   - **Nếu dùng điện thoại thật**:
     - Kết nối điện thoại và máy tính cùng mạng Wi-Fi.
     - Lấy địa chỉ IPv4 máy tính (chạy lệnh `ipconfig` trong Command Prompt, ví dụ `192.168.1.15`).
     - Vào màn hình **Hồ sơ / Cài đặt** trong ứng dụng để nhập `http://192.168.1.15:8000/`.
4. Nhấn nút **Run** (Shift + F10) để build và cài đặt app lên thiết bị.

---

## 📡 API Endpoints Chính

| Phương thức | Endpoint | Mô tả |
|:-----------:|:---------|:------|
| `GET` | `/health` | Kiểm tra tình trạng server và kiểm tra nạp checkpoint mô hình |
| `GET` | `/classes` | Lấy danh sách 7 lớp bệnh kèm tên tiếng Việt |
| `GET` | `/model-info` | Lấy thông số kiến trúc mô hình và thông số chuẩn hóa ảnh (mean, std) |
| `POST` | `/predict` | Tải ảnh lá chè lên (`multipart/form-data`) để nhận kết quả chẩn đoán |

---

## 👨‍💻 Tác Giả & Đóng Góp

- Dự án phát triển bởi: **Huy Hoàng** ([@Hoang01012005](https://github.com/Hoang01012005))
- Mọi ý kiến đóng góp hoặc báo lỗi vui lòng tạo [Issue](https://github.com/Hoang01012005/Tea_app/issues) hoặc gửi Pull Request!
