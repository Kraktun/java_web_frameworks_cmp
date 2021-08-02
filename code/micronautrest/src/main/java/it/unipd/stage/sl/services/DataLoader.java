package it.unipd.stage.sl.services;

import io.micronaut.flyway.FlywayConfigurationProperties;
import io.micronaut.flyway.FlywayMigrator;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import javax.transaction.Transactional;

/**
 * Initialize flyway after the db is created by hibernate
 */
@Singleton
public class DataLoader {

    @Inject
    FlywayMigrator flywayMigrator;
    @Inject
    DataSource dataSource;
    @Inject
    FlywayConfigurationProperties flywayConfigurationProperties;

    @EventListener
    @Transactional
    void onStartup(final ServerStartupEvent event) {
        System.out.println("Seeding db data");
        flywayMigrator.run(flywayConfigurationProperties, dataSource);
    }
}
