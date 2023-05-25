package info.mktt.optimo.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import info.mktt.optimo.Memo;

/**
 * メモのControllerクラス
 * @author takku66
 * @since 1.0.0
 */
@Controller
public class MemoController {

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

}
