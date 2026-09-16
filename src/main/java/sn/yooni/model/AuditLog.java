package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Importation des annotations Lombok pour la génération automatique du code
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.LocalDateTime; // Importation pour la gestion des horodatages

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate) pour cette entité
@Table(name = "audit_logs") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des journaux d'audit
public class AuditLog { // Déclaration de la classe modèle AuditLog

    @Id // Clé primaire de la table audit_logs
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique de l'entrée d'audit

    @Column(name = "user_id") // Identifiant de l'utilisateur (null si action système ou non authentifiée)
    private Long userId; // ID de l'utilisateur concerné par l'action

    @Column(length = 180) // Email ou login de l'utilisateur au moment de l'action
    private String username; // Identifiant textuel de l'opérateur

    @Column(nullable = false, length = 100) // Intitulé de l'action obligatoire
    private String action; // Type d'action (ex: LOGIN_SUCCESS, PAYMENT_PROCESSED, USER_SUSPENDED)

    @Column(length = 100) // Nom de l'entité concernée
    private String entite; // Nom du modèle ciblé (ex: Livraison, User, Paiement)

    @Column(name = "entite_id") // Identifiant de la ressource impactée
    private Long entiteId; // ID spécifique de l'élément modifié

    @Column(name = "ip_adresse", length = 45) // Adresse IP d'origine (supporte IPv4 et IPv6)
    private String ipAdresse; // Adresse IP source de la requête HTTP

    @Column(columnDefinition = "TEXT") // Description longue des modifications
    private String details; // Données supplémentaires au format texte/JSON expliquant l'événement

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Horodatage fixe de l'événement
    private LocalDateTime createdAt; // Date et heure précises de la journalisation

}