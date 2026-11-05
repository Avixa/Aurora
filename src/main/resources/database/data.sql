-- =============================================================================
--  ALTN72 - Projet final 2026-27 - Données de démonstration (PostgreSQL)
--  À exécuter après schema.sql. Peut aussi être rejoué seul pour remettre les
--  données à zéro (TRUNCATE ci-dessous).
--
--  Toutes les dates sont calculées à partir de l'instant présent (§4) : les
--  indicateurs d'urgence sont donc justes au moment de l'exécution. Rejouez ce
--  script juste avant une démonstration (ou laissez Spring le rejouer à chaque
--  démarrage avec spring.sql.init.mode=always).
--
--  Comptes de test (mots de passe hachés en BCrypt, coût 10)
--    responsable / Responsable#2026  -> Camille, RESPONSABLE_EXPLOITATION
--    agent       / Agent#2026        -> Lucas,   AGENT
--    agent2      / Agent#2026        -> Inès,    AGENT
-- =============================================================================

-- Les colonnes TIMESTAMP n'ont pas de fuseau : LOCALTIMESTAMP doit être évalué
-- dans le même fuseau que l'horloge (java.time.Clock) de l'application.
SET TIME ZONE 'Europe/Paris';

TRUNCATE
    evenement_incident,
    evaluation,
    incident,
    prestataire_type_equipement,
    equipement,
    prestataire,
    type_equipement,
    station,
    utilisateur
RESTART IDENTITY CASCADE;


-- -----------------------------------------------------------------------------
--  Utilisateurs
-- -----------------------------------------------------------------------------
INSERT INTO utilisateur (login, mot_de_passe, prenom, profil) VALUES
    ('responsable', '$2a$10$TAvnUmcqeR2mEW0YPeZeie2678N5u.GSAjoyktsK3PKQy458OQe/S', 'Camille', 'RESPONSABLE_EXPLOITATION'),
    ('agent',       '$2a$10$9atD8SpjAkgHkENBt07is.JkmSn/UofOKT9shOVGdp67EgZ7Fd8Ae', 'Lucas',   'AGENT'),
    ('agent2',      '$2a$10$9atD8SpjAkgHkENBt07is.JkmSn/UofOKT9shOVGdp67EgZ7Fd8Ae', 'Inès',    'AGENT');


-- -----------------------------------------------------------------------------
--  Stations (coordonnées fictives pour la piste 4)
-- -----------------------------------------------------------------------------
INSERT INTO station (nom, latitude, longitude) VALUES
    ('Gare Centrale',        48.8443, 2.3744),
    ('Place des Arts',       48.8606, 2.3376),
    ('Université',           48.8462, 2.3448),
    ('Hôtel de Ville',       48.8566, 2.3522),
    ('Parc des Expositions', 48.8323, 2.2875),
    ('Les Docks',            48.8977, 2.3800);


-- -----------------------------------------------------------------------------
--  Types d'équipements
-- -----------------------------------------------------------------------------
INSERT INTO type_equipement (code, libelle) VALUES
    ('ASCENSEUR',          'Ascenseur'),
    ('ESCALIER_MECANIQUE', 'Escalier mécanique'),
    ('PORTIQUE',           'Portique de validation'),
    ('DISTRIBUTEUR',       'Distributeur de titres de transport'),
    ('ECRAN',              'Écran d''information voyageurs');


