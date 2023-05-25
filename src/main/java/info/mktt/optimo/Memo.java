package info.mktt.optimo;

import java.util.Arrays;
import java.util.List;

/**
 * 本情報の保持・操作を提供する。
 * 
 * @author takku66
 * @since 1.0.0
 */
public record Memo (String id, String title, String content, List<String> categories) {

    /**
     * 本の情報を作成し保持する。
     */
    private static List<Memo> memos = Arrays.asList(
            new Memo("memo-1", "Memo Title1", "Memo Content1", Arrays.asList("カテゴリ1", "カテゴリ2", "カテゴリ3")),
            new Memo("memo-2", "Memo Title2", "Memo Content2", Arrays.asList("カテゴリ1", "カテゴリ2")),
            new Memo("memo-3", "Memo Title3", "Memo Content3", Arrays.asList("カテゴリ1", "カテゴリ4"))
    );

    /**
     * 本をIDで取得する。
     * @param id 本ID
     * @return Memo
     */
    public static Memo getById(String id) {
        return memos.stream()
				.filter(memo -> memo.id().equals(id))
				.findFirst()
				.orElse(null);
    }
}
