package br.com.dio.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public void executeMigration(){
        try (var jdbcConnection = new JdbcConnection(connection)) {
            var liquibase = new Liquibase(
                    "/db/changelog/db.changelog-master.yml",
                    new ClassLoaderResourceAccessor(),
                    jdbcConnection);
            try {
                liquibase.update();
            } finally {
                liquibase.close();
            }
        } catch (LiquibaseException e) {
            throw new IllegalStateException("Não foi possível aplicar as migrations do banco", e);
        }
    }

}
