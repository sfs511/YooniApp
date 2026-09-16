package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importation pour éviter les erreurs de sérialisation des proxies Hibernate
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Importation des annotations Lombok pour réduire le code répétitif
import org.springframework.data.annotation.CreatedDate; // Importation pour l'horodatage automatique de création
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Importation de l'écouteur JPA d'audit

import java.time.LocalDateTime; // Importation pour la gestion des horodatages

@Entity // Indique que cette classe est une entité JPA mappée en base de données
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (@CreatedDate) pour cette entité
@Table(name = "notifications") // Nom de la table SQL associée dans MySQL
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore les proxies Hibernate lors de la sérialisation JSON
@Getter // Lombok : Génération automatique de tous les Getters
@Setter // Lombok : Génération automatique de tous les Setters
@NoArgsConstructor // Lombok : Constructeur sans arguments requis par JPA
@AllArgsConstructor // Lombok : Constructeur avec tous les arguments
@Builder // Lombok : Pattern Builder pour instancier des notifications proprement
public class Notification { // Déclaration de la classe modèle Notification

    @Id // Clé primaire de la table notifications
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentation gérée par MySQL
    private Long id; // Identifiant unique de la notification

    @ManyToOne(fetch = FetchType.LAZY) // Plusieurs notifications peuvent être destinées à un même utilisateur
    @JoinColumn(name = "user_id", nullable = false) // Clé étrangère user_id liée à la table users
    private User user; // Destinataire de la notification

    @Column(nullable = false, length = 150) // Titre de la notification obligatoire, max 150 caractères
    private String titre; // Intitulé court de la notification (ex: "Chauffeur en route")

    @Column(nullable = false, columnDefinition = "TEXT") // Contenu textuel complet de la notification
    private String message; // Corps du message d'information ou d'alerte

    @Enumerated(EnumType.STRING) // Sérialisation sous forme de texte en BDD
    @Column(name = "type_notification", nullable = false, length = 40) // Champ obligatoire en BDD
    private TypeNotification typeNotification; // Catégorie du message (COURSE_ACCEPTEE, LIVREUR_ARRIVE, etc.)

    @Column(nullable = false) // Statut de lecture de la notification
    @Builder.Default // Valeur par défaut pour le Builder Lombok
    private Boolean lue = false; // Indique si l'utilisateur a lu la notification (false par défaut)

    @CreatedDate // Horodatage injecté automatiquement par Spring Data JPA à l'insertion
    @Column(name = "created_at", updatable = false) // Horodatage d'envoi non modifiable
    private LocalDateTime createdAt; // Date et heure de création de la notification

}