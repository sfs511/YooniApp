package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la gestion ORM avec la base de données
import lombok.*; // Importation des annotations Lombok pour la génération automatique du code répétitif
import java.math.BigDecimal; // Importation pour le type décimal précision

@Entity // Indique que cette classe est une entité JPA mappée sur la base de données
@Table(name = "livreurs") // Spécifie le nom de la table SQL MySQL associée
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génère automatiquement les méthodes Getters
@Setter // Lombok : Génère automatiquement les méthodes Setters
@NoArgsConstructor // Lombok : Génère le constructeur sans arguments exigé par JPA/Hibernate
@AllArgsConstructor // Lombok : Génère le constructeur contenant tous les arguments
@Builder // Lombok : Permet d'utiliser le pattern Builder pour instancier des livreurs
public class Livreur { // Déclaration de la classe modèle Livreur

    @Id // Marque le champ comme clé primaire de la table livreurs
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire auto-incrémentée par la base MySQL
    private Long id; // Identifiant unique du chauffeur ou livreur

    @OneToOne(fetch = FetchType.LAZY) // Relation un-à-un avec le compte utilisateur parent, chargement paresseux
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Clé étrangère user_id liant le livreur à la table users
    private User user; // Compte utilisateur d'accès système associé

    @Column(nullable = false, length = 100) // Champ nom obligatoire, limité à 100 caractères
    private String nom; // Nom de famille du livreur

    @Column(nullable = false, length = 100) // Champ prénom obligatoire, limité à 100 caractères
    private String prenom; // Prénom du livreur

    @Column(name = "photo_url", length = 255) // URL de la photo de profil du chauffeur
    private String photoUrl; // Lien vers l'image de profil hébergée

    @Column(name = "permis_numero", nullable = false, unique = true, length = 50) // Numéro de permis unique et obligatoire
    private String permisNumero; // Numéro officiel du permis de conduire du chauffeur

    @Enumerated(EnumType.STRING) // Stocke l'énumération sous forme de chaîne de caractères en base SQL
    @Column(nullable = false, length = 30) // Champ statut obligatoire en BDD
    @Builder.Default // Valeur par défaut gérée lors de l'instanciation via le builder Lombok
    private StatutLivreur statut = StatutLivreur.INACTIF; // État opérationnel (DISPONIBLE, EN_COURSE, INACTIF, SUSPENDU)

    @Column(name = "note_moyenne", precision = 2, scale = 1) // Colonne stockant la note moyenne attribuée par les utilisateurs
    @Builder.Default // Valeur par défaut gérée par le builder Lombok
    private BigDecimal noteMoyenne = BigDecimal.valueOf(5.0); // Note moyenne du chauffeur sur 5 (initialisée par défaut à 5.0)

    @Column(nullable = false) // Indique si le livreur a activé sa disponibilité dans l'application
    @Builder.Default // Valeur par défaut gérée par le builder Lombok
    private Boolean disponible = false; // Indique si le livreur accepte les courses (false par défaut)

}