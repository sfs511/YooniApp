-- ==========================================
-- YOONI - Migration V1 : Création initiale du schéma
-- Plateforme VTC & Livraison (Sénégal)
-- ==========================================

-- -----------------------------------------------------------
-- Table users : comptes utilisateurs (base de l'authentification)
-- -----------------------------------------------------------
CREATE TABLE users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    telephone     VARCHAR(30)  NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    actif         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_telephone UNIQUE (telephone)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table clients : profils des clients (1:1 avec users)
-- -----------------------------------------------------------
CREATE TABLE clients (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    user_id   BIGINT       NOT NULL,
    nom       VARCHAR(100) NOT NULL,
    prenom    VARCHAR(100) NOT NULL,
    photo_url VARCHAR(255),
    CONSTRAINT pk_clients PRIMARY KEY (id),
    CONSTRAINT uk_clients_user UNIQUE (user_id),
    CONSTRAINT fk_clients_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table livreurs : profils des chauffeurs / livreurs (1:1 avec users)
-- -----------------------------------------------------------
CREATE TABLE livreurs (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    photo_url     VARCHAR(255),
    permis_numero VARCHAR(50)  NOT NULL,
    statut        VARCHAR(30)  NOT NULL,
    note_moyenne  DOUBLE,
    disponible    BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_livreurs PRIMARY KEY (id),
    CONSTRAINT uk_livreurs_user UNIQUE (user_id),
    CONSTRAINT uk_livreurs_permis UNIQUE (permis_numero),
    CONSTRAINT fk_livreurs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table vehicules : véhicules des livreurs (1:1 avec livreurs)
-- -----------------------------------------------------------
CREATE TABLE vehicules (
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    livreur_id     BIGINT      NOT NULL,
    marque         VARCHAR(50) NOT NULL,
    modele         VARCHAR(50) NOT NULL,
    immatriculation VARCHAR(30) NOT NULL,
    type_vehicule  VARCHAR(30) NOT NULL,
    couleur        VARCHAR(30),
    CONSTRAINT pk_vehicules PRIMARY KEY (id),
    CONSTRAINT uk_vehicules_livreur UNIQUE (livreur_id),
    CONSTRAINT uk_vehicules_immatriculation UNIQUE (immatriculation),
    CONSTRAINT fk_vehicules_livreur FOREIGN KEY (livreur_id) REFERENCES livreurs (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table livraisons : courses / livraisons
-- -----------------------------------------------------------
CREATE TABLE livraisons (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    client_id        BIGINT       NOT NULL,
    livreur_id       BIGINT,
    adresse_depart   VARCHAR(255) NOT NULL,
    lat_depart       DOUBLE       NOT NULL,
    lng_depart       DOUBLE       NOT NULL,
    adresse_arrivee  VARCHAR(255) NOT NULL,
    lat_arrivee      DOUBLE       NOT NULL,
    lng_arrivee      DOUBLE       NOT NULL,
    prix             DOUBLE       NOT NULL,
    statut           VARCHAR(30)  NOT NULL,
    distance_km      DOUBLE,
    duree_estimee_min INT,
    created_at       DATETIME(6),
    updated_at       DATETIME(6),
    CONSTRAINT pk_livraisons PRIMARY KEY (id),
    CONSTRAINT fk_livraisons_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_livraisons_livreur FOREIGN KEY (livreur_id) REFERENCES livreurs (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table colis : colis associés aux livraisons (1:1 avec livraisons)
-- -----------------------------------------------------------
CREATE TABLE colis (
    id                     BIGINT       NOT NULL AUTO_INCREMENT,
    livraison_id           BIGINT       NOT NULL,
    description            VARCHAR(255),
    poids_kg               DOUBLE,
    valeur_estimee         DOUBLE,
    nom_destinataire       VARCHAR(100) NOT NULL,
    telephone_destinataire VARCHAR(30)  NOT NULL,
    code_secret_validation VARCHAR(10),
    statut                 VARCHAR(30)  NOT NULL,
    created_at             DATETIME(6),
    updated_at             DATETIME(6),
    CONSTRAINT pk_colis PRIMARY KEY (id),
    CONSTRAINT uk_colis_livraison UNIQUE (livraison_id),
    CONSTRAINT fk_colis_livraison FOREIGN KEY (livraison_id) REFERENCES livraisons (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table paiements : paiements des livraisons (1:1 avec livraisons)
-- -----------------------------------------------------------
CREATE TABLE paiements (
    id                   BIGINT       NOT NULL AUTO_INCREMENT,
    livraison_id         BIGINT       NOT NULL,
    montant              DOUBLE       NOT NULL,
    moyen_paiement       VARCHAR(50)  NOT NULL,
    statut_paiement      VARCHAR(30)  NOT NULL,
    reference_transaction VARCHAR(100),
    created_at           DATETIME(6),
    updated_at           DATETIME(6),
    CONSTRAINT pk_paiements PRIMARY KEY (id),
    CONSTRAINT uk_paiements_livraison UNIQUE (livraison_id),
    CONSTRAINT fk_paiements_livraison FOREIGN KEY (livraison_id) REFERENCES livraisons (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table avis : évaluations des livraisons
-- -----------------------------------------------------------
CREATE TABLE avis (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    livraison_id BIGINT      NOT NULL,
    client_id   BIGINT       NOT NULL,
    livreur_id  BIGINT       NOT NULL,
    note        INT          NOT NULL,
    commentaire VARCHAR(500),
    created_at  DATETIME(6),
    CONSTRAINT pk_avis PRIMARY KEY (id),
    CONSTRAINT uk_avis_livraison UNIQUE (livraison_id),
    CONSTRAINT fk_avis_livraison FOREIGN KEY (livraison_id) REFERENCES livraisons (id),
    CONSTRAINT fk_avis_client FOREIGN KEY (client_id) REFERENCES clients (id),
    CONSTRAINT fk_avis_livreur FOREIGN KEY (livreur_id) REFERENCES livreurs (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table notifications : notifications destinées aux utilisateurs
-- -----------------------------------------------------------
CREATE TABLE notifications (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    titre             VARCHAR(150) NOT NULL,
    message           TEXT         NOT NULL,
    type_notification VARCHAR(40)  NOT NULL,
    lue               BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        DATETIME(6),
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table positions : historique GPS des livreurs
-- -----------------------------------------------------------
CREATE TABLE positions (
    id          BIGINT     NOT NULL AUTO_INCREMENT,
    livreur_id  BIGINT     NOT NULL,
    latitude    DOUBLE     NOT NULL,
    longitude   DOUBLE     NOT NULL,
    horodatage  DATETIME(6) NOT NULL,
    CONSTRAINT pk_positions PRIMARY KEY (id),
    CONSTRAINT fk_positions_livreur FOREIGN KEY (livreur_id) REFERENCES livreurs (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table refresh_tokens : jetons de rafraîchissement JWT (1:1 avec users)
-- -----------------------------------------------------------
CREATE TABLE refresh_tokens (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    token       VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL,
    expiry_date DATETIME(6)  NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6),
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT uk_refresh_tokens_user UNIQUE (user_id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- -----------------------------------------------------------
-- Table audit_logs : journal d'audit de sécurité
-- -----------------------------------------------------------
CREATE TABLE audit_logs (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT,
    username   VARCHAR(180),
    action     VARCHAR(100) NOT NULL,
    entite     VARCHAR(100),
    entite_id  BIGINT,
    ip_adresse VARCHAR(45),
    details    TEXT,
    created_at DATETIME(6),
    CONSTRAINT pk_audit_logs PRIMARY KEY (id)
) ENGINE = InnoDB;