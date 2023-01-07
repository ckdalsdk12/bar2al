# bar2al
바코드로 식품의 알레르기 유발 물질을 체크하는 안드로이드 앱

## 프로그램 설명
안드로이드 기기에서 CameraX와 ML Kit을 통해 식품의 바코드를 스캔하고  
OpenAPI로 제공되는 정보를 통해 해당 식품의 알레르기 유발 물질을 체크하는 안드로이드 앱입니다.

제공하는 기능은 다음과 같습니다.

* 바코드 스캔 :  
카메라로 식품의 바코드를 스캔하고 식품에 포함되어 있는 알레르기 유발 물질을 체크하여 보여주는 기능입니다.

* 내 알레르기 설정 :  
사용자에 맞게 22가지의 알레르기 유발 물질을 각각 설정/해제할 수 있습니다.   
내 알레르기 설정에 따라 바코드 스캔 시 결과가 '나에게 해당하는 알레르기 성분'과 '그 외 포함된 알레르기 성분'으로 구분되어 표시됩니다.  

* 이전 기록 보기 :  
최근의 바코드 스캔 결과 기록을 5개까지 확인할 수 있습니다.

소스코드에서 OpenAPI 인증키 부분을 수정해야 하는 과정이 필요하므로 APK 파일이 제공되지 않고 직접 빌드 해야 합니다.

## 사용 방법
### 1. 안드로이드 스튜디오에서 프로젝트 열기
```git clone```이나 GitHub의 다운로드 기능을 사용해서 프로젝트를 다운로드한 뒤  
안드로이드 스튜디오에서 프로젝트를 열어주세요.

### 2. OpenAPI 사용 신청하기
본 앱은 3가지의 OpenAPI를 사용합니다.

* 바코드연계제품정보 : https://www.foodsafetykorea.go.kr/api/openApiInfo.do?svc_no=C005
* HACCP 제품이미지 및 포장지표기정보 : https://www.data.go.kr/data/15033307/openapi.do
* 식품(첨가물)품목제조보고(원재료) : https://www.foodsafetykorea.go.kr/api/openApiInfo.do?svc_no=C002

링크에서 OpenAPI 사용 신청을 하고 인증키를 받아주세요.

### 3. 소스코드에서 OpenAPI 인증키 부분 수정하기
ResultAL.kt 파일에서 인증키 부분을 수정해야 합니다.

```val keyId = "여기에 식품안전나라(식품의약품안전처 공공데이터활용)에서 받은 인증키를 넣으세요"``` * 2  
```val serviceKey = "serviceKey=여기에 공공데이터포털(data.go.kr)에서 받은 인증키를 넣으세요"``` * 1

총 3부분을 수정해야 합니다.

* 예시 (인증키가 ABCEDF일 경우) :  
```val keyId = "ABCDEF"```  
```val serviceKey = "serviceKey=ABCDEF"```

### 4. 프로젝트 빌드 및 APK 설치
프로젝트를 빌드하고 APK를 기기에 설치합니다.