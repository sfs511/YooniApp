package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Client; // Importation de l'entité Client
import sn.yooni.model.Livraison; // Importation de l'entité Livraison
import sn.yooni.model.Livreur; // Importation de l'entité Livreur
import sn.yooni.model.StatutLivraison; // Importation de l'énumération StatutLivraison

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.data.jpa.repository.Modifying; // Marque une requête de modification (UPDATE/DELETE)
import org.springframework.data.jpa.repository.Query; // Requête JPQL personnalisée
import org.springframework.data.repository.query.Param; // Paramètre nommé des requêtes JPQL
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.List; // Liste de livraisons

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface LivraisonRepository extends JpaRepository<Livraison, Long> { // Dépôt JPA lié à l'entité Livraison

    // Historique des courses d'un client
    List<Livraison> findByClientOrderByCreatedAtDesc(Client client); // Tri par date de création décroissante

    // Historique des courses acceptées ou effectuées par un livreur
    List<Livraison> findByLivreurOrderByCreatedAtDesc(Livreur livreur); // Tri par date de création décroissante

    // Acceptation atomique d'une course : l'UPDATE conditionnel est exécuté comme une seule
    // instruction SQL, ce qui empêche deux livreurs d'accepter simultanément la même demande.
    // Retourne 1 si exactement une ligne était encore EN_ATTENTE (mise à jour faite),
    // ou 0 si la course a déjà été prise entre-temps (course non modifiée).
    @Modifying(clearAutomatically = true) // Vide le contexte de persistance après l'UPDATE (les états en mémoire sont périmés)
    @Query("UPDATE Livraison l SET l.livreur = :livreur, l.statut = :nouveau WHERE l.id = :id AND l.statut = :attendu")
    int claimByIdAndStatut(@Param("id") Long id,
                           @Param("livreur") Livreur livreur,
                           @Param("attendu") StatutLivraison attendu,
                           @Param("nouveau") StatutLivraison nouveau);

    // Livraisons par état (EN_ATTENTE, EN_COURSE, TERMINEE, ANNULÉE...)
    List<Livraison> findByStatut(StatutLivraison statut); // Filtrer par statut unique

    // Nombre de livraisons en attente d'un livreur disponible (file d'attente des demandes)
    long countByStatut(StatutLivraison statut); // Compteur rapide pour les tableaux de bord

    // Requête JPQL : date/heure de la demande de livraison la plus récente d'un client
    @Query("SELECT MAX(l.createdAt) FROM Livraison l WHERE l.client = :client")
    java.time.LocalDateTime findLastRequestDate(@Param("client") Client client); // Dernière demande du client

}