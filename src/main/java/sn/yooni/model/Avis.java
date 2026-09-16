package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import jakarta.validation.constraints.Max; // Importation pour la validation de la valeur maximale
import jakarta.validation.constraints.Min; // Importation pour la validation de la valeur minimale
import lombok.*; // Importation des annotations Lombok pour éviter le code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.LocalDateTime; // Importation pour la gestion des horodatages

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate) pour cette entité
@Table(name = "avis") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des avis proprement
public class Avis { // Déclaration de la classe modèle Avis

    @Id // Clé primaire de la table avis
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique de l'avis

    @OneToOne(fetch = FetchType.LAZY) // Un avis est associé à une seule livraison
    @JoinColumn(name = "livraison_id", nullable = false, unique = true) // Clé étrangère livraison_id liée à la table livraisons
    private Livraison livraison; // Course ou livraison concernée par l'évaluation

    @ManyToOne(fetch = FetchType.LAZY) // Un client peut donner plusieurs avis
    @JoinColumn(name = "client_id", nullable = false) // Clé étrangère client_id liée à la table clients
    private Client client; // Client auteur de l'évaluation

    @ManyToOne(fetch = FetchType.LAZY) // Un livreur peut recevoir plusieurs avis
    @JoinColumn(name = "livreur_id", nullable = false) // Clé étrangère livreur_id liée à la table livreurs
    private Livreur livreur; // Livreur ou chauffeur évalué

    @Min(1) // Validation : la note minimale autorisée est 1 étoile
    @Max(5) // Validation : la note maximale autorisée est 5 étoiles
    @Column(nullable = false) // Note obligatoire
    private Integer note; // Note attribuée dans un intervalle de 1 à 5 étoiles

    @Column(length = 500) // Commentaire optionnel limité à 500 caractères
    private String commentaire; // Avis textuel ou retour d'expérience rédigé par le client

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Date de création non modifiable
    private LocalDateTime createdAt; // Horodatage du dépôt de l'avis

}