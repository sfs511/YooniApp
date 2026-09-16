package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance en base de données
import lombok.*; // Importation des annotations Lombok pour réduire le code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.annotation.LastModifiedDate; // Importation pour l'horodatage automatique de mise à jour
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.math.BigDecimal; // Importation pour le type décimal précision (argent)
import java.time.LocalDateTime; // Importation pour la gestion des horodatages

@Entity // Indique que cette classe est une entité persistante JPA
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate/@LastModifiedDate) pour cette entité
@Table(name = "paiements") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des paiements de façon fluide
public class Paiement { // Déclaration de la classe modèle Paiement

    @Id // Marque le champ comme clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique du paiement

    @OneToOne(fetch = FetchType.LAZY) // Relation un-à-un unique avec la livraison associée
    @JoinColumn(name = "livraison_id", nullable = false, unique = true) // Clé étrangère livraison_id liée à la table livraisons
    private Livraison livraison; // Demande de course ou livraison concernée par ce paiement

    @Column(nullable = false, precision = 12, scale = 2) // Montant obligatoire
    private BigDecimal montant; // Montant de la transaction réglé en FCFA

    @Enumerated(EnumType.STRING) // Sérialisation de l'énumération sous forme de texte en BDD
    @Column(name = "moyen_paiement", nullable = false, length = 50) // Moyen de paiement obligatoire
    private MoyenPaiement moyenPaiement; // Méthode utilisée (WAVE, ORANGE_MONEY, FREE_MONEY, ESPECES)

    @Enumerated(EnumType.STRING) // Sérialisation de l'énumération sous forme de texte en BDD
    @Column(name = "statut_paiement", nullable = false, length = 30) // Statut financier obligatoire
    @Builder.Default // Valeur par défaut pour le Builder Lombok
    private StatutPaiement statutPaiement = StatutPaiement.EN_ATTENTE; // État du paiement (EN_ATTENTE, PAYE, ECHOUE, REMBOURSE)

    @Column(name = "reference_transaction", length = 100) // Référence externe du fournisseur de paiement
    private String referenceTransaction; // Identifiant unique renvoyé par l'API Wave ou Mobile Money

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Date de création non modifiable
    private LocalDateTime createdAt; // Horodatage d'initialisation du paiement

    @LastModifiedDate // Horodatage injecté automatiquement par Spring Data JPA à chaque mise à jour
    @Column(name = "updated_at") // Date de mise à jour
    private LocalDateTime updatedAt; // Horodatage du changement d'état du paiement

}