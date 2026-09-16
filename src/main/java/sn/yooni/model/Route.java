package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Ignore les proxies Hibernate lors de la sérialisation JSON
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Lombok : réduction de code répétitif

@Entity // Classe JPA mappée sur la table routes
@Table(name = "routes") // Table des routes prédéfinies entre régions
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Sérialisation propre
@Getter @Setter // Accesseurs et mutateurs
@NoArgsConstructor // Constructeur sans arguments requis par JPA
@AllArgsConstructor // Constructeur avec tous les arguments
@Builder // Pattern Builder
public class Route { // Route prédéfinie entre deux régions (pour les courses inter-régionales)

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentée par MySQL
    private Long id; // Identifiant unique de la route

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs routes peuvent appartenir à une même région de départ
    @JoinColumn(name = "region_depart_id", nullable = false) // Clé étrangère vers regions
    private Region regionDepart; // Région d'origine

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs routes peuvent desservir une même région d'arrivée
    @JoinColumn(name = "region_arrivee_id", nullable = false) // Clé étrangère vers regions
    private Region regionArrivee; // Région de destination

    @Column(name = "distance_km") // Distance en kilomètres de la route
    private Double distanceKm; // Parcourir cette distance entre les deux régions

    @Column(name = "duree_estimee_min") // Temps estimé en minutes pour ce trajet
    private Integer dureeEstimeeMin; // Durée standard du parcours

    @Column(name = "prix_base_fcfa", precision = 12, scale = 2) // Tarif de base de la route en FCFA
    private java.math.BigDecimal prixFcfa; // Prix fixe ou minimum pour cette route

    @Column(nullable = false) // Indique si la route est active et exploitée
    @Builder.Default // Valeur par défaut pour le builder Lombok
    private Boolean active = true; // Route active par défaut

}