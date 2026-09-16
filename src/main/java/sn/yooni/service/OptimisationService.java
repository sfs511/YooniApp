package sn.yooni.service; // Déclaration du package service (logique métier)

import org.springframework.stereotype.Service; // Stéréotype service métier

@Service // Bean service enregistré dans le conteneur Spring
public class OptimisationService { // Services d'optimisation de l'attribution des courses

    // TODO Nouvelle étape : injecter LivreurRepository + PositionRepository puis implémenter :
    // - sélection du livreur le plus proche et disponible pour une demande
    // - calcul de l'E.T.A. (arrivée estimée) à partir des positions GPS et du graphe routier
    // - (à terme) répartition optimale multi-courses (moteur d'assignation en lot)
}