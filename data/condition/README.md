# 구분 조건 데이터  

## 파일 형식 
JSON  

## 내부 정보
- id : 구분 조건 ID
- use_public_validation : 공통 유효성 사용 여부
  - `Y`, `N`
- split_position : 구분 위치
  - `B`, `F`
- value : 구분시 사용하는 값
  - string_group 내부에 존재하는 `.dic` 파일
- validations : 사용하는 유효성 ID  

### Validation ID 발급 규칙
`ABCD_ID`  
ID의 앞 네글자는 해당 유효성에 적용되는 속성값을 뜻한다.    
  
- A : `Y` or `N`
    - 매치 여부  
    - `Y` : 유효성 값과 일치할 경우 유효  
    - `N` : 유효성 값과 다를경우 유효  
    
- B : `F` or `B`
    - 비교 위치
    - `F` : 구분점의 앞에서 비교  
    - `B` : 구분점의 뒤에서 비교  
    
- CD : `SG` or `RX` 등등 
    - 사용하는 값 유형  
    - `SG` : 문자열 그룹 (String Group)
    - `RX` : 정규식 데이터 (Regx)
    - 계속 추가될 예정

- ID
  - 해당 조건이 사용 할 값의 ID
    - `connective_1`
    - `number_index`
    - 등등
   

## 예시
```json
{
  "id": "terminator",
  "use_public_validation": "N",
  "split_position": "B",
  "value": "terminator_1",
  "validations": ["NBSG_connective_1", "NBSG_connective_2"]
}
```

