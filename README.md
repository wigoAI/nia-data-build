# 개발환경
-   open jdk 1.8
-   spring boot 2.2.1 ( rest api 용)
-   mecab
    -   mecab-0.996-ko-0.9.2
    -   mecab-ko-dic-2.1.1-20180720
    -   mecab-java-0.996  
    
# REST API
## Request
-   필수여부가 N 인 목록은 결과 테스트용으로 실 운영에는 설정 값 사용을 권장함(전송하지 않으면 설정 값 사용)
-   method: post
-   address/stopword/wordclass/highlight
-   request param

| Key | Description | 필수여부 | Type | Child Type | 기본값 | 
| ------ | ------ | ------ | ------ | ------ | ------ |
| contents | 컨텐츠 (내용) | Y | String |
| out_array | 제외 하고 하는 품사 배열 시작 문자열 | N | Json Array | String | app.yml 설정 값
| start_tag | 시작 테그 | N | String |  | app.yml 설정 값
| end_tag | 끝 테그 | N | String |  | app.yml 설정 값

## Response

| Key | Description  | Type |
| ------ | ------ | ------ |
| contents | 컨텐츠 (변경된값)  | String |
| message | 성공 실패 메시지 성공일떄만 success | String |
## API 상세 설명 및 예제
상세설명 및 예제 아래 주소 참조
https://wigoai.atlassian.net/wiki/spaces/WIGO/pages/146767873/wigo-nlp-java

# Preprocessing
Bflysoft에서 제공되는 데이터를 json형태의 전처리 파일로 정제한다.

## 원문 데이터
### 강원도민일보.xlsx 중 일부
|기사번호|매체명|매체유형|매체구분|발행일시|기사카테고리|기사대중소|기사본문글자수|제목|내용|
|-----|-----|-----|-----|-----|-----|-----|-----|-----|-----|
|329480480|강원도민일보|온라인|지역지|2019-01-01 11:12:15|종합|대|1822|"해가 떴다! 2019년이 밝았다"...동해안 해돋이명소에 인파 몰려|가족·연인 "좋은 날만 가득하길" 기원, 정동진·간절곶 인산인해 \r\n\r\n▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다. 2019.1.1\r\n60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다. \r\n\r\n새해 첫해는 7시 33분 울산 간절곶을 시작으로 동해안 수평선 위로 힘차게 솟았다.\r\n\r\n ... 중략 ...가족과 함께 행사장을 찾은 김병우(68·보은군 보은읍)씨는 "60년 만에 맞는 황금 돼지해인 만큼 가족 모두 건강하고, 사회 전반에 희망이 넘치기 바란다"고 기원했다.\r\n\r\n제주 성산일출봉 정상에도 500명이 올라 첫 태양을 기다렸으나 아쉽게도 구름 많고 흐린 날씨 탓에 해돋이는 볼 수 없었다.\r\n\r\n|

