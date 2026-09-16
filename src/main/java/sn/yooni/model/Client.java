package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations de persistance JPA
import lombok.*; // Importation des annotations Lombok pour la génération automatique du code répétitif

@Entity // Indique que cette classe est une entité JPA mappée sur une table de la base de données
@Table(name = "clients") // Spécifie le nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génère automatiquement tous les getters
@Setter // Lombok : Génère automatiquement tous les setters
@NoArgsConstructor // Lombok : Génère le constructeur sans arguments pour Hibernate/JPA
@AllArgsConstructor // Lombok : Génère un constructeur avec tous les champs
@Builder // Lombok : Implémente le pattern Builder pour instancier des clients proprement
public class Client { // Déclaration de la classe modèle Client

    @Id // Indique la clé primaire de la table clients
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire auto-incrémentée par la base de données MySQL
    private Long id; // Identifiant unique du profil client

    @OneToOne(fetch = FetchType.LAZY) // Association un-à-un avec le compte utilisateur, chargement paresseux pour les performances
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Clé étrangère user_id liant le client à la table users
    private User user; // Compte utilisateur d'accès associé à ce profil client

    @Column(nullable = false, length = 100) // Champ obligatoire en BDD, limité à 100 caractères
    private String nom; // Nom de famille du client

    @Column(nullable = false, length = 100) // Champ obligatoire en BDD, limité à 100 caractères
    private String prenom; // Prénom du client

    @Column(name = "photo_url", length = 255) // Mappe sur la colonne photo_url pour stocker l'URL ou chemin de l'image
    private String photoUrl; // Lien vers la photo de profil du client
}