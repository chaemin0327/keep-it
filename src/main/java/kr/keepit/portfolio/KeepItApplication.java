package kr.keepit.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class KeepItApplication {

	public static void main(String[] args) {
        boolean customDatabase = System.getProperty("spring.datasource.url") != null || System.getenv("SPRING_DATASOURCE_URL") != null ||
                Arrays.stream(args).anyMatch(arg -> arg.startsWith("--spring.datasource.url="));
        if (!customDatabase) {
            String directory = System.getenv().getOrDefault("KEEPIT_DATA_DIR", System.getenv().getOrDefault("MOA_DATA_DIR", "./data"));
            try { LegacyDataMigration.copyIfNeeded(Path.of(directory)); }
            catch (IOException e) { throw new IllegalStateException("기존 데이터 이전에 실패했습니다. " + e.getMessage(), e); }
        }
        SpringApplication.run(KeepItApplication.class, args);
	}

}
