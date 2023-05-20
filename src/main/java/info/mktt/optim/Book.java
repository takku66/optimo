package info.mktt.optim;

import java.util.Arrays;
import java.util.List;

/**
 * 本情報の保持・操作を提供する。
 * 
 * @author takku66
 * @since 1.0.0
 */
public record Book (String id, String name, int pageCount, String authorId) {

    /**
     * 本の情報を作成し保持する。
     */
    private static List<Book> books = Arrays.asList(
            new Book("book-1", "Effective Java", 416, "author-1"),
            new Book("book-2", "Hitchhiker's Guide to the Galaxy", 208, "author-2"),
            new Book("book-3", "Down Under", 436, "author-3")
    );

    /**
     * 本をIDで取得する。
     * @param id 本ID
     * @return Book
     */
    public static Book getById(String id) {
        return books.stream()
				.filter(book -> book.id().equals(id))
				.findFirst()
				.orElse(null);
    }
}
