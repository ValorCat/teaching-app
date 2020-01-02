package teaching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static final String DB_SCHEMA = "adept_python";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
