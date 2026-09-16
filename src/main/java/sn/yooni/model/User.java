package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter la récursion infinie lors de la sérialisation JSON
import com.fasterxml.jackson.annotation.JsonProperty; // Importation pour contrôler la sérialisation JSON des champs sensibles
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance en base de données
import lombok.*; // Importation des annotations Lombok pour réduire le code boilerplate
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.annotation.LastModifiedDate; // Importation pour l'horodatage automatique de mise à jour
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.LocalDateTime; // Importation du type de date/heure Java 8+

@Entity // Indique à JPA que cette classe est une entité persistante mappée sur une table SQL
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate/@LastModifiedDate) pour cette entité
@Table(name = "users") // Spécifie le nom exact de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génère automatiquement tous les getters pour les champs
@Setter // Lombok : Génère automatiquement tous les setters pour les champs
@NoArgsConstructor // Lombok : Génère le constructeur sans arguments exigé par JPA
@AllArgsConstructor // Lombok : Génère un constructeur contenant tous les arguments
@Builder // Lombok : Implémente le pattern Builder pour instancier facilement des objets User
public class User { // Déclaration de la classe modèle User

    @Id // Indique que ce champ est la clé primaire de la table SQL
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Délègue la génération de l'ID à l'AUTO_INCREMENT de MySQL
    private Long id; // Identifiant unique de l'utilisateur

    @Column(nullable = false, unique = true, length = 180) // Champ obligatoire, unique en BDD et limité à 180 caractères
    private String email; // Adresse e-mail de connexion de l'utilisateur

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Le hash n'est JAMAIS exposé en lecture JSON (sécurité)
    @Column(name = "password_hash", nullable = false) // Mappe sur la colonne password_hash, obligatoire
    private String passwordHash; // Empreinte cryptographique du mot de passe (haché avec BCrypt, jamais stocké en clair)

    @Column(nullable = false, unique = true, length = 30) // Champ obligatoire, unique en BDD et limité à 30 caractères
    private String telephone; // Numéro de téléphone portable (ex: +221770000000)

    @Enumerated(EnumType.STRING) // Stocke la valeur de l'énumération sous forme de texte (STRING) en BDD
    @Column(nullable = false, length = 20) // Champ obligatoire limité à 20 caractères
    private UserRole role; // Rôle applicatif attribué (ROLE_CLIENT, ROLE_LIVREUR, ROLE_ADMIN)

    @Column(nullable = false) // Champ non nul en base de données
    @Builder.Default // Valeur par défaut utilisée par le pattern Builder de Lombok
    private Boolean actif = true; // Indique si le compte est actif (true) ou verrouillé/suspendu (false)

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Champ mappé sur created_at, non modifiable après insertion
    private LocalDateTime createdAt; // Horodatage précis de la création du compte

    @LastModifiedDate // Horodatage injecté automatiquement par Spring Data JPA à chaque mise à jour
    @Column(name = "updated_at") // Champ mappé sur la colonne updated_at
    private LocalDateTime updatedAt; // Horodatage de la dernière modification des informations du compte

}