package mate.academy.jvbookstore;

import java.math.BigDecimal;
import mate.academy.jvbookstore.model.Book;
import mate.academy.jvbookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JvBookStoreApplication {
    @Autowired
    private BookService bookService;
    
    public static void main(String[] args) {
        SpringApplication.run(JvBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setAutor("author");
            book.setCoverImage("coverImage");
            book.setIsbn("isbn");
            book.setPrice(BigDecimal.valueOf(1));
            book.setTitle("title");

            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
