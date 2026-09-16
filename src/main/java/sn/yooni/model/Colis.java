package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour l'ORM
import lombok.*; // Importation des annotations Lombok pour réduire le code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.annotation.LastModifiedDate; // Importation pour l'horodatage automatique de mise à jour
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.math.BigDecimal; // Importation pour le type décimal précision (argent)
import java.time.LocalDateTime; // Importation pour la gestion des horodatages

@Entity // Indique que la classe est une entité persistante JPA
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate/@LastModifiedDate) pour cette entité
@Table(name = "colis") // Nom de la table SQL correspondante dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génère tous les getters
@Setter // Lombok : Génère tous les setters
@NoArgsConstructor // Lombok : Génère le constructeur sans arguments pour JPA
@AllArgsConstructor // Lombok : Génère le constructeur complet
@Builder // Lombok : Permet d'utiliser le pattern Builder pour créer un colis
public class Colis { // Déclaration de la classe modèle Colis

    @Id // Clé primaire de la table colis
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Clé primaire auto-incrémentée par MySQL
    private Long id; // Identifiant unique du colis

    @OneToOne(fetch = FetchType.LAZY) // Relation 1-à-1 avec la livraison associée
    @JoinColumn(name = "livraison_id", nullable = false, unique = true) // Clé étrangère livraison_id liée à la table livraisons
    private Livraison livraison; // Demande de livraison associée à ce colis

    @Column(length = 255) // Description optionnelle du contenu du paquet
    private String description; // Détails sur le colis (ex: Papiers administratifs, Électronique, Vêtements)

    @Column(name = "poids_kg") // Colonne stockant le poids approximatif en kilogrammes
    private Double poidsKg; // Poids du colis en kg

    @Column(name = "valeur_estimee", precision = 12, scale = 2) // Valeur déclarée pour le remboursement en cas de perte
    private BigDecimal valeurEstimee; // Valeur monétaire estimée en FCFA

    @Column(name = "nom_destinataire", nullable = false, length = 100) // Nom du destinataire obligatoire
    private String nomDestinataire; // Nom complet de la personne devant réceptionner le colis

    @Column(name = "telephone_destinataire", nullable = false, length = 30) // Téléphone du destinataire obligatoire
    private String telephoneDestinataire; // Numéro de téléphone pour contacter le destinataire (ex: +221770000000)

    @Column(name = "code_secret_validation", length = 10) // Code de confirmation requis à la livraison
    private String codeSecretValidation; // OTP/Code PIN (ex: 4 chiffres) fourni par le destinataire au livreur pour valider la remise

    @Enumerated(EnumType.STRING) // Sérialisation en texte dans la base de données
    @Column(nullable = false, length = 30) // Champ statut obligatoire en BDD
    @Builder.Default // Valeur par défaut pour le Builder Lombok
    private StatutColis statut = StatutColis.EN_ATTENTE_RETRAIT; // État d'avancement du colis

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Horodatage de création non modifiable
    private LocalDateTime createdAt; // Date et heure d'enregistrement du colis

    @LastModifiedDate // Horodatage injecté automatiquement par Spring Data JPA à chaque mise à jour
    @Column(name = "updated_at") // Horodatage de dernière modification
    private LocalDateTime updatedAt; // Date et heure de mise à jour du statut du colis

}