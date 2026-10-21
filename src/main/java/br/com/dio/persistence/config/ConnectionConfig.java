package br.com.dio.persistence.config;

import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ConnectionConfig {

    public static Connection getConnection() throws SQLException {
        var url = System.getenv().getOrDefault("BOARD_DB_URL", "jdbc:mysql://localhost:3306/board?serverTimezone=UTC");
        var user = requiredEnvironment("BOARD_DB_USER");
        var password = requiredEnvironment("BOARD_DB_PASSWORD");
        var connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        return connection;
    }

    private static String requiredEnvironment(final String name) {
        var value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Configure a variável de ambiente %s para conectar ao MySQL".formatted(name));
        }
        return value;
    }

}
