package sn.yooni; // Déclaration du package racine de l'application Yooni

import org.springframework.boot.SpringApplication; // Importation de la classe permettant de lancer l'application Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication; // Importation de l'annotation de configuration automatique Spring Boot
import org.springframework.scheduling.annotation.EnableScheduling; // Importation pour l'activation des tâches arrière-plan planifiées

@SpringBootApplication // Active la configuration automatique, le scan des composants et la déclaration des Beans Spring
// Active le binding par CONSTRUCTEUR de JwtProperties (record) : indispensable pour que
// @Component + record fonctionne, sinon Spring tente d'injecter String/long en tant que beans.

@EnableScheduling // Active le moteur de planification des tâches récurrentes en arrière-plan
public class YooniApplication { 
    public static void main(String[] args) {
        // Démarre l'application Spring, initialise le serveur web embarqué et charge le contexte applicatif
        SpringApplication.run(YooniApplication.class, args);
    }

}