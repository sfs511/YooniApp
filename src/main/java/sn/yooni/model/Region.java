package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Ignore les proxies Hibernate lors de la sérialisation JSON
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Lombok : réduction de code répétitif

@Entity // Classe JPA mappée sur la table regions
@Table(name = "regions") // Table des régions géographiques couvertes par la plateforme
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Sérialisation propre
@Getter @Setter // Accesseurs et mutateurs
@NoArgsConstructor // Constructeur sans arguments requis par JPA
@AllArgsConstructor // Constructeur avec tous les arguments
@Builder // Pattern Builder
public class Region { // Région géographique d'activité (ex : Dakar, Thiès, Saint-Louis)

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentée par MySQL
    private Long id; // Identifiant unique de la région

    @Column(nullable = false, unique = true, length = 50) // Code court unique (ex : DKR, THS)
    private String code; // Code abrégé de la région

    @Column(nullable = false, length = 100) // Nom complet de la région
    private String nom; // Libellé affiché (ex : Région de Dakar)

    @Column(length = 80) // Pays de la région (ex : Sénégal)
    private String pays;

    @Column(columnDefinition = "TEXT") // Description optionnelle
    private String description; // Détails géographiques ou administratifs

}