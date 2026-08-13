-- Donnees de demonstration Academy
-- Promo Master 1 IUA (Cyber Securite / MIAGE / Genie Informatique)
-- A executer une seule fois, manuellement, via DB Browser for SQLite

-- Etudiants
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-001', 'ABDALLAH', 'Nadhufane', 'Cyber Sécurité', 'nadhufane.abdallah@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-001', 'ANATO', 'Elise Ursula', 'Génie Informatique', 'elise.anato@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-002', 'ASSARE', 'Marc Wilfried', 'Cyber Sécurité', 'marc.assare@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-MIAGE-001', 'BAH', 'Abdoulaye', 'MIAGE', 'abdoulaye.bah@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-002', 'BAKAYOKO', 'Zakarya Ahmed Ilias', 'Génie Informatique', 'zakarya.bakayoko@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-003', 'BALIGUINI DEMBA', 'Dimitri Nelson', 'Génie Informatique', 'dimitri.baliguini@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-MIAGE-002', 'BIKPO', 'Boya Archange David Yoan N''Guessan', 'MIAGE', 'boya.bikpo@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-004', 'DIARRASSOUBA', 'Nabi Madawa', 'Génie Informatique', 'nabi.diarrassouba@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-003', 'DJAO', 'Giovanni Xavier', 'Cyber Sécurité', 'giovanni.djao@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-005', 'DOUO', 'Boualy Theophile', 'Génie Informatique', 'boualy.douo@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-006', 'ETTIEN', 'Celina Fabion Anoh', 'Génie Informatique', 'celina.ettien@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-004', 'KAMAGATÉ', 'Falikou', 'Cyber Sécurité', 'falikou.kamagate@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-007', 'KAMENAN', 'Adjé Christian Dorgeles', 'Génie Informatique', 'adje.kamenan@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-008', 'KOUASSI', 'Henock', 'Génie Informatique', 'henock.kouassi@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-009', 'KOUYATE', 'Djelika', 'Génie Informatique', 'djelika.kouyate@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-005', 'KPINDI', 'Akissy Jacques Emmanuel', 'Cyber Sécurité', 'akissy.kpindi@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-010', 'OKOU', 'Paul David Sabin', 'Génie Informatique', 'paul.okou@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-CYB-006', 'OUATTARA', 'Yakirafolo Pavel', 'Cyber Sécurité', 'yakirafolo.ouattara@iua-demo.ci');
INSERT OR IGNORE INTO etudiant (matricule, nom, prenom, classe, email) VALUES ('IUA-GI-011', 'YAO', 'Yao Abraham', 'Génie Informatique', 'yao.yao@iua-demo.ci');

-- Matieres (dedupliquees entre filieres, coefficient par defaut a ajuster)
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Programmation systemes et reseaux sous Linux', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Securite des systemes d''information', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Technologies IP avancees', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('PHP avance', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Securite des reseaux', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Programmation systemes et reseaux sous .NET', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Methodes Agiles', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Droit et contractualisation dans l''informatique', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Developpement mobiles', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('PHP et Framework associes', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Architectures logicielles et web services', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Base de donnees avancees', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Mathematiques du signal', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Administration Reseau', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Apprentissage automatique', 2, 'A definir');
INSERT OR IGNORE INTO matiere (nom, coefficient, enseignant) VALUES ('Administration systeme', 2, 'A definir');