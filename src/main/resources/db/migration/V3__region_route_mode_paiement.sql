-- =====================================================================
-- V3 : Tables Region, Route, ModePaiement
-- Ajoute les entités manquantes (Region, Route, ModePaiement)
-- pour éviter un échec de ddl-auto=validate
-- =====================================================================

-- Table des régions géographiques couvertes par la plateforme
CREATE TABLE regions (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    code          VARCHAR(50)  NOT NULL,
    nom           VARCHAR(100) NOT NULL,
    pays          VARCHAR(80),
    description   TEXT,
    PRIMARY KEY (id),
    UNIQUE KEY uk_regions_code (code)  -- Code région unique (ex : DKR, THS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des routes prédéfinies entre régions (courses inter-régionales)
CREATE TABLE routes (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    region_depart_id    BIGINT        NOT NULL,
    region_arrivee_id   BIGINT        NOT NULL,
    distance_km         DOUBLE,
    duree_estimee_min   INT,
    prix_base_fcfa      DECIMAL(12,2),
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_routes_region_depart  FOREIGN KEY (region_depart_id)  REFERENCES regions(id),
    CONSTRAINT fk_routes_region_arrivee FOREIGN KEY (region_arrivee_id) REFERENCES regions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table des modes de paiement supportés par la plateforme
CREATE TABLE modes_paiement (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nom         VARCHAR(50)  NOT NULL,
    description TEXT,
    actif       BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    UNIQUE KEY uk_modes_paiement_nom (nom)  -- Nom unique (ex : WAVE, ORANGE_MONEY)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insertion des modes de paiement de base du Sénégal
INSERT INTO modes_paiement (nom, description) VALUES
    ('WAVE',         'Paiement mobile via Wave (très répandu au Sénégal)'),
    ('ORANGE_MONEY', 'Paiement mobile via Orange Money (opérateur Orange Sénégal)'),
    ('FREE_MONEY',   'Paiement mobile via Free Money (opérateur Free Sénégal)'),
    ('ESPECES',      'Paiement en espèces remis directement au livreur');