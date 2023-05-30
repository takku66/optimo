package info.mktt.optimo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpURLConnectionWrapper {

    private HttpURLConnection connection;

    private final Map<String, Object> bodyData = new HashMap<String, Object>();
    private final ObjectMapper mapper = new ObjectMapper();

    public enum RequestMethod {
        GET,
        POST,
        PUT,
        DELETE
    }

    public enum ContentType {
        JSON("application/json");

        String contentType;

        ContentType(String contentType){
            this.contentType = contentType;
        }

        public String getValue(){
            return this.contentType;
        }
    }

    public enum Encoding {
        UTF8("UTF-8");

        String encoding;

        Encoding(String encoding){
            this.encoding = encoding;
        }

        public String getValue(){
            return this.encoding;
        }

    }

    /**
     * <o>指定されたURLに接続し、接続状態のHttpURLConnectionオブジェクトを返す。
     * <p>接続に失敗した場合は、例外を発生させる。
     * @param url 接続したいURL
     * @return 接続状態のHttpURLConnectionオブジェクト
     * @throws IOException 接続に失敗した場合に発生する例外
     * @see HttpURLConnection
     */
    public HttpURLConnectionWrapper createConnection(String url) throws IOException{
        var obj = new URL(url);
        this.connection = (HttpURLConnection) obj.openConnection();
        return this;
    }

    public HttpURLConnectionWrapper method(HttpURLConnectionWrapper.RequestMethod method) throws ProtocolException {
        try{
            connection.setRequestMethod(method.name());

            // TODO: とりあえずPOSTの場合はdooutputをtrueにしてるけど、実際不要なケースもある？そこらへんは考慮できてない。
            switch(method.name()){
                case "POST":
                    this.connection.setDoOutput(true);
                    break;
                default:
                    break;
            }
        }catch(ProtocolException e){
            // TODO: そもそも発生はしないはずだが、指定のメソッドしか受け付けないことを呼び出し元に知らせる。
            throw e;
        }
        return this;
    }

    public HttpURLConnectionWrapper contentType(HttpURLConnectionWrapper.ContentType contentType) throws ProtocolException {
        connection.setRequestProperty("Content-Type", contentType.getValue());
        return this;
    }
    public HttpURLConnectionWrapper authorization(String token) throws ProtocolException {
        connection.setRequestProperty("Authorization", "Bearer " + token);
        return this;
    }
    public HttpURLConnectionWrapper openAiOrg(String orgId) throws ProtocolException {
        connection.setRequestProperty("OpenAI-Organization", orgId);
        return this;
    }

    // TODO: bodyへ値を設定するのは別クラスにして、このラッパークラスはそのクラスを設定すればよいようにしたい
    // TODO: jsonの直接指定と、key-value形式にも対応できるようにする。
    public HttpURLConnectionWrapper bufferBody(String key, Object value){
        this.bodyData.put(key, value);
        return this;
    }
    public HttpURLConnectionWrapper flushBody(HttpURLConnectionWrapper.Encoding encoding) throws UnsupportedEncodingException, IOException{
        // リクエストのbodyにJSON文字列を書き込む
        String json = mapper.writeValueAsString(this.bodyData);
        var out = new OutputStreamWriter(this.connection.getOutputStream(), encoding.getValue());
        out.write(json);
        out.flush();
        return this;
    }

    public void sendRequest() throws IOException{
        this.connection.connect();
    }

    public int getResponseCode() throws IOException{
        // TODO: レスポンス用のラッパークラスを別に作って、このクラスにもたせる
        return this.connection.getResponseCode();
    }

    public String getResponseData() throws IOException{
        // レスポンス用のラッパークラスに実装を移す
        String response = "";

        if(this.connection.getResponseCode() >= 200 & this.connection.getResponseCode() < 300){
            response = extractResponse(this.connection.getInputStream());
        }else{
            response = extractResponse(this.connection.getInputStream());
        }

        return response;
    }

    private String extractResponse(InputStream in) throws IOException{

        try(BufferedReader br = new BufferedReader(new InputStreamReader(in));){

            String inputLine;
            StringBuffer sb = new StringBuffer();

            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
    
            return sb.toString();
        }catch(IOException e){
            throw e;
        }
    }

}
