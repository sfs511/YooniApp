package sn.yooni.config; // Déclaration du package config dédié aux beans de configuration

import org.springframework.context.annotation.Bean; // Déclaration d'un bean de configuration
import org.springframework.context.annotation.Configuration; // Classe de configuration Spring
import org.springframework.data.domain.AuditorAware; // Fournisseur de l'utilisateur courant pour l'audit
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Active les horodatages @CreatedDate/@LastModifiedDate

import java.util.Optional; // Valeur optionnelle de l'auditeur courant

@Configuration // Classe de configuration Spring
@EnableJpaAuditing // Active l'audit automatique des entités (@CreatedDate, @LastModifiedDate, @CreatedBy)
public class AuditConfig { // Configuration de l'audit automatique des entités JPA

    // Fournit l'utilisateur courant pour les champs @CreatedBy/@LastModifiedBy (si utilisés)
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication()
        ).map(auth -> auth.getName()) // Retourne l'e-mail de l'utilisateur authentifié
                .or(() -> Optional.of("system")); // Valeur par défaut pour les traitements automatiques ou non authentifiés
    }

}