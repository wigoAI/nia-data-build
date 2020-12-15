# 구분기 데이터
## 파일 형식
JSON
## 내부 정보
- id : 구분기 ID
- name : 구분기 이름
- minimum_split_length : 문장 최소 길이
- conditions : 구분기가 사용하는 조건 ID
  - [condition 디렉터리](../condition)에 해당 ID를 가진 JSON 파일이 존재해야 함
  - 여러개 사용 가능
- exceptions : 구분기가 사용하는 예외 영역 ID
  - [exception 디렉터리](../exception)에 해당 ID를 가진 JSON 파일이 존재해야 함
  - 여러개 사용 가능

## 예시
```json

{
  "id": "basic",
  "name": "기본구분기",
  "minimum_split_length": 5,
  "conditions": ["terminator"],
  "exceptions": ["bracket_exception"]
}
```


