package com.example.teacare.data.model

object TeaLeafBDClassMapper {

    /**
     * Bảng ánh xạ chính xác 7 lớp bệnh của bộ dữ liệu TeaLeafBD sang tiếng Việt.
     * Lưu ý: Ánh xạ này chỉ dùng cho mục đích hiển thị giao diện người dùng,
     * tuyệt đối không can thiệp hay làm thay đổi thứ tự nhãn mô hình AI.
     */
    private val translationMap = mapOf(
        "tea algal leaf spot" to "Đốm lá do tảo",
        "algal leaf spot" to "Đốm lá do tảo",
        "brown blight" to "Bệnh cháy nâu",
        "gray blight" to "Bệnh cháy xám",
        "grey blight" to "Bệnh cháy xám",
        "helopeltis" to "Bọ xít muỗi hại chè",
        "red spider" to "Nhện đỏ",
        "green mirid bug" to "Bọ xít xanh",
        "healthy leaf" to "Lá chè khỏe mạnh",
        "healthy" to "Lá chè khỏe mạnh"
    )

    fun toVietnameseName(englishName: String): String {
        val key = englishName.trim().lowercase()
        return translationMap[key] ?: englishName
    }

    fun isHealthy(englishName: String): Boolean {
        val key = englishName.trim().lowercase()
        return key.contains("healthy")
    }

    fun getSeverity(englishName: String): DiseaseSeverity {
        val key = englishName.trim().lowercase()
        return when {
            key.contains("healthy") -> DiseaseSeverity.HEALTHY
            key.contains("algal") || key.contains("tảo") -> DiseaseSeverity.LOW
            key.contains("gray") || key.contains("grey") || key.contains("xám") -> DiseaseSeverity.MEDIUM
            key.contains("brown") || key.contains("nâu") -> DiseaseSeverity.HIGH
            key.contains("helopeltis") || key.contains("bọ xít muỗi") -> DiseaseSeverity.HIGH
            key.contains("spider") || key.contains("nhện đỏ") -> DiseaseSeverity.MEDIUM
            key.contains("mirid") || key.contains("bọ xít xanh") -> DiseaseSeverity.MEDIUM
            else -> DiseaseSeverity.MEDIUM
        }
    }

    fun getQuickSummary(englishName: String): String {
        val key = englishName.trim().lowercase()
        return when {
            key.contains("healthy") ->
                "Lá chè xanh mướt, biểu bì bóng mịn, không có dấu hiệu nấm hoặc côn trùng gây hại."
            key.contains("algal") ->
                "Do vi tảo ký sinh tạo thành các đốm nhung màu cam gỉ sắt hoặc nâu đỏ trên phiến lá."
            key.contains("brown") ->
                "Vết bệnh bắt đầu từ mép hoặc chóp lá tạo thành mảng cháy màu nâu sẫm, làm khô búp."
            key.contains("gray") || key.contains("grey") ->
                "Xuất hiện đốm xám tro có viền nâu đỏ đồng tâm trên phiến lá bánh tẻ, làm giòn rách lá."
            key.contains("helopeltis") ->
                "Bọ xít muỗi chích hút búp non tiết độc tố làm khô đen búp chè và queo ngọn."
            key.contains("spider") ->
                "Nhện đỏ chích hút dịch mặt trên phiến lá tạo các chấm vàng li ti, lá chuyển màu đồng."
            key.contains("mirid") ->
                "Bọ xít xanh chích hút nhựa búp và cuống lá non làm mép lá xoăn gợn sóng, búp cằn cỗi."
            else -> "Phát hiện dấu hiệu bất thường trên phiến lá chè."
        }
    }

    fun getQuickTreatment(englishName: String): String {
        val key = englishName.trim().lowercase()
        return when {
            key.contains("healthy") ->
                "Duy trì chế độ tưới ẩm và bổ sung phân hữu cơ vi sinh định kỳ."
            key.contains("algal") ->
                "Quét vôi gốc sau khi đốn chè, phun Booc-đô 1% hoặc thuốc gốc đồng bảo vệ tán."
            key.contains("brown") ->
                "Phun thuốc trừ nấm Chlorothalonil hoặc Difenoconazole, cắt tỉa cành bệnh dọn sạch nương."
            key.contains("gray") || key.contains("grey") ->
                "Phun Mancozeb hoặc Antracol 70WP, hạn chế gây xây xát khi thu hái búp."
            key.contains("helopeltis") ->
                "Phun chế phẩm sinh học Bt, dầu neem hoặc Emamectin benzoate vào buổi sáng sớm."
            key.contains("spider") ->
                "Tưới phun mưa rửa trôi nhện trong mùa khô hạn, dùng dầu khoáng SK Enspray 99EC."
            key.contains("mirid") ->
                "Hái búp chè triệt để phá vỡ nơi cư trú, phun thuốc sinh học thảo mộc gốc Matrine."
            else -> "Thăm vườn thường xuyên và tham khảo ý kiến cán bộ bảo vệ thực vật địa phương."
        }
    }
}
