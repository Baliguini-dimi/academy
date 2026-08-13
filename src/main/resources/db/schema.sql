-- Schema de la base Academy
-- Institut Universitaire d'Abidjan - Gestion des etudiants et des notes

CREATE TABLE IF NOT EXISTS etudiant (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    matricule      TEXT NOT NULL UNIQUE,
    nom            TEXT NOT NULL,
    prenom         TEXT NOT NULL,
    date_naissance TEXT,
    classe         TEXT NOT NULL,
    email          TEXT,
    date_creation  TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS matiere (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    nom          TEXT NOT NULL UNIQUE,
    coefficient  REAL NOT NULL DEFAULT 1,
    enseignant   TEXT
);

CREATE TABLE IF NOT EXISTS note (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    etudiant_id       INTEGER NOT NULL,
    matiere_id        INTEGER NOT NULL,
    valeur            REAL NOT NULL CHECK (valeur >= 0 AND valeur <= 20),
    type_evaluation   TEXT NOT NULL DEFAULT 'Devoir',
    date_evaluation   TEXT NOT NULL DEFAULT (date('now')),
    FOREIGN KEY (etudiant_id) REFERENCES etudiant(id) ON DELETE CASCADE,
    FOREIGN KEY (matiere_id)  REFERENCES matiere(id)  ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_note_etudiant ON note(etudiant_id);
CREATE INDEX IF NOT EXISTS idx_note_matiere  ON note(matiere_id);
CREATE TABLE IF NOT EXISTS parametre (
    cle    TEXT PRIMARY KEY,
    valeur TEXT
);

INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('nom_etablissement', 'Mon Etablissement');
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('annee_scolaire', '2025-2026');
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('seuil_validation', '10');
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('adresse_etablissement', '');
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('telephone_etablissement', '');
INSERT OR IGNORE INTO parametre (cle, valeur) VALUES ('email_etablissement', '');
