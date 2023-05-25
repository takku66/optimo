package info.mktt.optim.learn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class LearnObjectMapper {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    Environment environment;

    @Test
    void ObjectMapperのwriteValueAsString挙動確認() throws JsonProcessingException {

        var mapper = new ObjectMapper();

        // テスト用のパラメータを初期化する
        var sendDataUnOrdered = new HashMap<String, Object>();
        var sendDataOrdered = new LinkedHashMap<String, Object>();
        var messages = new ArrayList<Object>();
        messages.add(
            new HashMap<String, Object>(){
                {
                    put("role", "user");
                    put("content", "Say this is a test!");
                }
            }
        );

        // put時に順序を保証しない
        sendDataUnOrdered.put("model", "gpt-3.5-turbo");
        sendDataUnOrdered.put("messages", messages);
        sendDataUnOrdered.put("temperature", "0.7");
        var jsonUnordered = mapper.writeValueAsString(sendDataUnOrdered);

        // put時に順序を保証する
        sendDataOrdered.put("model", "gpt-3.5-turbo");
        sendDataOrdered.put("messages", messages);
        sendDataOrdered.put("temperature", "0.7");
        var jsonOrdered = mapper.writeValueAsString(sendDataOrdered);

        // HashMapで作成されたデータは、順序が保証されない
        var expectedUnOrderedString = "{\"temperature\":\"0.7\",\"messages\":[{\"role\":\"user\",\"content\":\"Say this is a test!\"}],\"model\":\"gpt-3.5-turbo\"}";
        assertThat(jsonUnordered).isEqualTo(expectedUnOrderedString);

        // LinkedHashMapで作成されたデータは、順序が保証される
        var exectedOrderedString = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\"Say this is a test!\"}],\"temperature\":\"0.7\"}";
        assertThat(jsonOrdered).isEqualTo(exectedOrderedString);

    }



}
