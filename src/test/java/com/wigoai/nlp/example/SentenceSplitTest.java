package com.wigoai.nlp.example;

import org.junit.Test;
import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.Document;
import org.moara.ara.datamining.textmining.document.sentence.Sentence;
import org.moara.common.code.LangCode;

public class SentenceSplitTest {
    @Test
    public void sentenceSplitTest() {
        SenExtract senExtract =  SentenceDictionary.getInstance().getSenExtract(LangCode.KO, Document.NEWS);
        String tmp = "";
        boolean primeOpen = false;
        boolean doublePrimeOpen = false;
        String str1 = "한세화 기자 \\r\\n\\r\\n최병찬/사진 = V LIVE\\r\\n\\r\\n \\r\\n\\r\\n \\r\\n\\r\\n빅톤 최병찬이 '프로듀스X101'에서 하차하게 된 소식에 실시간 검색어에 오른 가운데 과거 최병찬이 인성 논란에 휩싸였던 사건이 재점화 되고 있다.\\r\\n\\r\\n \\r\\n\\r\\n과거 최병찬은 팬들과 함께 개인 방송 v-live를 진행 중 최병찬은 \"내가 얼마나 보고 싶었는지 알아?\"라는 팬의 질문에 \"어 미안 모르겠어\"라고 차갑게 대꾸했다.\\r\\n\\r\\n \\r\\n\\r\\n이어 그는 \"오빠 어깨보다 제 어깨가 더 넓은 거 같아요\"라는 팬의 댓글에 \"아, 나 놀리는 거구나? 마음대로 생각해. 네 어깨가 넓든 내 어깨가 넓든, 네 마음이지 내 상관은 아니잖아?\"라고 싸늘하게 말했다.\\r\\n\\r\\n \\r\\n\\r\\n또 최병찬은 방송 진행 내내 자신의 머리를 매만지거나 팬들의 질문에 다소 냉담한 대답을 보였다. \\r\\n\\r\\n \\r\\n\\r\\n결국 한 팬이 \"병찬이한테 뭔 말을 못 하겠다\"라고 하자 그는 실소를 터뜨리며 \"어, 하지 마, 못하겠으면 하지 마, 안 해도 돼\"라고 답했다. \\r\\n\\r\\n \\r\\n\\r\\n이후 최병찬은 '프로듀스X 101 시즌4'에 연습생으로 등장했으나 해당 영상이 다수의 온라인 커뮤니티를 통해 회자되며 일부 누리꾼들으로부터 인성 논란에 휩싸였다.\\r\\n\\r\\n \\r\\n\\r\\n한편 오늘(11일) 최병찬 소속사 플레이엠엔터테인먼트는 “Mnet '프로듀스X101'에 출연 중이던 최병찬이 건강상의 이유로 프로그램에서 하차하게 됐다”고 밝혔다. \\r\\n\\r\\n \\r\\n\\r\\n이어 \"평소 최병찬이 앓던 만성 아킬레스건염의 통증이 최근 들어 심해졌다. 여기에 심리적 부담감이 겹쳤다. 치료를 병행하며 연습 및 경연 일정에 참여했지만, 프로그램 출연을 지속하기 어려운 상황이라고 판단했다\"고 하차 이유를 밝혔다.\\r\\n\\r\\n \\r\\n\\r\\n아킬레스건염은 발뒤꿈치 쪽에 길게 붙어있는 힘줄과 뼈가 부착된 부분, 아킬레스건에 염증이 생기는 것을 말한다.\\r\\n\\r\\n \\r\\n\\r\\n한편 최병찬은 지난 2016년 11월 9일 빅톤 첫 번째 앨범 'Voice To New World'를 통해 가요계에 데뷔했다.\\r\\n\\r\\n \\r\\n\\r\\n \\r\\n한세화 기자 ent88@ ";
        String str2 = "충남여성단체협의회 '제35회 여성대회' 개최\\r\\n여성긴급전화1366센터 폭력예방캠페인 '눈길'\\r\\n\\r\\n김흥수 기자 \\r\\n\\r\\n아산 경찰인재개발원에서 2일 '제35회 충남도 여성대회'가 열렸다.\\r\\n충남여성단체협의회(회장 임춘숙)는 2일 아산 경찰인재개발원에서 '제35회 충남도 여성대회'를 개최했다. \\r\\n\\r\\n도내 15개 시·군 여성단체장과 회원 등 1000여 명이 참석한 가운데 열린 이번 여성대회는 도내 여성들의 역량 결집 및 양성평등 문화를 확산하기 위해 마련됐다. \\r\\n\\r\\n1부 기념식에서는 여성사회참여 유공자 표창 등 25명에 대한 표창장이 수여됐다. 이어 참석자 전원은 '양성평등 YES!! 편견·차별 NO!!' 양성평등주간 슬로건을 활용한 피켓 퍼포먼스를 펼쳤다. 2부 특강과 3부 화합한마당에서는 이호선 교수가 '마음을 움직이는 심리학'을 주제로 강연했고, 뻔뻔한 클래식 공연 및 레크리에이션 등이 이어졌다.\\r\\n\\r\\n여성긴급전화1366충남센터 존중하는 양성평등 및 가정폭력·아동학대예방 캠페인 모습.\\r\\n특히 이날 여성긴급전화1366충남센터는 존중하는 양성평등 및 가정폭력·아동학대예방 캠페인을 실시해 눈길을 끌었다. \\r\\n\\r\\n양승조 충남지사는 격려사를 통해 \"충남은 미투를 넘어 양성이 동등한 인격체로 존중받는 양성평등사회를 지향한다\"며 \"충남여성가족플라자 건립을 통해 여성의 사회참여 확대와 풀뿌리 여성조직 육성 기반을 마련하겠다\"고 말했다.\\r\\n\\r\\n또 임 회장은 대회사를 통해 \"양성평등주간 기념식의 주제처럼 함께할 100년 더 행복한 충남을 위해 양성평등을 일상으로 실천하겠다\"며 \"충남여성이 더 행복할 수 있도록 노력하겠다\"고 각오를 다졌다. \\r\\n내포=김흥수 기자 soooo0825@\\r\\n\\r\\n";
        String str3 = "한세화 기자\\r\\n\\r\\n\\r\\n\\r\\n29일 '클래스101 아이폰 이벤트'가 핫이슈로 등극했다.\\r\\n\\r\\n이날 간편송금 서비스 toss는 '클래스101 아이폰 이벤트' Lucky Quiz를 출제해 누리꾼들의 관심이 폭주했다.\\r\\n\\r\\n이날 행운상금이 걸린 '클래스101 아이폰 이벤트' 이벤트 Lucky Quiz가 등장했다.\\r\\n\\r\\n첫번째 Quiz는 \"아이패드 입문자를 위한 '아이패스 시작하기 후-리패쓰 입문편'에 포함된 콘텐츠의 총 개수는 몇 개일까요? (정답은 숫자만 입력해주세요)\"이다.\\r\\n\\r\\n정답은 '234'이다.\\r\\n\\r\\n두번째 Quiz는 \"준비물까지 챙겨주는 온라인 클래스, '클래스101', 역대 최대 할인 이벤트! '참교육 초특가' 준비물까지 챙겨주는 온라인 클래스, '클래스101'의 총 클래스 갯수는 몇 개일까요?\"이다.\\r\\n\\r\\n이를 맞추기 위한 Hint로는 naver에서 ‘클래스101 아이폰 이벤트’ 복사한 후 검색어를 붙여넣기 한 후 엔터를 쳐 나오는 결과물인 ‘참교육 초특가’에서, ‘참교육 초특가 슈퍼-세일’을 참조하면 된다고 설명했다. 정답은 '372'다.\\r\\n\\r\\n세번째 Quiz는 '이벤트의 당첨자 발표일인 ○○월 ☆일 에서 ○○+☆=의 값은 얼마일까요?'이다.\\r\\n\\r\\n정답은 '12'이다.\\r\\n\\r\\n한편, ‘TOSS'처럼 mobile을 통해 간단하게 돈을 보내는 이용 액수가 작년 보다 세 배가 증가한 것으로 알려졌다.\\r\\n\\r\\n또한 아파트 관리비를 이메일 등 전자 방식으로 발행한 다음 대금을 직접 수수하고 정산을 대행하는 서비스인 전자고지 결제서비스도 증가한 것으로 알려져 우리 생활방식의 변화의 흐름을 읽을 수 있다.\\r\\n\\r\\n한세화 기자 ent88@";
        for(Sentence sentence : senExtract.extractSentenceList(0, str3.replace("\\r", "").replace("\\n", ""),"N")) {
            String sentenceValue = sentence.getValue();

            for(int i = 0 ; i < sentenceValue.length() ; i++) {
                char target = sentenceValue.charAt(i);
                if(target == '\'') primeOpen = !primeOpen;
                else if(target == '\"') doublePrimeOpen = !doublePrimeOpen;
            }
            tmp += (sentenceValue + " ");

            if(!primeOpen && !doublePrimeOpen) {
                System.out.println(tmp);
                tmp = "";
            }

        }
    }
}
