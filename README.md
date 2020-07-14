# 개발환경

-   open jdk 1.8
-   spring boot 2.2.1 ( rest api 용)
-   mecab
    -   mecab-0.996-ko-0.9.2
    -   mecab-ko-dic-2.1.1-20180720
    -   mecab-java-0.996
# Request (rest api)
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

# Response

| Key | Description  | Type |
| ------ | ------ | ------ |
| contents | 컨텐츠 (변경된값)  | String |
| message | 성공 실패 메시지 성공일떄만 success | String |
# API 상세 설명 및 예제
상세설명 및 예제 아래 주소 참조
https://wigoai.atlassian.net/wiki/spaces/WIGO/pages/146767873/wigo-nlp-java

License
----
무료 소프트웨어입니다. GPL (the GNU General Public License), LGPL (Lesser GNU General Public License) 또는 BSD 라이선스에 따라 소프트웨어를 사용, 재배포할 수 있습니다. 자세한 내용은 COPYING, GPL, LGPL, BSD 각 파일을 참조하십시오.


