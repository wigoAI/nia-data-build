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
        String[] answer = {"한세화 기자 최병찬/사진 = V LIVE 빅톤 최병찬이 '프로듀스X101'에서 하차하게 된 소식에 실시간 검색어에 오른 가운데 과거 최병찬이 인성 논란에 휩싸였던 사건이 재점화 되고 있다.\n",
                "과거 최병찬은 팬들과 함께 개인 방송 v-live를 진행 중 최병찬은 \"내가 얼마나 보고 싶었는지 알아?\"라는 팬의 질문에 \"어 미안 모르겠어\"라고 차갑게 대꾸했다.\n",
                "이어 그는 \"오빠 어깨보다 제 어깨가 더 넓은 거 같아요\"라는 팬의 댓글에 \"아, 나 놀리는 거구나? 마음대로 생각해. 네 어깨가 넓든 내 어깨가 넓든, 네 마음이지 내 상관은 아니잖아?\"라고 싸늘하게 말했다.\n",
                "또 최병찬은 방송 진행 내내 자신의 머리를 매만지거나 팬들의 질문에 다소 냉담한 대답을 보였다.\n",
                "결국 한 팬이 \"병찬이한테 뭔 말을 못 하겠다\"라고 하자 그는 실소를 터뜨리며 \"어, 하지 마, 못하겠으면 하지 마, 안 해도 돼\"라고 답했다.\n",
                "이후 최병찬은 '프로듀스X 101 시즌4'에 연습생으로 등장했으나 해당 영상이 다수의 온라인 커뮤니티를 통해 회자되며 일부 누리꾼들으로부터 인성 논란에 휩싸였다.\n",
                "한편 오늘(11일) 최병찬 소속사 플레이엠엔터테인먼트는 “Mnet '프로듀스X101'에 출연 중이던 최병찬이 건강상의 이유로 프로그램에서 하차하게 됐다”고 밝혔다.\n",
                "이어 \"평소 최병찬이 앓던 만성 아킬레스건염의 통증이 최근 들어 심해졌다. 여기에 심리적 부담감이 겹쳤다. 치료를 병행하며 연습 및 경연 일정에 참여했지만, 프로그램 출연을 지속하기 어려운 상황이라고 판단했다\"고 하차 이유를 밝혔다.\n",
                "아킬레스건염은 발뒤꿈치 쪽에 길게 붙어있는 힘줄과 뼈가 부착된 부분, 아킬레스건에 염증이 생기는 것을 말한다.\n",
                "한편 최병찬은 지난 2016년 11월 9일 빅톤 첫 번째 앨범 'Voice To New World'를 통해 가요계에 데뷔했다.\n",
                "한세화 기자 ent88@\n"};
        String tmp = "";
        boolean primeOpen = false;
        boolean doublePrimeOpen = false;
        String str1 = "한세화 기자 \\r\\n\\r\\n최병찬/사진 = V LIVE\\r\\n\\r\\n \\r\\n\\r\\n \\r\\n\\r\\n빅톤 최병찬이 '프로듀스X101'에서 하차하게 된 소식에 실시간 검색어에 오른 가운데 과거 최병찬이 인성 논란에 휩싸였던 사건이 재점화 되고 있다.\\r\\n\\r\\n \\r\\n\\r\\n과거 최병찬은 팬들과 함께 개인 방송 v-live를 진행 중 최병찬은 \"내가 얼마나 보고 싶었는지 알아?\"라는 팬의 질문에 \"어 미안 모르겠어\"라고 차갑게 대꾸했다.\\r\\n\\r\\n \\r\\n\\r\\n이어 그는 \"오빠 어깨보다 제 어깨가 더 넓은 거 같아요\"라는 팬의 댓글에 \"아, 나 놀리는 거구나? 마음대로 생각해. 네 어깨가 넓든 내 어깨가 넓든, 네 마음이지 내 상관은 아니잖아?\"라고 싸늘하게 말했다.\\r\\n\\r\\n \\r\\n\\r\\n또 최병찬은 방송 진행 내내 자신의 머리를 매만지거나 팬들의 질문에 다소 냉담한 대답을 보였다. \\r\\n\\r\\n \\r\\n\\r\\n결국 한 팬이 \"병찬이한테 뭔 말을 못 하겠다\"라고 하자 그는 실소를 터뜨리며 \"어, 하지 마, 못하겠으면 하지 마, 안 해도 돼\"라고 답했다. \\r\\n\\r\\n \\r\\n\\r\\n이후 최병찬은 '프로듀스X 101 시즌4'에 연습생으로 등장했으나 해당 영상이 다수의 온라인 커뮤니티를 통해 회자되며 일부 누리꾼들으로부터 인성 논란에 휩싸였다.\\r\\n\\r\\n \\r\\n\\r\\n한편 오늘(11일) 최병찬 소속사 플레이엠엔터테인먼트는 “Mnet '프로듀스X101'에 출연 중이던 최병찬이 건강상의 이유로 프로그램에서 하차하게 됐다”고 밝혔다. \\r\\n\\r\\n \\r\\n\\r\\n이어 \"평소 최병찬이 앓던 만성 아킬레스건염의 통증이 최근 들어 심해졌다. 여기에 심리적 부담감이 겹쳤다. 치료를 병행하며 연습 및 경연 일정에 참여했지만, 프로그램 출연을 지속하기 어려운 상황이라고 판단했다\"고 하차 이유를 밝혔다.\\r\\n\\r\\n \\r\\n\\r\\n아킬레스건염은 발뒤꿈치 쪽에 길게 붙어있는 힘줄과 뼈가 부착된 부분, 아킬레스건에 염증이 생기는 것을 말한다.\\r\\n\\r\\n \\r\\n\\r\\n한편 최병찬은 지난 2016년 11월 9일 빅톤 첫 번째 앨범 'Voice To New World'를 통해 가요계에 데뷔했다.\\r\\n\\r\\n \\r\\n\\r\\n \\r\\n한세화 기자 ent88@";
        String str2 = "충남여성단체협의회 '제35회 여성대회' 개최\\r\\n여성긴급전화1366센터 폭력예방캠페인 '눈길'\\r\\n\\r\\n김흥수 기자 \\r\\n\\r\\n아산 경찰인재개발원에서 2일 '제35회 충남도 여성대회'가 열렸다.\\r\\n충남여성단체협의회(회장 임춘숙)는 2일 아산 경찰인재개발원에서 '제35회 충남도 여성대회'를 개최했다. \\r\\n\\r\\n도내 15개 시·군 여성단체장과 회원 등 1000여 명이 참석한 가운데 열린 이번 여성대회는 도내 여성들의 역량 결집 및 양성평등 문화를 확산하기 위해 마련됐다. \\r\\n\\r\\n1부 기념식에서는 여성사회참여 유공자 표창 등 25명에 대한 표창장이 수여됐다. 이어 참석자 전원은 '양성평등 YES!! 편견·차별 NO!!' 양성평등주간 슬로건을 활용한 피켓 퍼포먼스를 펼쳤다. 2부 특강과 3부 화합한마당에서는 이호선 교수가 '마음을 움직이는 심리학'을 주제로 강연했고, 뻔뻔한 클래식 공연 및 레크리에이션 등이 이어졌다.\\r\\n\\r\\n여성긴급전화1366충남센터 존중하는 양성평등 및 가정폭력·아동학대예방 캠페인 모습.\\r\\n특히 이날 여성긴급전화1366충남센터는 존중하는 양성평등 및 가정폭력·아동학대예방 캠페인을 실시해 눈길을 끌었다. \\r\\n\\r\\n양승조 충남지사는 격려사를 통해 \"충남은 미투를 넘어 양성이 동등한 인격체로 존중받는 양성평등사회를 지향한다\"며 \"충남여성가족플라자 건립을 통해 여성의 사회참여 확대와 풀뿌리 여성조직 육성 기반을 마련하겠다\"고 말했다.\\r\\n\\r\\n또 임 회장은 대회사를 통해 \"양성평등주간 기념식의 주제처럼 함께할 100년 더 행복한 충남을 위해 양성평등을 일상으로 실천하겠다\"며 \"충남여성이 더 행복할 수 있도록 노력하겠다\"고 각오를 다졌다. \\r\\n내포=김흥수 기자 soooo0825@\\r\\n\\r\\n";
        String str3 = "첫번째 Quiz는 \"아이패드 입문자를 위한 '아이패스 시작하기 후-리패쓰 입문편'에 포함된 콘텐츠의 총 개수는 몇 개일까요? (정답은 숫자만 입력해주세요)\"이다. 정답은 '234'이다.";
        String str4 = "신서희 도시계획분야 백기영 교수, 민간위원 김범수 공동위원장 선출 \\r\\n(동양일보 신서희 기자) 행정중심복합도시건설청은 세종시 중앙공원2단계 민관협의체 공동위원장으로 백기영 교수와 김범수 민간위원이 선출됐다고 밝혔다. \\r\\n\\r\\n민관협의체는 중앙공원2단계 환경영향평가에 필요한 시설물 규모, 배치 등 마스터플랜(안) 마련에 대한 의견수렴을 위해 지난 11월에 각 읍?동에서 추천된 시민의원 10명과 관계기관 및 분야별 전문가 10명으로 구성된 자문기구다. \\r\\n\\r\\n공동위원장은 행복청?LH, 분야별 전문가?시민들과 함께 원활하고 효율적인 업무추진을 위해 소위원회를 구성?운영하여 민관협의체 전체 의견을 조율하는 역할을 하게 된다. \\r\\n\\r\\n백기영 위원장은 “중앙공원2단계 조성은 행복도시 중앙녹지공간의 완성을 위한 마지막 조각이 될 것”이라며, “주변 시설들과의 연계 효과를 극대화 하기 위해 빠른 시일 내에 마스터플랜이 마련될 수 있도록 노력하겠다”고 밝혔다. \\r\\n\\r\\n또한, 공동위원장인 김범수 위원장은 “갈등구조에 있는 중앙공원2단계의 환경영향평가를 위해 조속히 도입시설을 결정하여야 할 것”이라며, “갈등을 해소하여 하루 빨리 시민들에게 중앙공원을 돌려주고 싶다”고 말했다. \\r\\n\\r\\n향후, 유사 공원사례 조사와 도입시설 검토를 위해 공원계획, 생태?관리 소위원회를 구성하여 심층적으로 논의해 내년 3월을 목표로 마스터플랜(안)을 마련할 예정이다. \\r\\n\\r\\n세종 신서희 기자\\r\\n";
        String str5 = "김용범 금융위 부위원장은 “지난 2017년 도산전문법원인 서울회생법원 출범으로 국내 채무조정 제도 운영이 크게 개선되고 공·사 채무조정 간 연계가 확대되고 있다”며 “이번 방안으로 주택경매에 따른 주거상실 우려 없이 개인회생 절차를 진행할 수 있게 돼 채무조정안 이행의 성공률을 높일 것”이라고 말했다. jiany@fnnews.com 연지안 기자";
        String str6 = "대구 의료계 \"병원 내 폭력행위에 대해 처벌 강화하고 의료진 안전 확보해야\" [홍준헌 기자 hjh@imaeil.com]";
        for(Sentence sentence : senExtract.extractSentenceList(0, str6.replace("\\r", "").replace("\\n", ""),"N")) {
            String sentenceValue = sentence.getValue();

            System.out.println(sentenceValue);

        }
    }

}
