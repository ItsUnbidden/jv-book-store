package mate.academy.jvbookstore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
            title = "Book store",
            version = "0.1"
        )
)
@SpringBootApplication
public class JvBookStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(JvBookStoreApplication.class, args);
    }
}
