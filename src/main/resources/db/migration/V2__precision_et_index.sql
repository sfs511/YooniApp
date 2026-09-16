-- ==========================================
-- YOONI - Migration V2 : Précision décimale & index de performance
-- ==========================================

-- -------------------------------------------
-- Conversion des champs monétaires en DECIMAL
-- (Double → DECIMAL pour éviter les pertes d'arrondi)
-- -------------------------------------------
ALTER TABLE livraisons   MODIFY prix          DECIMAL(12,2) NOT NULL;
ALTER TABLE paiements    MODIFY montant       DECIMAL(12,2) NOT NULL;
ALTER TABLE colis        MODIFY valeur_estimee DECIMAL(12,2);
ALTER TABLE livreurs     MODIFY note_moyenne  DECIMAL(2,1);

-- -------------------------------------------
-- Index de performance sur les colonnes filtrées
-- -------------------------------------------
CREATE INDEX idx_livraisons_statut       ON livraisons (statut);
CREATE INDEX idx_livraisons_client       ON livraisons (client_id);
CREATE INDEX idx_colis_statut            ON colis (statut);
CREATE INDEX idx_notifications_user_lue  ON notifications (user_id, lue);
CREATE INDEX idx_positions_livreur_horodatage ON positions (livreur_id, horodatage);
CREATE INDEX idx_audit_logs_user         ON audit_logs (user_id);
CREATE INDEX idx_audit_logs_action       ON audit_logs (action);
