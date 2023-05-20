package info.mktt.optim.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import info.mktt.optim.Author;
import info.mktt.optim.Book;

/**
 * 本のControllerクラス
 * 
 * @author takku66
 * @since 1.0.0
 */
@Controller
public class BookController {

    /**
     * 本情報を取得する。
     * @param id
     * @return Book
     * @see Book
     */
    @QueryMapping
    public Book bookById(@Argument String id) {
        return Book.getById(id);
    }

    /**
     * 著者の情報を取得する。
     * @param book 本情報
     * @return Author
     * @see Author
     */
    @SchemaMapping
    public Author author(Book book) {
        return Author.getById(book.authorId());
    }
}
