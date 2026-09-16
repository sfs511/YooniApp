package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import com.fasterxml.jackson.annotation.JsonProperty; // Importation pour contrôler la sérialisation JSON des champs sensibles
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Importation des annotations Lombok pour la génération automatique du code
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.Instant; // Importation pour la gestion précise d'horodatage UTC

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate) pour cette entité
@Table(name = "refresh_tokens") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des jetons proprement
public class RefreshToken { // Déclaration de la classe modèle RefreshToken

    @Id // Clé primaire de la table refresh_tokens
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique de l'enregistrement

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Le hash n'est JAMAIS exposé en lecture JSON (sécurité)
    @Column(nullable = false, unique = true, length = 255) // Jeton unique et obligatoire
    private String token; // Empreinte SHA-256 du jeton de rafraîchissement (jamais la valeur en clair : voir AuthService.hashToken)

    @OneToOne(fetch = FetchType.LAZY) // Un utilisateur est associé à son jeton de rafraîchissement actif
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Clé étrangère user_id liée à la table users
    private User user; // Utilisateur propriétaire du jeton

    @Column(name = "expiry_date", nullable = false) // Date d'expiration obligatoire
    private Instant expiryDate; // Instant exact au-delà duquel le jeton n'est plus valide

    @Column(nullable = false) // Statut de révocation obligatoire
    @Builder.Default // Valeur par défaut pour le Builder Lombok
    private Boolean revoked = false; // Indique si le jeton a été révoqué manuellement (ex: déconnexion)

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Horodatage de création non modifiable
    private Instant createdAt; // Date et heure UTC de génération du jeton

}