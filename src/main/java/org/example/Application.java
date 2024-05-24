package org.example;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

@SpringBootApplication
public class Application {
@Bean
    public DataSource h2dataSource (@Value("${jdbcUrl}") String jdbcUrl, @Value("${password}") String password
,@Value("${user}") String user) {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL(jdbcUrl);
    dataSource.setUser(user);
    dataSource.setPassword(password);
    return dataSource;
}
@Bean
    CommandLineRunner cmd (DataSource dataSource) {
    return args -> {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/initial.sql");
            String sqlForCreateDb = new String(inputStream.readAllBytes());
            try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlForCreateDb)){
                preparedStatement.executeUpdate();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    };
}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
