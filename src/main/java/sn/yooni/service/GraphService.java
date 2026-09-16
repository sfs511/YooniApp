package sn.yooni.service; // Déclaration du package service (logique métier)

import org.springframework.stereotype.Service; // Stéréotype service métier

@Service // Bean service enregistré dans le conteneur Spring
public class GraphService { // Services de modélisation du réseau routier (graphe)

    // TODO Nouvelle étape : construire et interroger le graphe routier :
    // - noeuds (intersections), arêtes (routes) avec pondération distance/temps
    // - plus court chemin (Dijkstra/A*) entre deux adresses
    // - précalcul des itinéraires optimaux pour l'optimisation des courses
}