-- -----------------------------------------------------------------------------
--  Équipements
--  L'état de fonctionnement est cohérent avec les incidents actifs ci-dessous.
-- -----------------------------------------------------------------------------
INSERT INTO equipement (reference, libelle, description, type_equipement_id, station_id, etat_fonctionnement)
SELECT v.reference, v.libelle, v.description, t.id, s.id, v.etat
FROM (VALUES
    ('ASC-GCE-01', 'Ascenseur quai A',              'Relie le niveau rue au quai A.',            'ASCENSEUR',          'Gare Centrale',        'HORS_SERVICE'),
    ('ESC-GCE-01', 'Escalier mécanique sortie nord', NULL,                                        'ESCALIER_MECANIQUE', 'Gare Centrale',        'HORS_SERVICE'),
    ('POR-GCE-01', 'Portique ligne 1',              'Batterie de six portiques, accès ligne 1.', 'PORTIQUE',           'Gare Centrale',        'EN_SERVICE'),
    ('DIS-GCE-01', 'Distributeur hall principal',   NULL,                                        'DISTRIBUTEUR',       'Gare Centrale',        'DEGRADE'),
    ('ECR-GCE-01', 'Écran des départs, hall',       NULL,                                        'ECRAN',              'Gare Centrale',        'EN_SERVICE'),
    ('ASC-PDA-01', 'Ascenseur accès PMR',           'Seul accès PMR de la station.',             'ASCENSEUR',          'Place des Arts',       'HORS_SERVICE'),
    ('POR-PDA-01', 'Portique entrée est',           NULL,                                        'PORTIQUE',           'Place des Arts',       'DEGRADE'),
    ('ECR-PDA-01', 'Écran quai direction Nord',     NULL,                                        'ECRAN',              'Place des Arts',       'EN_SERVICE'),
    ('ESC-UNI-01', 'Escalier mécanique montant',    NULL,                                        'ESCALIER_MECANIQUE', 'Université',           'HORS_SERVICE'),
    ('DIS-UNI-01', 'Distributeur sortie campus',    NULL,                                        'DISTRIBUTEUR',       'Université',           'DEGRADE'),
    ('ASC-HDV-01', 'Ascenseur parvis',              NULL,                                        'ASCENSEUR',          'Hôtel de Ville',       'EN_SERVICE'),
    ('ESC-HDV-01', 'Escalier mécanique descendant', NULL,                                        'ESCALIER_MECANIQUE', 'Hôtel de Ville',       'HORS_SERVICE'),
    ('POR-HDV-01', 'Portique sortie',               NULL,                                        'PORTIQUE',           'Hôtel de Ville',       'EN_SERVICE'),
    ('ECR-PEX-01', 'Écran information voyageurs',   'Affiche aussi les horaires des navettes.',  'ECRAN',              'Parc des Expositions', 'HORS_SERVICE'),
    ('DIS-PEX-01', 'Distributeur entrée salons',    NULL,                                        'DISTRIBUTEUR',       'Parc des Expositions', 'EN_SERVICE'),
    ('ASC-DOC-01', 'Ascenseur quai B',              NULL,                                        'ASCENSEUR',          'Les Docks',            'DEGRADE'),
    ('POR-DOC-01', 'Portique principal',            NULL,                                        'PORTIQUE',           'Les Docks',            'EN_SERVICE'),
    ('ESC-DOC-01', 'Escalier mécanique quai B',     'Mis en service récemment, aucun incident.', 'ESCALIER_MECANIQUE', 'Les Docks',            'EN_SERVICE')
) AS v (reference, libelle, description, type_code, station_nom, etat)
JOIN type_equipement t ON t.code = v.type_code
JOIN station         s ON s.nom  = v.station_nom;


-- -----------------------------------------------------------------------------
--  Prestataires
-- -----------------------------------------------------------------------------
INSERT INTO prestataire (nom, email, telephone) VALUES
    ('Ascenso Maintenance',   'contact@ascenso.example',          '01 23 45 67 01'),
    ('Élévation Pro',         'interventions@elevation-pro.example', '01 23 45 67 02'),
    ('TechnoRail Services',   'support@technorail.example',       NULL),
    ('Digital Affichage',     'sav@digital-affichage.example',    '01 23 45 67 04'),
    ('Multiservices Urbains', NULL,                               '01 23 45 67 05');


