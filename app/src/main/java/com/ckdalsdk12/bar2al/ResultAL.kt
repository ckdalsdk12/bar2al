package com.ckdalsdk12.bar2al

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import com.ckdalsdk12.bar2al.databinding.ActivityResultAlBinding
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "ResultAL"

class ResultAL : AppCompatActivity() {

    private lateinit var barcodeValue : String
    private lateinit var binding : ActivityResultAlBinding
    private var prno : String = ""
    private var errorFlag : Int = 0
    private var allAllergyThing : String = ""
    private var allAllergyThingSplit : MutableList<String> = mutableListOf()
    private var matchedAL : MutableSet<String> = mutableSetOf()
    private var elseAL : MutableSet<String> = mutableSetOf()
    private var productName : String = ""
    private var productImageURL : String = ""
    private var allMaterial : String = ""
    private var allMaterialSplit : MutableList<String> = mutableListOf()

    private var egg = false
    private var milk = false
    private var buckwheat = false
    private var sulfurousAcid = false
    private var soybean = false
    private var pineNut = false
    private var peach = false
    private var peanut = false
    private var wheat = false
    private var chubMackerel = false
    private var crab = false
    private var shrimp = false
    private var pork = false
    private var tomato = false
    private var walnut = false
    private var chicken = false
    private var beef = false
    private var squid = false
    private var shellfish = false
    private var abalone = false
    private var oyster = false
    private var mussel = false

