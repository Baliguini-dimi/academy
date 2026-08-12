# Academy

Application desktop de gestion des etudiants, des matieres et des notes,
developpee dans le cadre du cours **Industrialisation du Developpement Logiciel**
a l'Institut Universitaire d'Abidjan (IUA).

## Objectif

Academy permet a un etablissement de gerer :
- les fiches etudiantes (matricule, identite, classe),
- les matieres enseignees et leurs coefficients,
- la saisie des notes et le suivi des moyennes.

L'identite visuelle de l'application s'inspire de la charte graphique de l'IUA
(bleu marine, bleu ciel, or) pour un rendu institutionnel et professionnel.

## Stack technique

| Composant | Choix |
|---|---|
| Langage | Java 17 |
| Interface | JavaFX 21 (FXML + CSS) |
| Base de donnees | SQLite (fichier local, `~/.academy/academy.db`) |
| Build | Maven |
| Architecture | MVC + DAO |

## Structure du projet

```
academy/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/iua/academy/
    │   ├── App.java              # Point d'entree JavaFX
    │   ├── model/                # Etudiant, Matiere, Note
    │   ├── dao/                  # Acces aux donnees (interfaces + implementations SQLite)
    │   ├── controller/           # Controleurs FXML
    │   └── util/                 # DatabaseManager, utilitaires
    └── resources/
        ├── com/iua/academy/
        │   ├── fxml/             # Ecrans (main-layout, dashboard, etudiants, matieres, notes)
        │   ├── css/              # theme.css (charte graphique IUA)
        │   └── images/           # Logo et assets
        └── db/
            └── schema.sql        # Script de creation des tables
```

## Lancer le projet

Prerequis : JDK 17+ et Maven.

```bash
mvn clean javafx:run
```

## Construire un jar executable

```bash
mvn clean package
java -jar target/academy-1.0.0.jar
```

## Etat d'avancement

- [x] Cadrage, charte graphique, architecture
- [x] Squelette Maven + JavaFX + SQLite
- [x] Schema de base de donnees (Etudiant, Matiere, Note)
- [x] Layout principal (navigation laterale) et tableau de bord
- [x] Ecran Etudiants : liste + recherche
- [ ] Ecran Etudiants : formulaire ajout/modification/suppression
- [ ] Ecran Matieres : CRUD complet
- [ ] Ecran Notes : saisie + calcul des moyennes
- [ ] Validation et gestion d'erreurs utilisateur
- [ ] Packaging final et documentation de presentation

## Auteur

Dimitri Nelson BALIGUINI — Master 1 Big Data et Intelligence Artificielle, IUA
