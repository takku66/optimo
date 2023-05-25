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

import info.mktt.optimo.OptimoApplication;

@SpringBootTest(classes=OptimoApplication.class)
public class LearnOpenAI {

    // プロパティファイルから設定値を取得する
    @Autowired
    Environment environment;

    // プロパティのキー文字列を定義
    /** OpenAIのAPIキー */
    String PROPERTY_API_KEY = "openapi.api-key";
    /** OpenAIの組織ID */
    String PROPERTY_ORGANIZATION_KEY = "openapi.organization-key";

    ObjectMapper mapper = new ObjectMapper();

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
            String successResponse = extractOneLine(in);
            System.out.println(successResponse.toString());
        }catch(IOException e){
            String errorResponse = extractOneLine(con.getInputStream());
            System.out.println(errorResponse);
            throw e;
        }finally{
            con.disconnect();
        }

    }

    /**
     * <p>InputStreamを読み取り、1行の文字列を返す。
     * @param in 抽出対象のストリーム
     * @return 抽出した1行の文字列
     * @throws IOException
     */
    private String extractOneLine(InputStream in) throws IOException{

        try(BufferedReader br = new BufferedReader(new InputStreamReader(in));){
            return extractOneLine(br);
        }
    }

    /**
     * <p>BufferedReaderを読み取り、1行の文字列を返す。
     * @param br 抽出対象のBufferedReader
     * @return 抽出した1行の文字列
     * @throws IOException
     */
    private String extractOneLine(BufferedReader br) throws IOException{

        String inputLine;
        StringBuffer sb = new StringBuffer();

        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }

        br.close();

        return sb.toString();
    }

    /**
     * <o>指定されたURLに接続し、接続状態のHttpURLConnectionオブジェクトを返す。
     * <p>接続に失敗した場合は、例外を発生させる。
     * @param url 接続したいURL
     * @return 接続状態のHttpURLConnectionオブジェクト
     * @throws IOException 接続に失敗した場合に発生する例外
     * @see HttpURLConnection
     */
    private HttpURLConnection createConnection(String url) throws IOException{
        var obj = new URL(url);
        return (HttpURLConnection) obj.openConnection();
    }

    /**
     * リクエストプロパティを設定する
     * @param con リクエスト送信前のコネクション
     * @throws ProtocolException リクエストメソッドが正しく設定されなかった場合に発生する例外
     */
    private void writeProperty(HttpURLConnection con) throws ProtocolException{
        var API_KEY = environment.getProperty(PROPERTY_API_KEY);
        var ORGANIZATION_KEY = environment.getProperty(PROPERTY_ORGANIZATION_KEY);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("OpenAI-Organization", ORGANIZATION_KEY);
        con.setDoOutput(true);
    }

    /**
     * 正常系のリクエストボディを設定する
     * @param con リクエスト送信前のコネクション
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
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

    /**
     * リクエストボディに書き込む
     * @param con リクエスト送信前のコネクション
     * @param json リクエストボディに書き込むJson文字列
     * @throws IOException 書き込めなかった場合の例外
     */
    private void writeConnection(HttpURLConnection con, String json) throws IOException{
        // リクエストのbodyにJSON文字列を書き込む
        var out = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
        out.write(json);
        out.flush();
    }

    




}