    // bool 변수의 T or F를 판별하여 String을 리스트에 추가하는 메소드
    fun boolCheck(boolThing: Boolean, stringThing: String) {
        if (boolThing)
            matchedAL.add(stringThing)
        else
            elseAL.add(stringThing)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.comparisonMyAL.movementMethod = ScrollingMovementMethod()
        binding.elseAL.movementMethod = ScrollingMovementMethod()

        val sharedPref = getSharedPreferences("MyAL", Context.MODE_PRIVATE)
        egg = sharedPref.getBoolean("egg", false)
        milk = sharedPref.getBoolean("milk", false)
        buckwheat = sharedPref.getBoolean("buckwheat", false)
        sulfurousAcid = sharedPref.getBoolean("sulfurousAcid", false)
        soybean = sharedPref.getBoolean("soybean", false)
        pineNut = sharedPref.getBoolean("pineNut", false)
        peach = sharedPref.getBoolean("peach", false)
        peanut = sharedPref.getBoolean("peanut", false)
        wheat = sharedPref.getBoolean("wheat", false)
        chubMackerel = sharedPref.getBoolean("chubMackerel", false)
        crab = sharedPref.getBoolean("crab", false)
        shrimp = sharedPref.getBoolean("shrimp", false)
        pork = sharedPref.getBoolean("pork", false)
        tomato = sharedPref.getBoolean("tomato", false)
        walnut = sharedPref.getBoolean("walnut", false)
        chicken = sharedPref.getBoolean("chicken", false)
        beef = sharedPref.getBoolean("beef", false)
        squid = sharedPref.getBoolean("squid", false)
        shellfish = sharedPref.getBoolean("shellfish", false)
        abalone = sharedPref.getBoolean("abalone", false)
        oyster = sharedPref.getBoolean("oyster", false)
        mussel = sharedPref.getBoolean("mussel", false)

        // 뒤로가기 버튼을 누르면 Scan_Barcode 액티비티 실행 및 현 액티비티 종료
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            @SuppressLint("UnsafeOptInUsageError")
            override fun handleOnBackPressed() {
                val intent = Intent(this@ResultAL, BarcodeScanner::class.java)
                startActivity(intent)
                finish()
            }
        })

        // 만약 이전 액티비티에서 전달해준 barcodeValue라는 이름의 value가 있으면
        // barcodeValue 변수에 value 저장
        if (intent.hasExtra("barcodeValue")) {
            barcodeValue = intent.getStringExtra("barcodeValue").toString()
        }

        // 바코드로 품목보고번호를 받아오는 과정(첫 번째 스레드) 실행
        val firstThread = BarcodeToPrnoThread()
        firstThread.start()
        firstThread.join()

        // 바코드로 품목보고번호를 받아오는 과정(첫 번째 스레드)에서 에러가 없었으면 실행
        // 품목보고번호로 알레르기 유발 물질 목록을 받아오는 과정(두 번째 스레드) 실행
        if (errorFlag == 0) {
            val secondThread = PrnoToALThread()
            secondThread.start()
            secondThread.join()
        }

        // 품목보고번호로 알레르기 유발 물질 목록을 받아오는 과정(두 번째 스레드)에서
        // 제품에 대한 데이터가 없거나 알레르기 정보가 없을 경우
        // 대체 OpenAPI에서 원재료명을 받아오는 과정(세 번째 스레드) 실행
        if (errorFlag == 2) {
            binding.productName.text = "검색 중"
            binding.progressBar.visibility = View.VISIBLE
            binding.productImage.visibility = View.GONE
            if (productImageURL != "")
                binding.productImage.visibility = View.VISIBLE
            val thirdThread = AlternateApiThread()
            thirdThread.start()
            // thirdThread.join()
        }

        // 전체 과정에서 에러가 없었을 경우
        if (errorFlag == 0) {
            // boolCheck 메소드를 사용하여 식품의 알레르기 유발 물질이 사용자에게 해당하는지 판별
            for (i in allAllergyThingSplit.indices) {
                when (allAllergyThingSplit[i]) {
                    "난류" ->
                        boolCheck(egg, allAllergyThingSplit[i])
                    "우유" ->
                        boolCheck(milk, allAllergyThingSplit[i])
                    "메밀" ->
                        boolCheck(buckwheat, allAllergyThingSplit[i])
                    "아황산류" ->
                        boolCheck(sulfurousAcid, allAllergyThingSplit[i])
                    "대두" ->
                        boolCheck(soybean, allAllergyThingSplit[i])
                    "잣" ->
                        boolCheck(pineNut, allAllergyThingSplit[i])
                    "복숭아" ->
                        boolCheck(peach, allAllergyThingSplit[i])
                    "땅콩" ->
                        boolCheck(peanut, allAllergyThingSplit[i])
                    "밀" ->
                        boolCheck(wheat, allAllergyThingSplit[i])
                    "고등어" ->
                        boolCheck(chubMackerel, allAllergyThingSplit[i])
                    "게" ->
                        boolCheck(crab, allAllergyThingSplit[i])
                    "새우" ->
                        boolCheck(shrimp, allAllergyThingSplit[i])
                    "돼지고기" ->
                        boolCheck(pork, allAllergyThingSplit[i])
                    "토마토" ->
                        boolCheck(tomato, allAllergyThingSplit[i])
                    "호두" ->
                        boolCheck(walnut, allAllergyThingSplit[i])
                    "닭고기" ->
                        boolCheck(chicken, allAllergyThingSplit[i])
                    "쇠고기" ->
                        boolCheck(beef, allAllergyThingSplit[i])
                    "오징어" ->
                        boolCheck(squid, allAllergyThingSplit[i])
                    "조개류" ->
                        boolCheck(shellfish, allAllergyThingSplit[i])
                    "전복" ->
                        boolCheck(abalone, allAllergyThingSplit[i])
                    "굴" ->
                        boolCheck(oyster, allAllergyThingSplit[i])
                    "홍합" ->
                        boolCheck(mussel, allAllergyThingSplit[i])
                }
            }
        }

        // 전체 과정에서 에러가 없었을 경우
        if (errorFlag == 0) {
            // 판별 완료 후 리스트를 ,로 구분한 스트링으로 반환하여 화면에 표시
            var matchedALString: String = matchedAL.joinToString(", ")
            var elseALString: String = elseAL.joinToString(", ")
            matchedALString = "나에게 해당하는 알레르기 성분\n" + matchedALString
            elseALString = "그 외 포함된 알레르기 성분\n" + elseALString
            binding.comparisonMyAL.text = matchedALString
            binding.elseAL.text = elseALString

            // DB 접근
            val logDB = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "LogDatabase"
            ).build()

            // DB의 Dao 객체 생성
            val logDatabaseDao = logDB.logDatabaseDao()

            // 타임스탬프를 포함하여 DB에 저장할 하나의 인스턴스를 만듬
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)
            val log = LogDatabase(0, formatted, productName, matchedALString, elseALString,
                productImageURL)

            // DB에 인스턴스를 저장하고 만약 로그 개수가 기준치를 넘으면 오래된 로그 제거하는 스레드
            val thread = Thread {
                logDatabaseDao.insertLog(log)
                logDatabaseDao.deleteOldLog()
            }
            thread.start()
            thread.join()

            // 사진 받아오기
            Glide.with(this).load(productImageURL).into(binding.productImage)
        }
    }

    // 바코드로 품목보고번호를 가져오는 스레드 구현
    inner class BarcodeToPrnoThread: Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val keyId = "여기에 식품안전나라(식품의약품안전처 공공데이터활용)에서 받은 인증키를 넣으세요"
            val serviceId = "C005"
            val dataType = "json"
            val startIdx = "1"
            val endIdx = "1"
            val barCd = "BAR_CD=$barcodeValue"
            val apiUrl = "https://openapi.foodsafetykorea.go.kr/api/" +
                    "$keyId/$serviceId/$dataType/$startIdx/$endIdx/$barCd"

            // Log.d(TAG, "$apiUrl") // 디버깅 용

            val url = URL(apiUrl) // URL 객체 생성
            val conn = url.openConnection() // 연결 생성
            val input = conn.getInputStream() // 응답 받기
            val isr = InputStreamReader(input, "UTF-8") // ISR 객체로 응답 읽기

            // Gson으로 barcodeToPrnoData 객체에 역직렬화
            val barcodeToPrnoData:BarcodeToPrnoData =
                Gson().fromJson(isr, BarcodeToPrnoData::class.java)

            // totalCount를 통해 데이터 유효성 체크
            if (barcodeToPrnoData.c005.totalCount == 0) {
                errorFlag = 1
                binding.productName.text = "바코드가 잘못 인식되었거나\n서버에 데이터가 없습니다."
                binding.productImage.visibility = View.GONE
            }
            // 데이터가 유효하면 제품명과 품목보고번호 가져오기
            else {
                productName = barcodeToPrnoData.c005.row[0].prdlstNm
                binding.productName.text = productName
                prno = barcodeToPrnoData.c005.row[0].prdlstReportNo
            }
        }
    }

    // 품목보고번호로 알레르기 유발 물질을 가져오는 스레드 구현
    inner class PrnoToALThread: Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val serviceKey = "serviceKey=여기에 공공데이터포털(data.go.kr)에서 받은 인증키를 넣으세요"
            val prdlstReportNo = "prdlstReportNo=$prno"
            val returnType = "returnType=json"
            val pageNo = "pageNo=1"
            val numOfRows = "numOfRows=1"
            val apiUrl = "http://apis.data.go.kr/B553748/CertImgListService/getCertImgListService?"+
                    "$serviceKey&$prdlstReportNo&$returnType&$pageNo&$numOfRows"

            // Log.d(TAG, "$apiUrl") // 디버깅 용

            val url = URL(apiUrl) // URL 객체 생성
            val conn = url.openConnection() // 연결 생성
            val input = conn.getInputStream() // 응답 받기
            val isr = InputStreamReader(input, "UTF-8") // ISR 객체로 응답 읽기
            
            // Gson으로 prnoToALData 객체에 역직렬화
            val prnoToALData:PrnoToALData = Gson().fromJson(isr, PrnoToALData::class.java)

            // totalCount를 통해 데이터 유효성 체크
            if (prnoToALData.body.totalCount == 0) {
                errorFlag = 2
                binding.productName.text = "바코드가 잘못 인식되었거나\n서버에 데이터가 없습니다."
                binding.productImage.visibility = View.GONE
            }
            // 데이터가 유효하면 제품 이미지 URL과 알레르기 정보 가져오기
            else {
                productImageURL = prnoToALData.body.items[0].item.imgurl1
                allAllergyThing = prnoToALData.body.items[0].item.allergy
                binding.comparisonMyAL.text = allAllergyThing
                // 알레르기 정보 유효성 체크
                when (allAllergyThing) {
                    "" -> errorFlag = 2
                    "알수없음" -> errorFlag = 2
                }
            }

            // 받아온 String에서 뒤에 붙어 있는 " 함유"를 제거하고 ,로 split 함
            allAllergyThing = allAllergyThing.removeSuffix(" 함유")
            allAllergyThingSplit = allAllergyThing.split(",") as MutableList<String>
        }
    }

    // 대체 OpenAPI에서 품목보고번호로 알레르기 유발 물질을 가져오는 스레드 구현
    inner class AlternateApiThread: Thread() {
        @SuppressLint("SetTextI18n")
        override fun run() {
            val keyId = "여기에 식품안전나라(식품의약품안전처 공공데이터활용)에서 받은 인증키를 넣으세요"
            val serviceId = "C002"
            val dataType = "json"
            val startIdx = "1"
            val endIdx = "1"
            val prdlstReportNo = "PRDLST_REPORT_NO=$prno"
            val apiUrl = "https://openapi.foodsafetykorea.go.kr/api/" +
                    "$keyId/$serviceId/$dataType/$startIdx/$endIdx/$prdlstReportNo"

            // Log.d(TAG, "$apiUrl") // 디버깅 용

            val url = URL(apiUrl) // URL 객체 생성
            val conn = url.openConnection() // 연결 생성
            val input = conn.getInputStream() // 응답 받기
            val isr = InputStreamReader(input, "UTF-8") // ISR 객체로 응답 읽기

            // Gson으로 alternateApiData 객체에 역직렬화
            val alternateApiData:AlternateApiData =
                Gson().fromJson(isr, AlternateApiData::class.java)

            // totalCount를 통해 데이터 유효성 체크
            if (alternateApiData.c002.totalCount == 0) {
                errorFlag = 3
                binding.productName.text = "바코드가 잘못 인식되었거나\n서버에 데이터가 없습니다."
                binding.productImage.visibility = View.GONE
            }
            // 데이터가 유효하면 경고 문구 추가 및 원재료들 가져오기
            else {
                runOnUiThread {
                    binding.productName.text = productName + "\n\n주의!\n" +
                            "정확하지 않은\n알레르기 성분 정보일 수 있습니다."
                }
                allMaterial = alternateApiData.c002.row[0].rawmtrlNm
            }

            // 원재료들이 한개의 String에 적혀있으므로 여러개의 String으로 split
            allMaterialSplit = allMaterial.split(",") as MutableList<String>

            // split된 String들이 모여있는 리스트를 사용하여
            // 식품의 알레르기 유발 물질이 사용자에게 해당하는지 판별
            for (i in allMaterialSplit.indices) {
                arrayOf("달걀", "계란", "메추리알", "커스터드", "머랭", "난백", "황백", "난각", "난황",
                "마시멜로", "레시틴", "전란액").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(egg, "난류")
                }
                arrayOf("우유", "젖", "분유", "치즈", "요거트", "요구르트", "카제인", "버터", "밀크",
                    "유당", "유청", "마가린", "크림", "커스터드", "연유", "원유", "커드").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(milk, "우유")
                }
                arrayOf("메밀").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(buckwheat, "메밀")
                }
                arrayOf("아황산", "햄", "소시지").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(sulfurousAcid, "아황산류")
                }
                arrayOf("대두", "식물성유지", "콩", "두부", "두유", "간장", "된장", "구아검",
                    "잔탄검", "레시틴").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(soybean, "대두")
                }
                arrayOf("잣", "송자", "백자", "실백").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(pineNut, "잣")
                }
                arrayOf("복숭아", "백도", "황도").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(peach, "복숭아")
                }
                arrayOf("사과", "자두", "체리").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(peach, "$it(복숭아 교차반응 확률 55%)")
                }
                if (allMaterialSplit[i] == "배")
                    boolCheck(wheat, allMaterialSplit[i]+"(복숭아 교차반응 확률 55%)")
                arrayOf("땅콩").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(peanut, "땅콩")
                }
                if (allMaterialSplit[i] == "밀")
                     boolCheck(wheat, "밀")
                arrayOf("밀가루", "통밀", "소맥", "맥아", "잔탄검", "중력분", "박력분", "강력분", "글루텐").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(wheat, "밀")
                }
                arrayOf("보리, 호밀").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(wheat, "$it(밀 교차반응 확률 20%)")
                }
                arrayOf("고등어").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(chubMackerel, "고등어")
                }
                arrayOf("게").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(crab, "게")
                }
                arrayOf("새우", "쉬림프", "슈림프", "가재").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(crab, "$it(게 교차반응 확률 75%)")
                }
                arrayOf("새우", "쉬림프", "슈림프").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(shrimp, "새우")
                }
                arrayOf("게", "가재").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(shrimp, "$it(새우 교차반응 확률 75%)")
                }
                arrayOf("돼지", "포크", "돈육", "햄", "스팸", "족발", "돈족", "젤라틴", "돈지", "라드",
                "베이컨").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(pork, "돼지고기")
                }
                arrayOf("토마토", "케찹", "케첩", "로제").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(tomato, "토마토")
                }
                arrayOf("호두").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(walnut, "호두")
                }
                arrayOf("헤이즐넛", "캐슈넛", "브라질넛").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(walnut, "$it(호두 교차반응 확률 37%)")
                }
                arrayOf("닭", "치킨", "계육", "텐더", "너겟").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(chicken, "닭고기")
                }
                arrayOf("쇠고기", "소고기", "비프", "우육", "양지", "사골", "우족", "소곱창").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(beef, "쇠고기")
                }
                arrayOf("오징어").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(squid, "오징어")
                }
                arrayOf("조개", "바지락", "대하", "백합", "동죽", "꼬막", "전복", "굴", "홍합",
                    "관자").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(shellfish, "조개류")
                }
                arrayOf("전복").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(abalone, "전복")
                }
                arrayOf("굴").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(oyster, "굴")
                }
                arrayOf("홍합").forEach {
                    if (allMaterialSplit[i].contains(it))
                        boolCheck(mussel, "홍합")
                }
                arrayOf("해산물", "해물", "시푸드", "씨푸드").forEach {
                    if (allMaterialSplit[i].contains(it)) {
                        boolCheck(squid, "오징어")
                        boolCheck(shellfish, "조개류")
                        boolCheck(abalone, "전복")
                        boolCheck(oyster, "굴")
                        boolCheck(mussel, "홍합")
                    }
                }
                arrayOf("육류", "동물성", "육수").forEach {
                    if (allMaterialSplit[i].contains(it)) {
                        boolCheck(beef, "쇠고기")
                        boolCheck(chicken, "닭고기")
                        boolCheck(pork, "돼지고기")
                    }
                }
                arrayOf("견과류").forEach {
                    if (allMaterialSplit[i].contains(it)) {
                        boolCheck(walnut, "호두")
                        boolCheck(peanut, "땅콩")
                        boolCheck(pineNut, "잣")
                    }
                }
                arrayOf("갑각류").forEach {
                    if (allMaterialSplit[i].contains(it)) {
                        boolCheck(crab, "게")
                        boolCheck(shrimp, "새우")
                    }
                }
            }

            // 판별된 알레르기 유발 물질을 UI에 반영
            var matchedALString: String = matchedAL.joinToString(", ")
            var elseALString: String = elseAL.joinToString(", ")
            matchedALString = "나에게 해당하는 알레르기 성분\n" + matchedALString
            elseALString = "그 외 포함된 알레르기 성분\n" + elseALString
            runOnUiThread {
                binding.comparisonMyAL.text = matchedALString
                binding.elseAL.text = elseALString
            }

            // DB 접근
            val logDB = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "LogDatabase"
            ).build()

            // DB의 Dao 객체 생성
            val logDatabaseDao = logDB.logDatabaseDao()

            // 타임스탬프를 포함하여 DB에 저장할 하나의 인스턴스를 만듬
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formatted = current.format(formatter)
            val log = LogDatabase(0, formatted, productName, matchedALString, elseALString,
                productImageURL)

            // DB에 인스턴스를 저장하고 만약 로그 개수가 기준치를 넘으면 오래된 로그 제거하는 스레드
            val thread = Thread {
                logDatabaseDao.insertLog(log)
                logDatabaseDao.deleteOldLog()
            }
            thread.start()
            thread.join()
            runOnUiThread {
                binding.progressBar.progress = 100
                binding.progressBar.visibility = View.GONE
                if (productImageURL != "") {
                    Glide.with(this@ResultAL).load(productImageURL)
                        .into(binding.productImage)
                }
            }
        }
    }
}