-- -----------------------------------------------------------------------------
--  Compétences des prestataires (piste 3)
-- -----------------------------------------------------------------------------
INSERT INTO prestataire_type_equipement (prestataire_id, type_equipement_id)
SELECT p.id, t.id
FROM (VALUES
    ('Ascenso Maintenance',   'ASCENSEUR'),
    ('Ascenso Maintenance',   'ESCALIER_MECANIQUE'),
    ('Élévation Pro',         'ASCENSEUR'),
    ('Élévation Pro',         'ESCALIER_MECANIQUE'),
    ('TechnoRail Services',   'PORTIQUE'),
    ('TechnoRail Services',   'DISTRIBUTEUR'),
    ('Digital Affichage',     'ECRAN'),
    ('Digital Affichage',     'DISTRIBUTEUR'),
    ('Multiservices Urbains', 'ASCENSEUR'),
    ('Multiservices Urbains', 'ESCALIER_MECANIQUE'),
    ('Multiservices Urbains', 'PORTIQUE'),
    ('Multiservices Urbains', 'DISTRIBUTEUR'),
    ('Multiservices Urbains', 'ECRAN')
) AS v (prestataire_nom, type_code)
JOIN prestataire     p ON p.nom  = v.prestataire_nom
JOIN type_equipement t ON t.code = v.type_code;


-- -----------------------------------------------------------------------------
--  Incidents
--  Chaque ligne indique « il y a combien de temps » l'incident a été ouvert,
--  affecté et résolu. Le statut en découle, ce qui garantit sa cohérence.
-- -----------------------------------------------------------------------------
INSERT INTO incident (equipement_id, description, date_ouverture, statut,
                      prestataire_id, date_affectation, date_resolution, declarant_id)
SELECT e.id,
       v.description,
       LOCALTIMESTAMP(0) - v.ouvert_il_y_a,
       CASE
           WHEN v.resolu_il_y_a   IS NOT NULL THEN 'RESOLU'
           WHEN v.prestataire_nom IS NOT NULL THEN 'AFFECTE'
           ELSE 'NON_AFFECTE'
       END,
       p.id,
       LOCALTIMESTAMP(0) - v.affecte_il_y_a,
       LOCALTIMESTAMP(0) - v.resolu_il_y_a,
       u.id
