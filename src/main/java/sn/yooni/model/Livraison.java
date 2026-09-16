package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la gestion ORM
import lombok.*; // Importation des annotations Lombok pour la génération de code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.annotation.LastModifiedDate; // Importation pour l'horodatage automatique de mise à jour
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.math.BigDecimal; // Importation pour le type décimal précision (argent)
import java.time.LocalDateTime; // Importation du type LocalDateTime pour les horodatages

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate/@LastModifiedDate) pour cette entité
@Table(name = "livraisons") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Génération du constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Génération du constructeur avec tous les arguments
@Builder // Lombok : Implémentation du pattern Builder pour instancier des livraisons
public class Livraison { // Déclaration de la classe modèle Livraison

    @Id // Indique la clé primaire de la table livraisons
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire auto-incrémentée par MySQL
    private Long id; // Identifiant unique de la course ou livraison

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs livraisons peuvent être effectuées par un même client
    @JoinColumn(name = "client_id", nullable = false) // Clé étrangère client_id liée à la table clients
    private Client client; // Profil du client ayant sollicité la course

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs livraisons peuvent être attribuées à un même livreur
    @JoinColumn(name = "livreur_id") // Clé étrangère livreur_id liée à la table livreurs (NULL tant qu'aucun livreur n'a accepté)
    private Livreur livreur; // Profil du chauffeur ou livreur qui prend en charge la course

    @Column(name = "adresse_depart", nullable = false, length = 255) // Libellé textuel du point de départ obligatoire
    private String adresseDepart; // Nom lisible du point de prise en charge (ex: Place de l'Indépendance, Dakar)

    @Column(name = "lat_depart", nullable = false) // Latitude de départ obligatoire
    private Double latDepart; // Coordonnée GPS Latitude de départ

    @Column(name = "lng_depart", nullable = false) // Longitude de départ obligatoire
    private Double lngDepart; // Coordonnée GPS Longitude de départ

    @Column(name = "adresse_arrivee", nullable = false, length = 255) // Libellé textuel de destination obligatoire
    private String adresseArrivee; // Nom lisible du point d'arrivée (ex: Aéroport DSS, Diass)

    @Column(name = "lat_arrivee", nullable = false) // Latitude d'arrivée obligatoire
    private Double latArrivee; // Coordonnée GPS Latitude de destination

    @Column(name = "lng_arrivee", nullable = false) // Longitude d'arrivée obligatoire
    private Double lngArrivee; // Coordonnée GPS Longitude de destination

    @Column(nullable = false, precision = 12, scale = 2) // Montant financier obligatoire en FCFA
    private BigDecimal prix; // Tarif total calculé pour la course

    @Enumerated(EnumType.STRING) // Sérialisation sous forme de chaîne de caractères en base de données
    @Column(nullable = false, length = 30) // Champ statut obligatoire en BDD
    @Builder.Default // Valeur par défaut utilisée par le Builder Lombok
    private StatutLivraison statut = StatutLivraison.EN_ATTENTE; // État initial de la course (EN_ATTENTE par défaut)

    @Column(name = "distance_km") // Colonne enregistrant la distance calculée en kilomètres
    private Double distanceKm; // Distance totale estimée du trajet

    @Column(name = "duree_estimee_min") // Colonne enregistrant le temps estimé en minutes
    private Integer dureeEstimeeMin; // Temps de parcours estimé du trajet

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Horodatage de création, non modifiable après insertion
    private LocalDateTime createdAt; // Date et heure de création de la demande

    @LastModifiedDate // Horodatage injecté automatiquement par Spring Data JPA à chaque mise à jour
    @Column(name = "updated_at") // Horodatage de la dernière mise à jour
    private LocalDateTime updatedAt; // Date et heure de dernière modification de l'état de la course

}