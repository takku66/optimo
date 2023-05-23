package info.mktt.optim.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class LearnOpenAI {

    String PROPERTY_API_KEY = "openapi.api-key";

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Environment environment;

    @Test
    void OpenAIに正しいパラメータを設定すればレスポンスステータス200が返ってくる() throws IOException{

        var url = "https://api.openai.com/v1/chat/completions";

        HttpURLConnection con = createConnection(url);
        writeProperty(con);
        writeBodyForValidParameter(con);
        
        // テスト用の文言をOpenAIにリクエスト
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        // レスポンスを取得し、正常・異常問わず結果をコンソールに出力する
        try(BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));){

            String successResponse = extractInputStream(in);
            System.out.println(successResponse.toString());
        }catch(IOException e){
            String errorResponse = extractInputStream(con.getInputStream());
            System.out.println(errorResponse);
            throw e;
        }finally{
            con.disconnect();
        }

    }

    private String extractInputStream(InputStream in) throws IOException{

        try(BufferedReader br = new BufferedReader(new InputStreamReader(in));){
            return extractInputStream(br);
        }
    }

    private String extractInputStream(BufferedReader br) throws IOException{

        String inputLine;
        StringBuffer sb = new StringBuffer();

        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }

        br.close();

        return sb.toString();
    }

    private HttpURLConnection createConnection(String url) throws IOException{
        var obj = new URL(url);
        return (HttpURLConnection) obj.openConnection();
    }

    private void writeProperty(HttpURLConnection con) throws ProtocolException{
        var API_KEY = environment.getProperty(PROPERTY_API_KEY);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("OpenAI-Organization", "org-RXGuQAaTwyKyHo5ixtbhkfHr");
        con.setDoOutput(true);
    }

    private void writeBodyForValidParameter(HttpURLConnection con) throws UnsupportedEncodingException, IOException{
        var sendData = new HashMap<String, Object>();
        var messages = new ArrayList<Object>();
        messages.add(
            new HashMap<String, Object>(){
                {
                    put("role", "user");
                    put("content", "Say this is a test!");
                }
            }
        );
        sendData.put("model", "gpt-3.5-turbo");
        sendData.put("messages", messages);
        sendData.put("temperature", 0.7);
        var json = mapper.writeValueAsString(sendData);
        writeConnection(con, json);
    }

    private void writeConnection(HttpURLConnection con, String json) throws IOException{
        // リクエストのbodyにJSON文字列を書き込む
        var out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
        out.write(json);
        out.flush();
    }

    




}
