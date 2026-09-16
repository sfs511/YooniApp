package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import org.springframework.context.annotation.Configuration; // Classe de configuration Spring
import org.springframework.transaction.annotation.EnableTransactionManagement; // Active la gestion des transactions

@Configuration // Classe de configuration Spring
@EnableTransactionManagement // Active explicitement le support @Transactional (déjà activé par Spring Boot, redondant volontairement)
public class DatabaseConfig { // Configuration de la couche d'accès aux données (JPA + Flyway + HikariCP)

    // La connexion, le pool HikariCP, Hibernate (ddl-auto=validate) et Flyway sont
    // entièrement pilotés par application.properties / application-dev.properties /
    // application-prod.properties. Cette classe centralise toute future surcharge
    // programmatique (traçage des requêtes, découpage par source de données, etc.).
}