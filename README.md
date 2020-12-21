# 개발환경

- open jdk 1.8
- mecab
    - mecab-0.996-ko-0.9.2
    - mecab-ko-dic-2.1.1-20180720
    - mecab-java-0.996
- 의존성 정의는 build.gradle 파일 참조

# 개요

### 원천 데이터 관련

- 데이터 정제 원천데이트는 비플라이소프트에서 제공하는 데이터를 활용합니다. 이프로젝트는 데이터 요약에 사용할 수 있는 데이터 정제 프로젝트 입니다
    - 원천 데이터 문의: http://www.bflysoft.co.kr/

### 오픈소스 관리 관련

- moara-core 프로젝트의 문장 구분기를 사용하였는데 이부분은 오픈소스화가 되어있지 않아서 mavenCentral 에 올리지는 않았습니다.
- 한국어 자연어 처리 yido 프로젝트가 오픈소스로 진행되고 있는데 관련 부분이 완성되면 mavenCentral 에 올릴 수 있는 빌드환경이 구성 됩니다.
    - https://github.com/wigoAI/yido

# Preprocessing

Bflysoft에서 제공되는 데이터를 json형태의 전처리 파일로 정제한다.

## 원문 데이터

### 강원도민일보.xlsx 중 일부

#### 문서 정보

|기사번호|매체명|매체유형|매체구분|발행일시|기사카테고리|기사대중소|기사본문글자수|제목|  
|-----|-----|-----|-----|-----|-----|-----|-----|-----|    
|329480480|강원도민일보|온라인|지역지|2019-01-01 11:12:15|종합|대|1822|"해가 떴다! 2019년이 밝았다"...동해안 해돋이명소에 인파 몰려|  

#### 문서 내용

`가족·연인 "좋은 날만 가득하길" 기원, 정동진·간절곶 인산인해 \r\n\r\n▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다. 2019.1.1\r\n60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다. \r\n\r\n새해 첫해는 7시 33분 울산 간절곶을 시작으로 동해안 수평선 위로 힘차게 솟았다.\r\n\r\n ... 중략 ...가족과 함께 행사장을 찾은 김병우(68·보은군 보은읍)씨는 "60년 만에 맞는 황금 돼지해인 만큼 가족 모두 건강하고, 사회 전반에 희망이 넘치기 바란다"고 기원했다.\r\n\r\n제주 성산일출봉 정상에도 500명이 올라 첫 태양을 기다렸으나 아쉽게도 구름 많고 흐린 날씨 탓에 해돋이는 볼 수 없었다.\r\n\r\n`

## 정제 과정
### 문장과 토큰 출력
```java
class TextMining{
    public void make(File file) {
        /*  데이터 정보 추출 생략  */

        String contents = getCellValue(9); // excel 데이터에서 컨텐츠 추출
        String documentType = "news_paragraph"; // 문단 단위로 구분을 수행하기 위해서는 documentType이 "_paragraph"로 끝나야 한다.
        
        Document document = new Document(); // 데이터를 주입 할 Document 객체 생성
        
        // document 데이터 주입
        document.setType(documentType);
        document.setContents(contents);

        // 데이터 정제 결과 객체를 생성
        // 문단 단위로 결과를 출력하기 위해 documentMiningParagraph 사용
        DocumentMiningParagraph documentMining;
        
        // 데이터 정제 실행 및 결과 주입
        documentMining = (DocumentMiningParagraph) DocumentMiningFactory.newDocumentMining(document);

        // 정제 결과에서 문단 추출
        Paragraph[] paragraphs = documentMining.miningParagraph();
        Paragraph paragraph = paragraphs[0];
        
        
        // 문단 내용 출력
        String paragraphContents = paragraph.getContents();
        System.out.println("Paragraph : " + paragraphContents);

        // 문단 내부에서 구분된 문장 추출
        for (Sentence sentence : paragraph.getSentences()) {
            
            // 문장 내용 출력
            System.out.println("Sentence : " + sentence.getContents());

            // 문장 토큰 추출
            WordToken[] wordTokens = sentence.getTokens();

            
            StringBuilder indexBuilder = new StringBuilder();

            outer:
            for(WordToken wordToken: wordTokens){

                // 품사 토큰 획득
                String partOfSpeech = wordToken.getPartOfSpeech();

                // 선택한 품사에 대한 토큰 획득
                // String[] outArray = {"M"}
                for(String out : outArray){
                    if(partOfSpeech.startsWith(out)){
                        indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
                        continue outer;
                    }
                }
            }

            // 해당 문장에 토큰이 존재할 경우 출력
            if (indexBuilder.length() > 0) {
                System.out.println("Highlight : " + indexBuilder.substring(1));
            } else {
                System.out.println("Highlight :");
            }
        }
    }
}

```

