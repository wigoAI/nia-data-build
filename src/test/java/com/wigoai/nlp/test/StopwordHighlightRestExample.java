package com.wigoai.nlp.test;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <pre>
 *  파 일 명 : StopwordHighlightRestExample.java
 *  설    명 :
 *
 *  작 성 자 : macle(김용수)
 *  작 성 일 : 2020.07
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author Copyrights 2020 by ㈜ WIGO. All right reserved.
 */

public class StopwordHighlightRestExample {

    public static void main(String[] args) {
        try{


            JSONObject param = new JSONObject();
            param.put("contents" , "국내 양대 포털인 네이버·카카오가 어떤 혐오표현을 얼마나 규제하는지에 대한 정보를 공개해야 한다는 지적이 나왔다.\n"
                    +  "\n"
                    +"한국인터넷자율정책기구(KISO)는 9일 오후 서울 종로구 새문안로 S타워에서 '온라인상 혐오표현 그 해법은 무엇인가'라는 주제로 포럼을 열었다. KISO 이사회 의장은 여민수 카카오 공동대표가 맡고 있다");

            URL url = new URL("http://moara.org:7100/stopword/wordclass/highlight/");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setUseCaches(false);

            OutputStream stream = conn.getOutputStream();
            stream.write(param.toString().getBytes());
            stream.flush();
            stream.close();

            String charSet = "UTF-8";
            StringBuilder messageBuilder = new StringBuilder();
            BufferedReader br;

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), charSet));

                for (;;) {
                    String line = br.readLine();
                    if (line == null) break;
                    messageBuilder.append('\n').append(line);
                }
                br.close();

            }else{
                throw new RuntimeException("http response fail: " + conn.getResponseCode());
            }

            String message ;

            if(messageBuilder.length() > 0){
                message = messageBuilder.substring(1);
            }else{
                message = "";
            }

            JSONObject response = new JSONObject(message);

            System.out.println(response.getString("contents"));

        }catch(Exception e){
            e.printStackTrace();
        }



    }

}
