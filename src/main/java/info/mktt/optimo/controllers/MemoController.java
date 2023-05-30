package info.mktt.optimo.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ProtocolException;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import info.mktt.optimo.Memo;
import info.mktt.optimo.services.OpenAIService;
import lombok.RequiredArgsConstructor;

/**
 * メモのControllerクラス
 * @author takku66
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Controller
public class MemoController {

    private final OpenAIService service;

    /**
     * メモ情報を取得する。
     * @param id
     * @return Memo
     * @see Memo
     */
    @QueryMapping
    public Memo memoById(@Argument String id) {
        return Memo.getById(id);
    }

    @QueryMapping
    public Memo optimizeMemo(@Argument String content) throws IOException {
        try{
            return service.requestOptimize(content);
        }catch(IOException e){
            e.printStackTrace();
            throw e;
        }
    }
}