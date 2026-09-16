package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.AuditLog; // Importation de l'entité AuditLog

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.List; // Liste de traces d'audit

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> { // Dépôt JPA lié à l'entité AuditLog

    // Traces d'audit récentes d'un utilisateur donné (AuditLog stocke userId, pas une relation User)
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId); // Historique des actions d'un compte

    // Traces d'audit par type d'action (LOGIN, REGISTER, DELETE...)
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action); // Filtre par action pour les tableaux de bord

}