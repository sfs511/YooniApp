package sn.yooni.model; // Déclaration du package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Ignore les proxies Hibernate lors de la sérialisation JSON
import jakarta.persistence.*; // Importation des annotations JPA pour la persistance
import lombok.*; // Lombok : réduction de code répétitif

@Entity // Classe JPA mappée sur la table modes_paiement
@Table(name = "modes_paiement") // Table des modes de paiement supportés par la plateforme
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Sérialisation propre
@Getter @Setter // Accesseurs et mutateurs
@NoArgsConstructor // Constructeur sans arguments requis par JPA
@AllArgsConstructor // Constructeur avec tous les arguments
@Builder // Pattern Builder
public class ModePaiement { // Mode de paiement disponible (ex : Wave, Orange Money, Espèces)

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrémentée par MySQL
    private Long id; // Identifiant unique du mode de paiement

    @Column(nullable = false, unique = true, length = 50) // Nom unique (ex : WAVE, ORANGE_MONEY)
    private String nom; // Libellé affiché du mode de paiement

    @Column(columnDefinition = "TEXT") // Description ou instruction d'utilisation
    private String description; // Détails sur le mode de paiement

    @Column(nullable = false) // Indique si le mode de paiement est actuellement accepté
    @Builder.Default // Valeur par défaut pour le builder Lombok
    private Boolean actif = true; // Actif par défaut

}