### 결과
```text
Paragraph : ▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다. 2019.1.1\r\n60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다. \r\n\r\n
Sentence : ▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다.
Highlight : 35,36
Sentence : 2019.1.1\r\n60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다.
Highlight : 39,40
Sentence : \r\n\r\n
Highlight :
```

### 토큰 종류
|태그|  뜻|
|----|----|
|NNG|일반 명사|
|NNP|고유 명사|
|NNB|의존 명사|
|NNBC|단위를 나타내는 명사|
|NR|수사|   
|NP|대명사|
|VV|동사|
|VA|형용사|
|VX|보조 용언|
|VCP|긍정 지정사|
|VCN|부정 지정사|
|MM|관형사|
|MAG|일반 부사|
|MAJ|접속 부사|
|IC|감탄사|
|JKS|주격 조사|
|JKC|보격 조사|
|JKG|관형격 조사|
|JKO|목적격 조사|
|JKB|부사격 조사|
|JKV|호격 조사|
|JKQ|인용격 조사|
|JX|보조사|
|JC|접속 조사|
|EP|선어말 어미|
|EF|종결 어미|
|EC|연결 어미|
|ETN|명사형 전성 어미|
|ETM|관형형 전성 어미|
|XPN|체언 접두사|
|XSN|명사 파생 접미사|
|XSV|동사 파생 접미사|
|XSA|형용사 파생 접미사|
|XR|어근|
|SF|마침표, 물음표, 느낌표|
|SE|줄임표|
|SS|따옴표,괄호표,줄표|
|SSO| 여는 괄호 (, [|
|SSC|닫는 괄호 ), ]|
|SP|쉼표,가운뎃점,콜론,빗금|
|SO|붙임표(물결,숨김,빠짐)|
|SW|기타기호 (논리수학기호,화폐기호)|
|SY|기타기호|
|SL|외국어|
|SH|한자|
|SN|숫자|

## Json 추출
### 추출 과정 예시
#### 문서 정보 추출 (from .excel)
```java
// 각 항의 데이터 추출
class DataPreprocessorImpl implements DataPreProcessor{

  protected JsonObject getDocumentInfo(XSSFCell cell) {

    String id = null ;
    try{
      id = Long.toString((long)cell.getNumericCellValue());
    }catch(Exception e) {
      try {
        id = getCellValue(0);
      }catch(Exception e1){
        logger.error(ExceptionUtil.getStackTrace(e));
      }
    }

    String sizeType = getCellValue(6);
    sizeType = SizeTypeUtil.getSizeType(sizeType);

    JsonObject document = new JsonObject();
    document.addProperty("id", id);
    document.addProperty("category", getCellValue(5));
    document.addProperty("media_type", "online");
    document.addProperty("media_sub_type", getCellValue(3));
    document.addProperty("media_name", getCellValue(1));
    document.addProperty("size", sizeType);
    document.addProperty("char_count", getCellValue(7));
    document.addProperty("publish_date", getCellValue(4));
    document.addProperty("title", getCellValue(8));

    return document;
  }

}

```

#### 문단 데이터 추출
```java
class TextMining{
  private JsonArray getText(String contents) {
    JsonArray text = new JsonArray();
    Document document = new Document();

    document.setType(documentType);
    document.setContents(contents);

    DocumentMiningParagraph documentMining = (DocumentMiningParagraph) DocumentMiningFactory.newDocumentMining(document);
    Paragraph[] paragraphs = documentMining.miningParagraph();

    int index = 0;
    for (Paragraph paragraph : paragraphs) {

      JsonArray paragraphJson = getParagraphJson(paragraph);

      if(paragraphJson.size() == 0) { continue; }

      for (int i = 0; i < paragraphJson.size(); i++) {
        JsonObject sentenceObject = paragraphJson.get(i).getAsJsonObject();
        sentenceObject.addProperty("index", index++);
      }

      text.add(paragraphJson);

    }
    return text;
  }
}

```

