package info.mktt.optimo.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import info.mktt.optimo.Memo;
import info.mktt.optimo.utils.HttpURLConnectionWrapper;

@Service
public class OpenAIService {
 
    @Autowired
    private final Environment environment;
    private final String apiKey;
    private final String orgId;

    // プロパティのキー文字列を定義
    /** OpenAIのAPIキー */
    private final String PROPERTY_API_KEY = "openapi.api-key";
    /** OpenAIの組織ID */
    private final String PROPERTY_ORGANIZATION_KEY = "openapi.organization-key";

    private final ObjectMapper mapper = new ObjectMapper();

    private final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";


    public OpenAIService(Environment environment){
        this.environment = environment;
        this.apiKey = this.environment.getProperty(this.PROPERTY_API_KEY);
        this.orgId = this.environment.getProperty(this.PROPERTY_ORGANIZATION_KEY);
    }

    
    public Memo requestOptimize(String content) throws UnsupportedEncodingException, ProtocolException, IOException{

        // OpenAIに送信するためのチャット内容を生成する。
        List<Object> messages = createMessages(content);

        // OpenAIのエンドポイントを叩くためのコネクションの生成と、各必要なパラメータを設定する
        var con = new HttpURLConnectionWrapper();
        con.createConnection(OPENAI_URL)
            .method(HttpURLConnectionWrapper.RequestMethod.POST)
            .contentType(HttpURLConnectionWrapper.ContentType.JSON)
            .authorization(this.apiKey)
            .openAiOrg(this.orgId)
            .bufferBody("model", "gpt-3.5-turbo")
            .bufferBody("temperature", 0.7)
            .bufferBody("messages", messages)
            .flushBody(HttpURLConnectionWrapper.Encoding.UTF8);

        // TODO: 作りこみができてない。
        try{
            // リクエストを送信
            con.sendRequest(); // TODO:わざわざsendRequestとか用意する必要あるか？
            int responseCode = con.getResponseCode();
            // TODO: 失敗した場合は例外を投げる
            // Memoのオブジェクトに変換
            return createMemo(con.getResponseData());
        }catch(IOException e){
            throw e;
        }
    }

    private List<Object> createMessages(String content){
        String contentPrefix = """
            以下の内容を確認して、タイトルと最大10個のカテゴリをつけてください。
            返答の内容はJSONの形で、タイトルは文字列型でキー名をtitle、カテゴリは配列型でキー名をcategories、原文は文字列型でキー名をcontentとしてください。
            以下に内容を記載します。

        """;

        var messages = new ArrayList<Object>();
        messages.add(
            new HashMap<String, Object>(){
                {
                    put("role", "user");
                    put("content", contentPrefix + content);
                }
            }
        );
        return messages;
    }

    private Memo createMemo(String json) throws IOException{
        JsonNode responseJson = mapper.readTree(json);
        String detailJson = responseJson.get("choices").get(0).get("message").get("content").toString();
        JsonNode detail = mapper.readTree(detailJson.replaceAll("\\\\n|\\\\", "").replaceAll("\"\\{", "{").replaceAll("\\}\"", "}"));
        String title = detail.get("title").toString();
        String content = detail.get("content").toString();
        JsonNode categoriesNode = detail.get("categories");
        List<String> categoriesList = mapper.readValue(categoriesNode.traverse(), new TypeReference<List<String>>(){});

        return new Memo(UUID.randomUUID().toString(), title, content, categoriesList);
    }

}
