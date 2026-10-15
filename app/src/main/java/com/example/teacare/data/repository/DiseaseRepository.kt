package com.example.teacare.data.repository

import com.example.teacare.data.model.Disease
import com.example.teacare.data.model.DiseaseCategory
import com.example.teacare.data.model.DiseaseSeverity
import com.example.teacare.data.model.TeaTip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DiseaseRepository {

    // 7 lớp bệnh đồng bộ chuẩn theo bộ dữ liệu TeaLeafBD
    private val diseases = listOf(
        Disease(
            id = "algal_leaf_spot",
            vietnameseName = "Đốm Lá Do Tảo",
            commonName = "Tea algal leaf spot",
            scientificName = "Cephaleuros virescens Kunze",
            severity = DiseaseSeverity.LOW,
            category = DiseaseCategory.FUNGAL,
            shortDescription = "Vi tảo ký sinh tạo thành các đốm nhung màu cam gỉ sắt hoặc nâu đỏ gồ ghề trên phiến lá già và cành bánh tẻ.",
            symptoms = listOf(
                "Mặt trên lá xuất hiện các đốm tròn nhung mịn hơi gồ lên, ban đầu màu vàng cam như gỉ sắt.",
                "Sau một thời gian đốm chuyển dần sang màu nâu xám, biểu bì lá bên dưới bị hoại tử nhẹ.",
                "Cành non bị bao quanh bởi đốm tảo có thể bị nứt vỏ, teo tóp nếu đất quá chua và thiếu lân."
            ),
            causes = listOf(
                "Vi tảo Cephaleuros virescens phát tán qua giọt nước mưa và sương sớm.",
                "Đất nương chè nghèo kiệt dinh dưỡng, bạc màu, thoát nước kém, tán chè già cỗi."
            ),
            biologicalControl = listOf(
                "Quét vôi bột hòa nước vào gốc và thân chính sau mỗi đợt đốn tỉa chè.",
                "Bón bổ sung phân hữu cơ vi sinh, phân trùn quế và nấm rễ cộng sinh Mycorrhiza."
            ),
            chemicalControl = listOf(
                "Phun Booc-đô 1% (Bordeaux mixture) hoặc Copper Oxychloride định kỳ sau khi đốn chè.",
                "Sử dụng thuốc gốc đồng Champion 77WP theo đúng khuyến cáo."
            ),
            prevention = listOf(
                "Cải tạo đất nương chè, trồng xen cây che bóng tơi xốp đất.",
                "Tăng cường bón lân nung chảy và kali cân đối."
            ),
            optimalClimate = "Mùa hè ẩm nóng kết hợp mưa nhiều rải rác, ẩm độ > 85%"
        ),
        Disease(
            id = "brown_blight",
            vietnameseName = "Bệnh Cháy Nâu",
            commonName = "Brown Blight",
            scientificName = "Colletotrichum camelliae Massee",
            severity = DiseaseSeverity.HIGH,
            category = DiseaseCategory.FUNGAL,
            shortDescription = "Gây cháy khô mép và chóp lá non, lan nhanh thành mảng nâu đỏ sẫm khiến búp chè thui chột và rụng trơ cành.",
            symptoms = listOf(
                "Vết bệnh bắt đầu từ mép hoặc chóp lá non tạo thành mảng cháy màu nâu sẫm hoặc nâu đỏ.",
                "Ranh giới giữa mô bệnh và mô khỏe rõ rệt bằng một đường viền gồ ghề màu nâu đen đậm.",
                "Mặt trên vết bệnh khô rải rác các ổ bào tử đen li ti, lá giòn vụn khi bóp nhẹ."
            ),
            causes = listOf(
                "Nấm Colletotrichum phát triển mạnh trong điều kiện mưa nhiều, thời tiết nóng ẩm.",
                "Vết thương hái búp chưa kịp liền sẹo gặp thời tiết mưa dầm kéo dài."
            ),
            biologicalControl = listOf(
                "Phun chế phẩm sinh học nấm đối kháng Trichoderma viride.",
                "Phun dịch chiết nano bạc hoặc Chitosan tự nhiên bảo vệ biểu bì lá non."
            ),
            chemicalControl = listOf(
                "Phun thuốc trừ nấm: Chlorothalonil (Daconil 75WP), Difenoconazole + Azoxystrobin (Amistar Top 325SC).",
                "Tuân thủ nghiêm ngặt thời gian cách ly (PHI) từ 7-14 ngày trước khi thu hái."
            ),
            prevention = listOf(
                "Đào rãnh thoát nước chống ngập úng gốc chè trong mùa mưa bão.",
                "Phát dọn cỏ dại quanh tán, hái chè đúng kỹ thuật tránh dập nát búp non."
            ),
            optimalClimate = "Nhiệt độ 25-30°C, ẩm độ không khí cao > 90%"
        ),
        Disease(
            id = "gray_blight",
            vietnameseName = "Bệnh Cháy Xám",
            commonName = "Gray Blight",
            scientificName = "Pestalotiopsis theae (Sawada) Steyaert",
            severity = DiseaseSeverity.MEDIUM,
            category = DiseaseCategory.FUNGAL,
            shortDescription = "Vết bệnh xám tro hình tròn hoặc bất định có vân đồng tâm và viền nâu sẫm, làm giảm diện tích quang hợp của cây.",
            symptoms = listOf(
                "Vết bệnh ban đầu là đốm nhỏ màu nâu, sau lan rộng thành mảng màu xám tro đặc trưng.",
                "Mặt trên vết bệnh xuất hiện các vân vòng đồng tâm rõ rệt và các hạt đen nhỏ (ổ nấm).",
                "Lá chè bị bệnh trở nên khô xác, dễ rách rụng sớm làm giảm năng suất lứa búp tiếp theo."
            ),
            causes = listOf(
                "Nấm Pestalotiopsis theae xâm nhiễm qua vết thương xây xát cơ giới hoặc vết chích của bọ xít.",
                "Thời tiết ấm ẩm chuyển mùa, sương mù kết hợp nắng ấm xen kẽ."
            ),
            biologicalControl = listOf(
                "Phun vi sinh vật đối kháng Bacillus subtilis.",
                "Bổ sung phân hữu cơ vi sinh ủ hoai mục có nấm có ích."
            ),
            chemicalControl = listOf(
                "Sử dụng Mancozeb (Dithane M-45 80WP), Propineb (Antracol 70WP).",
                "Luân phiên thuốc trừ nấm có tính nội hấp theo danh mục thuốc BVTV cho phép."
            ),
            prevention = listOf(
                "Hạn chế cọ xát cơ giới khi chăm sóc và làm cỏ trên nương chè.",
                "Phun phòng trừ côn trùng chích hút để triệt tiêu cửa ngõ xâm nhập của nấm."
            ),
            optimalClimate = "Nhiệt độ ấm 22-28°C, mưa nắng xen kẽ"
        ),
        Disease(
            id = "helopeltis",
            vietnameseName = "Bọ Xít Muỗi Hại Chè",
            commonName = "Helopeltis",
            scientificName = "Helopeltis theivora Waterhouse",
            severity = DiseaseSeverity.HIGH,
            category = DiseaseCategory.PEST,
            shortDescription = "Loài côn trùng chích hút nhựa búp non và lá non chè, tiết độc tố làm khô đen búp và gây rụng lứa búp mới.",
            symptoms = listOf(
                "Búp non và lá non xuất hiện các đốm chích trong suốt, sau thâm đen và lõm xuống.",
                "Lá non bị biến dạng, mép lá co rúm, giòn gãy.",
                "Búp chè bị chích nhiều sẽ thui đen khô queo, chồi ngừng phát triển hoàn toàn."
            ),
            causes = listOf(
                "Cả sâu non và con trưởng thành Helopeltis dùng vòi chích hút dịch nhựa cây.",
                "Xuất hiện nhiều khi nương chè ra búp non rộ trong điều kiện râm mát, ẩm ướt."
            ),
            biologicalControl = listOf(
                "Bảo vệ thiên địch tự nhiên như kiến vàng, bọ ngựa, nhện bắt mồi.",
                "Phun chế phẩm sinh học Bt (Bacillus thuringiensis) hoặc dầu thảo mộc Neem oil."
            ),
            chemicalControl = listOf(
                "Thuốc sinh học gốc Abamectin hoặc Emamectin benzoate (Dylan 2EC, Tasieu 1.9EC).",
                "Chỉ phun điểm cục bộ ở nơi xuất hiện mật độ bọ xít cao, tránh phun tràn lan."
            ),
            prevention = listOf(
                "Hái búp triệt để 'san bằng mặt bằng' tán chè, phá vỡ nơi cư trú của côn trùng.",
                "Dọn sạch cỏ dại cây bụi là ký chủ phụ quanh nương đồi."
            ),
            optimalClimate = "Thời tiết âm u râm mát, sau các đợt mưa rào nhẹ"
        ),
        Disease(
            id = "red_spider",
            vietnameseName = "Nhện Đỏ Hại Chè",
            commonName = "Red spider",
            scientificName = "Oligonychus coffeae Nietner",
            severity = DiseaseSeverity.MEDIUM,
            category = DiseaseCategory.PEST,
            shortDescription = "Nhện chích hút dịch tế bào biểu bì mặt trên lá làm lá biến sang màu nâu đồng, khô cứng và rụng sớm.",
            symptoms = listOf(
                "Mặt trên lá xuất hiện các chấm vàng trắng li ti dọc theo gân chính và gân phụ.",
                "Khi mật độ cao, toàn bộ phiến lá chuyển màu nâu đồng hoặc đỏ gỉ sắt, mặt lá phủ lớp mạng tơ mỏng.",
                "Lá già và lá bánh tẻ khô giòn, giảm khả năng quang hợp và làm cây cằn cỗi."
            ),
            causes = listOf(
                "Nhện đỏ sinh sản nhanh trong điều kiện khô hạn kéo dài, nhiệt độ cao.",
                "Vườn chè thiếu nước tưới giữ ẩm, cây còi cọc thiếu dinh dưỡng."
            ),
            biologicalControl = listOf(
                "Tưới phun mưa với áp lực nước mạnh lên mặt tán để rửa trôi nhện và trứng.",
                "Sử dụng dầu khoáng nông nghiệp SK Enspray 99EC bọc màng làm nghẹt thở nhện."
            ),
            chemicalControl = listOf(
                "Sử dụng thuốc trừ nhện sinh học chuyên dụng: Hexythiazox (Nissorun), Propargite (Comite).",
                "Phun kỹ mặt trên phiến lá nơi nhện tập trung nhiều nhất."
            ),
            prevention = listOf(
                "Tưới giữ ẩm thường xuyên trong các tháng mùa khô hạn.",
                "Trồng cây che bóng làm giảm nhiệt độ và tăng ẩm độ cho đồi chè."
            ),
            optimalClimate = "Mùa khô hanh, nhiệt độ 25-32°C, lượng mưa thấp"
        ),
        Disease(
            id = "green_mirid_bug",
            vietnameseName = "Bọ Xít Xanh Hại Chè",
            commonName = "Green mirid bug",
            scientificName = "Apolygus lucorum Meyer-Dür",
            severity = DiseaseSeverity.MEDIUM,
            category = DiseaseCategory.PEST,
            shortDescription = "Bọ xít chích hút nhựa tế bào đỉnh sinh trưởng và gân lá non, làm búp queo gãy và mép lá rách tả tơi.",
            symptoms = listOf(
                "Lá non xuất hiện các đốm châm nhỏ, khi lá lớn lên các đốm này thủng rách thành lỗ bất định.",
                "Mép lá non cuộn sóng, biến dạng cong queo không đều.",
                "Búp non cằn cỗi, chồi non bị teo và chuyển màu nâu đen."
            ),
            causes = listOf(
                "Cả bọ xít non và trưởng thành chích hút dịch nhựa vào buổi sáng sớm và chiều mát.",
                "Thời tiết ấm áp chuyển mùa, nương chè có nhiều cỏ dại họ cúc xung quanh."
            ),
            biologicalControl = listOf(
                "Sử dụng nấm ký sinh côn trùng Metarhizium (nấm xanh) hoặc Beauveria bassiana.",
                "Cắt tỉa làm sạch cỏ dại ven bờ nương chè để cắt đứt nguồn thức ăn phụ."
            ),
            chemicalControl = listOf(
                "Phun thuốc sinh học chiết xuất thảo mộc gốc Matrine (Wotac 5EC).",
                "Luân phiên thuốc an toàn bảo vệ thiên địch có ích trong vườn chè."
            ),
            prevention = listOf(
                "Hái búp đúng lứa, hái sạch chồi vượt để loại bỏ nơi đẻ trứng.",
                "Bón phân cân đối N-P-K, tránh bón đạm thừa làm búp quá mềm yếu."
            ),
            optimalClimate = "Nhiệt độ 20-27°C, thời tiết nắng ráo dịu mát"
        ),
        Disease(
            id = "healthy_leaf",
            vietnameseName = "Lá Chè Khỏe Mạnh",
            commonName = "Healthy leaf",
            scientificName = "Camellia sinensis (L.) Kuntze",
            severity = DiseaseSeverity.HEALTHY,
            category = DiseaseCategory.HEALTHY,
            shortDescription = "Búp chè 'một tôm hai lá' mập mạp, màu xanh nõn chuối bóng mượt, không có vết cắn hay đốm nấm bệnh.",
            symptoms = listOf(
                "Lá chè xanh bóng tự nhiên, gân lá phân bố đều đặn và rõ nét.",
                "Búp tôm non mập mạp, có lớp lông tơ mịn trắng phủ đều đạt chuẩn thu hái.",
                "Thân cành chắc khỏe, tán phát triển tròn đều và cân đối."
            ),
            causes = listOf(
                "Quy trình canh tác đạt chuẩn VietGAP/GlobalGAP, đất thoát nước tốt.",
                "Độ ẩm đất duy trì 70-80%, ánh sáng khuếch tán lý tưởng."
            ),
            biologicalControl = listOf(
                "Duy trì thảm thực vật che phủ đất bằng cỏ lạc dại.",
                "Định kỳ bổ sung nấm rễ cộng sinh Mycorrhiza."
            ),
            chemicalControl = listOf(
                "Không cần can thiệp hóa chất bảo vệ thực vật."
            ),
            prevention = listOf(
                "Tiếp tục lịch tưới tiêu và bổ sung dinh dưỡng hữu cơ định kỳ.",
                "Kiểm tra vườn định kỳ 2 lần/tuần để phát hiện sớm bất thường."
            ),
            optimalClimate = "Nhiệt độ 18-25°C, sương sớm vừa phải, nắng nhẹ"
        )
    )

    private val teaTips = listOf(
        TeaTip(
            id = "tip_1",
            title = "Kỹ thuật đốn tỉa tán chè phục hồi sau vụ thu hoạch",
            category = "Kỹ thuật cắt tỉa",
            summary = "Đốn phớt cách vết đốn cũ 3-5cm giúp chè phân cành đồng đều, bung búp rộ vào đầu mùa tới.",
            content = "Thời điểm đốn chè thích hợp nhất là từ giữa tháng 12 đến hết tháng 1 khi cây ngừng sinh trưởng. Dùng dao đốn hoặc kéo chuyên dụng sắc bén, vết cắt nghiêng 45 độ, dọn sạch cành tăm, cành sâu bệnh bên trong lòng tán để tăng độ thông thoáng.",
            tag = "Cắt tỉa"
        ),
        TeaTip(
            id = "tip_2",
            title = "Nguyên tắc bón phân hữu cơ vi sinh đúng cách cho đồi chè",
            category = "Dinh dưỡng & Đất",
            summary = "Bón rãnh theo mép tán lá chè giúp rễ tơ hút dinh dưỡng tối đa và giữ ẩm cho đất mùa khô.",
            content = "Không bón phân sát gốc. Đào rãnh sâu 15-20cm dọc theo mép ngoài hình chiếu tán lá, bón phân chuồng ủ hoai mục kết hợp nấm Trichoderma rồi lấp đất kín. Phân hữu cơ giúp đất tơi xốp, giữ nước và nâng cao hương vị thơm ngon của búp chè thành phẩm.",
            tag = "Bón phân"
        ),
        TeaTip(
            id = "tip_3",
            title = "Phòng ngừa bọ xít muỗi và nhện đỏ bằng giải pháp sinh học",
            category = "Phòng trừ dịch hại",
            summary = "Tưới phun mưa áp lực cao và bảo vệ thiên địch kiến vàng giúp khống chế dịch hại tự nhiên.",
            content = "Vào đầu mùa khô hoặc sau các đợt mưa ẩm râm mát, việc tưới phun rửa tán cây vào sáng sớm sẽ cuốn trôi ấu trùng bọ xít muỗi và rửa sạch mạng nhện đỏ, giảm 80% nguy cơ bùng phát dịch bệnh mà không cần dùng thuốc hóa học độc hại.",
            tag = "Bảo vệ thực vật"
        ),
        TeaTip(
            id = "tip_4",
            title = "Tiêu chuẩn thu hái 'Một tôm hai ba lá non' giữ trọn hương vị",
            category = "Thu hái & Chế biến",
            summary = "Hái búp vào buổi sáng tạnh sương giúp giữ trọn vẹn tinh dầu và polyphenol quý giá trong búp chè.",
            content = "Không bấm dập nát búp, đựng vào gùi sọt thoáng khí, không nén chặt búp làm lên men ngoài ý muốn. Sau khi hái phải chuyển ngay về nơi râm mát rải mỏng chờ chế biến trong vòng 3-4 giờ.",
            tag = "Thu hái"
        )
    )

    fun getAllDiseases(): Flow<List<Disease>> = flow {
        emit(diseases)
    }

    fun getDiseaseById(id: String): Disease? {
        val cleanId = id.trim().lowercase()
        return diseases.find {
            it.id.equals(cleanId, ignoreCase = true) ||
            it.commonName.equals(cleanId, ignoreCase = true) ||
            it.vietnameseName.equals(cleanId, ignoreCase = true)
        }
    }

    fun getDiseaseByClassName(className: String): Disease {
        val clean = className.trim().lowercase()
        return diseases.find {
            it.commonName.equals(clean, ignoreCase = true) ||
            it.vietnameseName.equals(clean, ignoreCase = true) ||
            it.id.contains(clean.replace(" ", "_")) ||
            clean.contains(it.id.replace("_", " "))
        } ?: diseases.find { it.id == "algal_leaf_spot" } ?: diseases.first()
    }

    fun searchDiseases(query: String, category: DiseaseCategory? = null, severity: DiseaseSeverity? = null): List<Disease> {
        return diseases.filter { item ->
            val matchQuery = query.isBlank() ||
                    item.vietnameseName.contains(query, ignoreCase = true) ||
                    item.commonName.contains(query, ignoreCase = true) ||
                    item.scientificName.contains(query, ignoreCase = true) ||
                    item.shortDescription.contains(query, ignoreCase = true)

            val matchCategory = category == null || item.category == category
            val matchSeverity = severity == null || item.severity == severity

            matchQuery && matchCategory && matchSeverity
        }
    }

    fun getDailyTips(): List<TeaTip> = teaTips
}
