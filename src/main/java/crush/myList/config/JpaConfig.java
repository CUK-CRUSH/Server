package crush.myList.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
@EnableJpaRepositories(basePackages = "crush.myList.domain.*.repository") // JPA Repository 활성화
public class JpaConfig {
}
