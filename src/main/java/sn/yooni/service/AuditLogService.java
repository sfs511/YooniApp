package sn.yooni.service; // Déclaration du package service (logique métier)

import sn.yooni.model.AuditLog; // Entité journal d'audit
import sn.yooni.model.User; // Entité compte utilisateur
import sn.yooni.repository.AuditLogRepository; // Dépôt des traces d'audit

import lombok.RequiredArgsConstructor; // Génération d'un constructeur pour les champs finaux

import org.springframework.stereotype.Service; // Stéréotype service métier
import org.springframework.transaction.annotation.Transactional; // Gestion des transactions

@Service // Bean service enregistré dans le conteneur Spring
@RequiredArgsConstructor // Constructeur généré pour l'injection du dépôt
public class AuditLogService { // Services de traçabilité des actions sensibles

    private final AuditLogRepository auditLogRepository; // Accès aux traces d'audit

    // Journalise une action métier (login, livraison, paiement...) dans la table audit_logs
    @Transactional // Écriture en base
    public void record(String action, String entite, Long entiteId, String details, User user) {
        auditLogRepository.save(AuditLog.builder() // Construit la trace en base
                .userId(user != null ? user.getId() : null) // Identifiant de l'opérateur (null pour un accès système)
                .username(user != null ? user.getEmail() : null) // E-mail de l'opérateur au moment de l'action
                .action(action) // Intitulé de l'action (LIVRAISON_CREEE, LOGIN_SUCCESS...)
                .entite(entite) // Nom du modèle concerné (Livraison, Paiement, User...)
                .entiteId(entiteId) // Identifiant de la ressource impactée
                .details(details) // Description textuelle ou JSON de l'événement
                .build()); // Persiste la trace
    }

    // Recherche des traces d'audit par type d'action (tableaux de bord d'administration)
    public java.util.List<AuditLog> findByAction(String action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action); // Tri chronologique décroissant
    }

    // Recherche des traces d'audit d'un utilisateur (profil de sécurité)
    public java.util.List<AuditLog> findByUser(Long userId) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId); // Historique du compte
    }

}