FROM (VALUES
    -- Non affectés : cas limites de RG3
    ('ECR-PEX-01', 'Écran éteint, aucun affichage des prochains passages.',     INTERVAL '2 hours',  NULL::VARCHAR,           NULL::INTERVAL,              NULL::INTERVAL,                'agent'),   -- 2 h   : VERT
    ('DIS-UNI-01', 'Le distributeur refuse les paiements par carte bancaire.',  INTERVAL '47 hours', NULL,                    NULL,                        NULL,                          'agent2'),  -- 47 h  : VERT (limite)
    ('ASC-PDA-01', 'Ascenseur bloqué au niveau quai, portes fermées.',          INTERVAL '48 hours', NULL,                    NULL,                        NULL,                          'agent'),   -- 48 h  : ORANGE (limite)
    ('POR-PDA-01', 'Le portique ne lit plus les passes sans contact.',          INTERVAL '60 hours', NULL,                    NULL,                        NULL,                          'agent2'),  -- 60 h  : ORANGE
    ('ESC-UNI-01', 'Escalier mécanique arrêté après un bruit métallique.',      INTERVAL '71 hours', NULL,                    NULL,                        NULL,                          'agent'),   -- 71 h  : ORANGE (juste avant 72 h)
    ('ASC-GCE-01', 'Bouton d''appel du niveau rue inopérant.',                  INTERVAL '72 hours', NULL,                    NULL,                        NULL,                          'agent2'),  -- 72 h  : ROUGE (limite)
    ('ESC-HDV-01', 'Marches désalignées, escalier mis à l''arrêt par sécurité.', INTERVAL '5 days',  NULL,                    NULL,                        NULL,                          'agent'),   -- 120 h : ROUGE

    -- Affectés : RG4, jamais d'indicateur vert / orange / rouge
    ('ESC-GCE-01', 'Main courante déchirée sur toute la longueur.',             INTERVAL '96 hours', 'Ascenso Maintenance',   INTERVAL '90 hours',         NULL,                          'agent'),   -- serait ROUGE s'il n'était pas affecté
    ('DIS-GCE-01', 'Écran tactile du distributeur ne répond plus.',             INTERVAL '30 hours', 'TechnoRail Services',   INTERVAL '26 hours',         NULL,                          'agent2'),
    ('ASC-DOC-01', 'Ascenseur qui s''arrête entre deux niveaux par intermittence.', INTERVAL '50 hours', 'Élévation Pro',     INTERVAL '1 hour',           NULL,                          'agent'),   -- affecté il y a 1 h

    -- Résolus : historique (F9), notes (F10), plusieurs incidents par équipement (F3)
    ('ECR-PDA-01', 'Affichage figé sur l''horaire de la veille.',               INTERVAL '30 hours', 'Digital Affichage',     INTERVAL '28 hours',         INTERVAL '3 hours',            'agent2'),  -- le plus récent de l'historique
    ('POR-DOC-01', 'Portique bloqué en position ouverte.',                      INTERVAL '4 days',   'TechnoRail Services',   INTERVAL '94 hours',         INTERVAL '2 days',             'agent'),
    ('ASC-HDV-01', 'Voyant de surcharge allumé en permanence.',                 INTERVAL '6 days',   'Ascenso Maintenance',   INTERVAL '5 days',           INTERVAL '3 days',             'agent2'),  -- pas encore évalué
    ('ECR-GCE-01', 'Écran clignotant, texte illisible.',                        INTERVAL '8 days',   'Digital Affichage',     INTERVAL '7 days 23 hours',  INTERVAL '7 days',             'agent'),   -- pas encore évalué
    ('ASC-GCE-01', 'Porte de cabine qui se rouvre sans cesse.',                 INTERVAL '12 days',  'Élévation Pro',         INTERVAL '11 days 20 hours', INTERVAL '10 days',            'agent'),
    ('POR-HDV-01', 'Lecteur de tickets magnétiques hors service.',              INTERVAL '15 days',  'Multiservices Urbains', INTERVAL '14 days',          INTERVAL '13 days',            'agent2'),
    ('DIS-UNI-01', 'Monnayeur bloqué, pièces non rendues.',                     INTERVAL '18 days',  'Multiservices Urbains', INTERVAL '17 days',          INTERVAL '16 days',            'agent'),
    ('POR-GCE-01', 'Vantaux du portique qui se referment trop vite.',           INTERVAL '20 days',  'TechnoRail Services',   INTERVAL '19 days',          INTERVAL '18 days',            'agent2'),
    ('ESC-HDV-01', 'Escalier mécanique tournant dans le sens inverse de celui affiché.', INTERVAL '25 days', 'Élévation Pro', INTERVAL '24 days',          INTERVAL '21 days',            'agent'),
    ('DIS-PEX-01', 'Distributeur figé, redémarré sur place par l''agent.',      INTERVAL '30 days',  NULL,                    NULL,                        INTERVAL '29 days 22 hours',   'agent2'),  -- résolu sans prestataire : non évaluable (RG8)
    ('ESC-GCE-01', 'Arrêt d''urgence déclenché sans raison apparente.',         INTERVAL '35 days',  'Ascenso Maintenance',   INTERVAL '34 days',          INTERVAL '32 days',            'agent'),
    ('ASC-GCE-01', 'Cabine immobilisée au niveau rue.',                         INTERVAL '40 days',  'Ascenso Maintenance',   INTERVAL '39 days 22 hours', INTERVAL '38 days',            'agent2'),
    ('ECR-PDA-01', 'Écran fissuré suite à un choc.',                            INTERVAL '50 days',  'Digital Affichage',     INTERVAL '49 days',          INTERVAL '45 days',            'agent')
) AS v (reference, description, ouvert_il_y_a, prestataire_nom, affecte_il_y_a, resolu_il_y_a, declarant_login)
JOIN      equipement  e ON e.reference = v.reference
LEFT JOIN prestataire p ON p.nom       = v.prestataire_nom
LEFT JOIN utilisateur u ON u.login     = v.declarant_login;


