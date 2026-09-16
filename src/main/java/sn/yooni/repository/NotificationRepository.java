package sn.yooni.repository; // Déclaration du package repository (couche d'accès aux données)

import sn.yooni.model.Notification; // Importation de l'entité Notification
import sn.yooni.model.User; // Importation de l'entité User

import org.springframework.data.jpa.repository.JpaRepository; // Interface Spring Data JPA de base
import org.springframework.stereotype.Repository; // Stéréotype marquant le composant comme un bean de dépôt

import java.util.List; // Liste de notifications

@Repository // Enregistre ce dépôt dans le conteneur Spring
public interface NotificationRepository extends JpaRepository<Notification, Long> { // Dépôt JPA lié à l'entité Notification

    // Notifications d'un utilisateur, les plus récentes en premier
    List<Notification> findByUserOrderByCreatedAtDesc(User user); // File de notifications du compte

    // Notifications non lues d'un utilisateur (badge de compteur)
    List<Notification> findByUserAndLueFalseOrderByCreatedAtDesc(User user); // Alertes en attente de lecture

    // Compteur de notifications non lues pour l'interface du client/livreur/admin
    long countByUserAndLueFalse(User user); // Nombre d'alertes à afficher

}