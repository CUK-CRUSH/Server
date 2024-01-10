package crush.myList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MyListApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyListApplication.class, args);
	}

}