-- -----------------------------------------------------------------------------
--  Évaluations (RG8 : uniquement des incidents résolus ET affectés)
--  Quatre incidents résolus restent sans évaluation pour pouvoir démontrer F10.
-- -----------------------------------------------------------------------------
INSERT INTO evaluation (incident_id, note, commentaire, date_evaluation)
SELECT i.id, v.note, v.commentaire, i.date_resolution + v.delai
FROM (VALUES
    ('Cabine immobilisée au niveau rue.',                         5, 'Intervention rapide, cabine remise en service dans la journée.', INTERVAL '3 hours'),
    ('Porte de cabine qui se rouvre sans cesse.',                 4, 'Réparation efficace, léger retard à l''arrivée.',               INTERVAL '1 day'),
    ('Vantaux du portique qui se referment trop vite.',           2, 'Deux passages nécessaires, réglage approximatif.',              INTERVAL '2 days'),
    ('Escalier mécanique tournant dans le sens inverse de celui affiché.', 3, NULL,                                                   INTERVAL '1 day'),
    ('Lecteur de tickets magnétiques hors service.',              5, 'Pièce remplacée immédiatement.',                                INTERVAL '4 hours'),
    ('Portique bloqué en position ouverte.',                      4, 'Bon travail.',                                                  INTERVAL '6 hours'),
    ('Écran fissuré suite à un choc.',                            1, 'Délai de remplacement beaucoup trop long.',                     INTERVAL '1 day'),
    ('Monnayeur bloqué, pièces non rendues.',                     3, NULL,                                                            INTERVAL '2 days'),
    ('Arrêt d''urgence déclenché sans raison apparente.',         4, 'Cause identifiée et expliquée clairement.',                     INTERVAL '12 hours')
) AS v (description, note, commentaire, delai)
JOIN incident i ON i.description = v.description
WHERE i.statut = 'RESOLU'
  AND i.prestataire_id IS NOT NULL;


-- -----------------------------------------------------------------------------
--  Journal métier (piste 5), reconstitué à partir des données ci-dessus
-- -----------------------------------------------------------------------------
INSERT INTO evenement_incident (incident_id, auteur_id, date_evenement, type_evenement, detail)
SELECT i.id, i.declarant_id, i.date_ouverture, 'DECLARATION', 'Incident déclaré'
FROM incident i;

-- Exemple de réaffectation : un premier prestataire a été remplacé quatre heures plus tard
INSERT INTO evenement_incident (incident_id, auteur_id, date_evenement, type_evenement, detail)
SELECT i.id, u.id, i.date_affectation - INTERVAL '4 hours', 'AFFECTATION', 'Affecté à Élévation Pro'
FROM incident i
JOIN utilisateur u ON u.login = 'responsable'
WHERE i.description = 'Main courante déchirée sur toute la longueur.';

INSERT INTO evenement_incident (incident_id, auteur_id, date_evenement, type_evenement, detail)
SELECT i.id,
       u.id,
       i.date_affectation,
       CASE WHEN i.description = 'Main courante déchirée sur toute la longueur.' THEN 'REAFFECTATION' ELSE 'AFFECTATION' END,
       CASE WHEN i.description = 'Main courante déchirée sur toute la longueur.' THEN 'Réaffecté à ' ELSE 'Affecté à ' END || p.nom
FROM incident i
JOIN prestataire p ON p.id    = i.prestataire_id
JOIN utilisateur u ON u.login = 'responsable';

INSERT INTO evenement_incident (incident_id, auteur_id, date_evenement, type_evenement, detail)
SELECT i.id, u.id, i.date_resolution, 'RESOLUTION', 'Incident résolu'
FROM incident i
JOIN utilisateur u ON u.login = 'responsable'
WHERE i.statut = 'RESOLU';

INSERT INTO evenement_incident (incident_id, auteur_id, date_evenement, type_evenement, detail)
SELECT ev.incident_id, u.id, ev.date_evaluation, 'EVALUATION', 'Note attribuée : ' || ev.note || '/5'
FROM evaluation ev
JOIN utilisateur u ON u.login = 'responsable';
