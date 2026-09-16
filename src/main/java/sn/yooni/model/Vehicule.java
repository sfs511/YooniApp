package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance des données
import lombok.*; // Importation des annotations Lombok pour la génération automatique du code répétitif

@Entity // Indique à JPA que cette classe correspond à une entité persistante
@Table(name = "vehicules") // Mappe l'entité sur la table SQL vehicules dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génère automatiquement tous les getters
@Setter // Lombok : Génère automatiquement tous les setters
@NoArgsConstructor // Lombok : Génère le constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Génère un constructeur contenant l'ensemble des champs
@Builder // Lombok : Implémente le pattern Builder pour créer des objets Vehicule de façon fluide
public class Vehicule { // Déclaration de la classe modèle Vehicule

    @Id // Identifie ce champ comme la clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire auto-incrémentée par MySQL
    private Long id; // Identifiant unique du véhicule

    @OneToOne(fetch = FetchType.LAZY) // Association un-à-un avec le livreur, chargement différé
    @JoinColumn(name = "livreur_id", nullable = false, unique = true) // Clé étrangère livreur_id pointant vers la table livreurs
    private Livreur livreur; // Livreur ou chauffeur auquel appartient ce véhicule

    @Column(nullable = false, length = 50) // Champ obligatoire en base de données, max 50 caractères
    private String marque; // Marque du véhicule (ex: Toyota, Peugeot, Yamaha)

    @Column(nullable = false, length = 50) // Champ obligatoire en base de données, max 50 caractères
    private String modele; // Modèle du véhicule (ex: Corolla, Duster, Jakarta)

    @Column(name = "immatriculation", nullable = false, unique = true, length = 30) // Immatriculation obligatoire et unique
    private String immatriculation; // Plaque d'immatriculation officielle du véhicule

    @Enumerated(EnumType.STRING) // Sérialisation de l'énumération sous forme de texte en BDD
    @Column(name = "type_vehicule", nullable = false, length = 30) // Champ type_vehicule obligatoire en BDD
    private TypeVehicule typeVehicule; // Catégorie du véhicule (MOTO, BERLINE, SUV, CAMIONETTE)

    @Column(length = 30) // Couleur optionnelle, max 30 caractères
    private String couleur; // Couleur du véhicule pour la reconnaissance visuelle par le client
}