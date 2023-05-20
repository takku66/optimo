package info.mktt.optimo;

import java.util.Arrays;
import java.util.List;

/**
 * 著者情報の保持・操作を提供する。
 * 
 * @author takku66
 * @since 1.0.0
 */
public record Author (String id, String firstName, String lastName) {

    /**
     * 著者の情報を作成し保持する。
     */
    private static List<Author> authors = Arrays.asList(
        new Author("author-1", "Joshua", "Bloch"),
        new Author("author-2", "Douglas", "Adams"),
        new Author("author-3", "Bill", "Bryson")
    );

    /**
     * 著者をIDで取得する。
     * @param id 著者ID
     * @return Author
     */
    public static Author getById(String id) {
        return authors.stream()
                    .filter(author -> author.id().equals(id))
                    .findFirst()
                    .orElse(null);
    }
}