#### 문장 데이터 추출
```java
class TextMining{
  private JsonArray getParagraphJson(Paragraph paragraph) {
    JsonArray paragraphJson = new JsonArray();

    for (Sentence sentence : paragraph.getSentences()) {
      JsonObject sentenceJson = new JsonObject();

      WordToken[] wordTokens = sentence.getTokens();

      StringBuilder indexBuilder = new StringBuilder();
      outer:
      for (WordToken wordToken : wordTokens) {
        String partOfSpeech = wordToken.getPartOfSpeech();

        for (String out : outArray) {
          if (partOfSpeech.startsWith(out)) {
            indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
            continue outer;
          }
        }
      }

      String highlightIndices = "";
      if (indexBuilder.length() > 0) {
        highlightIndices = indexBuilder.substring(1);
      }



      sentenceJson.addProperty("index", 0);
      sentenceJson.addProperty("sentence", sentence.getContents());
      sentenceJson.addProperty("highlight_indices", highlightIndices);
      paragraphJson.add(sentenceJson);

    }

    return paragraphJson;
  }
}

```

#### 각 데이터 삽입
```java
class DataPreprocessorImpl{
  
    public JsonObject getDocument(XSSFSheet sheet, int rowIndex) {
    row = sheet.getRow(rowIndex);

    XSSFCell cell = row.getCell(0);

    if (cell == null) {
      logger.debug("rowIndex: " + rowIndex);
      return null;
    }

    JsonObject documentJsonObject = getDocumentInfo(cell);
    if(documentJsonObject == null) {
      logger.debug("document null ");
      return null;
    }


    String contents = getCellValue(9);
    if(contents == null){
      logger.debug("contents null");
      return null;
    }


    JsonArray text = getText(contents);
    if(text == null){
      logger.debug("text null");
      return null;
    }

    documentJsonObject.add("text", text);

    return documentJsonObject;
  }
}
```


### 결과
```json
{
  "name": "강원도민일보.20200720_161845_6349건_",
  "delivery_date": "2020-12-16 14:20:52",
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
            "sentence": "가족·연인 \"좋은 날만 가득하길\" 기원, 정동진·간절곶 인산인해",
            "highlight_indices": "13,15"
          }
        ],
        [
          {
            "index": 1,
            "sentence": "▲ 1일 오전 강릉 경포 해변에서 수평선 위로 기해년(己亥年) 첫 태양이 힘차게 떠오르고 있다.",
            "highlight_indices": "35,36"
          },
          {
            "index": 2,
            "sentence": "2019.1.1\\r\\n60년 만에 돌아온 황금돼지해인 기해년(己亥年) 첫 태양이 힘차게 떠올랐다.",
            "highlight_indices": "39,40"
          }
        ],
        [
          {
            "index": 3,
            "sentence": "새해 첫해는 7시 33분 울산 간절곶을 시작으로 동해안 수평선 위로 힘차게 솟았다.",
            "highlight_indices": "29,30"
          }
        ],
        [
          {
            "index": 4,
            "sentence": "해맞이 명소로 손꼽히는 강릉 정동진, 울산 간절곶, 포항 호미곶, 부산 해운대 등에는 해맞이객이 해안선을 따라 길게 늘어섰다.",
            "highlight_indices": ""
          }
        ],
        [
          {
            "index": 5,
            "sentence": "황금돼지해의 첫 태양이 수평선 위로 모습을 드러내자 해맞이객들은 탄성을 터뜨리며 가족·연인과 함께 저마다 간직한 새해 소망을 빌었다.",
            "highlight_indices": "7,8;52,54;55,58"
          }
        ],
        [
          ...
          ...
          ...
        ],
        [
          {
            "index": 26,
            "sentence": "어둠이 걷히기 전 고갯마루에 오른 시민들은 새해 소망을 담은 촛불을 밝히고, '하늘 소리 난타 공연단'의 대북 연주와 풍물 공연을 감상하면서 새해 각오를 다졌다.",
            "highlight_indices": "8,9"
          }
        ],
        [
          {
            "index": 27,
            "sentence": "가족과 함께 행사장을 찾은 김병우(68·보은군 보은읍)씨는 \"60년 만에 맞는 황금 돼지해인 만큼 가족 모두 건강하고, 사회 전반에 희망이 넘치기 바란다\"고 기원했다.",
            "highlight_indices": "4,6;58,60"
          }
        ],
        [
          {
            "index": 28,
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

전처리시 데이터가 사용되는 인공지능 학습에 필요가 없는 기자정보, 헤드라인, 이미지 정보 등등 필요 없는 정보들을 추가적으로 제거할 수 있다.