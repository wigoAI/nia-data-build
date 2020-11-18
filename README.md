# 개발환경
-   open jdk 1.8
-   spring boot 2.3.5 ( rest api 용)
-   mecab
    -   mecab-0.996-ko-0.9.2
    -   mecab-ko-dic-2.1.1-20180720
    -   mecab-java-0.996  
- 의존성 정의는 build.gradle 파일 참조


# 개요
### 원천 데이터 관련
- 데이터 정제 원천데이트는 비플라이소프트에서 제공하는 데이터를 활용합니다. 이프로젝트는 데이터 요약에 사용할 수 있는 데이터 정제 프로젝트 입니다
    - 원천 데이터 문의: http://www.bflysoft.co.kr/

### 오픈소스 관리 관련
- moara-core 프로젝트의 문장 구분기를 사용하였는데 이부분은 오픈소스화가 되어있지 않아서 mavenCentral 에 올리지는 않았습니다. 
- 한국어 자연어 처리 yido 프로젝트가 오픈소스로 진행되고 있는데 관련 부분이 완성되면 mavenCentral 에 올릴 수 있는 빌트환경이 구성 됩니다.
    - https://github.com/wigoAI/yido
    
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

## 정제 데이터
### 강원도민일보.json 중 일부
```json
{
  "name": "강원도민일보.20200720_161845_6349건_",
  "delivery_date": "2020-10-15 17:36:56",
  "documents": [
    {
      "id": "329480480",
      "category": "종합",
      "media_type": "online",
      "media_sub_type": "지역지",
      "media_name": "강원도민일보",
      "size": "large",
      "char_count": "1822",
      "publish_date": "2019-01-01 11:12:15",
      "title": "\"해가 떴다! 2019년이 밝았다\"...동해안 해돋이명소에 인파 몰려",
      "text": [
        [
          {
            "index": 0,
            "sentence": "▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다.",
            "highlight_indices": "35,36"
          },
          {
            "index": 1,
            "sentence": "60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다.",
            "highlight_indices": "27,28"
          }
        ],
        [
          {
            "index": 2,
            "sentence": "새해 첫해는 7시 33분 울산 간절곶을 시작으로 동해안 수평선 위로 힘차게 솟았다.",
            "highlight_indices": "29,30"
          }
        ],
        [...중략...],
        [
          {
            "index": 26,
            "sentence": "가족과 함께 행사장을 찾은 김병우(68·보은군 보은읍)씨는 \"60년 만에 맞는 황금 돼지해인 만큼 가족 모두 건강하고, 사회 전반에 희망이 넘치기 바란다\"고 기원했다.",
            "highlight_indices": "4,6;58,60"
          }
        ],
        [
          {
            "index": 27,
            "sentence": "제주 성산일출봉 정상에도 500명이 올라 첫 태양을 기다렸으나 아쉽게도 구름 많고 흐린 날씨 탓에 해돋이는 볼 수 없었다.",
            "highlight_indices": "23,24"
          }
        ]
      ]
    }
   ]
}
```
전처리가 적용된 파일의 형태는 원본 문서의 정보와 문서의 내용을 문단 및 문장 단위로 구분한 정보이다.  

전처리시 이후에 데이터가 사용되는 인공지능 학습에 필요가 없는 기자정보, 헤드라인, 이미지 정보 등등 필요 없는 정보들이 제거된다.

## 정제 과정

### 1. 1차 전처리
원본 데이터의 타입에 맞는 전처리기의 객체를 생성하여 전처리를 수행한다. 파일이 존재하는 경로를 통해 다수의 파일을 동시에 처리 가능하다.  

처리가 끝난 뒤에는 입력한 경로 내부에 `json`이라는 디렉터리를 생성하고 해당 디렉터리에 결과물을 생성한다.

```java
// XML 형태의 데이터 전처리
// XML 데이터같은 경우 원본에서 한 파일당 한개의 데이터만 포함하는 경우가 많았기 때문에 출력 경로를 별도로 지정해줌
// String outputPath = "D:\\moara\\data\\law\\test"; 
// DataPreprocessor dataPreprocessor = new XmlPreprocessor(outputPath);

// txt 형태의 데이터 전처리
// DataPreprocessor dataPreprocessor = new TextPreprocessor();

// exel 형태의 데이터 
DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

String dirPath ="D:\\moara\\data\\기고문_2차\\";
dataPreprocessor.makeByPath(dirPath);
```


### 2. 2차 전처리
1차 전처리가 끝난 파일에 대해서 해당 데이터에 추가적이 정제가 필요한 경우 2차 전처리를 수행한다.  

2차 전처리에는 추가 정제, 문서 분류, 하이라이팅 등이 있다.

#### 추가 정제

```java
String path = "D:\\moara\\data\\기고문_2차\\json\\";
JsonFileEditor jsonFileEditor = new JsonFileEditor();

jsonFileEditor.editJsonFileByPath(path);
```

#### 문서 분류
```java
String path = "D:\\moara\\data\\기고문_2차\\json\\";

JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();

jsonFileClassifier.classifyJsonFileByPath(path);
```

#### 하이라이팅
```java
JsonFileEditor jsonFileEditor = new JsonFileHighlighter();

String path = "D:\\moara\\data\\기고문_2차\\json\\edit\\new\\";
List<File> fileList = FileUtil.getFileList(path, ".json");

for(File file : fileList) {
    jsonFileEditor.editJsonFile(file, path);
}

```
