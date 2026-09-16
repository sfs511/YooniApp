package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Importation des annotations Lombok pour éviter le code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.LocalDateTime; // Importation pour la gestion de la date et l'heure

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate) pour cette entité
@Table(name = "positions") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des positions GPS proprement
public class Position { // Déclaration de la classe modèle Position

    @Id // Clé primaire de la table positions
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique du relevé GPS

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs positions GPS peuvent appartenir au même livreur
    @JoinColumn(name = "livreur_id", nullable = false) // Clé étrangère livreur_id liée à la table livreurs
    private Livreur livreur; // Chauffeur ou livreur ayant émis ce signal GPS

    @Column(nullable = false) // Coordonnée latitude obligatoire
    private Double latitude; // Coordonnée GPS Latitude transmise

    @Column(nullable = false) // Coordonnée longitude obligatoire
    private Double longitude; // Coordonnée GPS Longitude transmise

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion (si vide)
    @Column(nullable = false, updatable = false) // Horodatage fixe du relevé
    private LocalDateTime horodatage; // Instant précis de la capture des coordonnées GPS

}