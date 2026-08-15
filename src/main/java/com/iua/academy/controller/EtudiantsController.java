package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.dao.ParametreDAO;
import com.iua.academy.dao.ParametreDaoSqlite;
import com.iua.academy.model.Etudiant;
import com.iua.academy.model.NoteDetaillee;
import com.iua.academy.util.BulletinPdfGenerator;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controleur de l'ecran de gestion des etudiants : liste, recherche, filtre par classe, tri,
 * CRUD complet, moyenne ponderee, export bulletin PDF, actions desactivees sans selection.
 */
public class EtudiantsController {

    private static final String TRI_NOM_ASC = "Nom (A-Z)";
    private static final String TRI_NOM_DESC = "Nom (Z-A)";
    private static final String TRI_MOYENNE_ASC = "Moyenne (croissante)";
    private static final String TRI_MOYENNE_DESC = "Moyenne (decroissante)";

    @FXML
    private TableView<Etudiant> tableEtudiants;
    @FXML
    private TableColumn<Etudiant, String> colMatricule;
    @FXML
    private TableColumn<Etudiant, String> colNom;
    @FXML
    private TableColumn<Etudiant, String> colPrenom;
    @FXML
    private TableColumn<Etudiant, String> colClasse;
    @FXML
    private TableColumn<Etudiant, String> colEmail;
    @FXML
    private TableColumn<Etudiant, Double> colMoyenne;
    @FXML
    private TextField champRecherche;
    @FXML
    private ComboBox<String> comboFiltreClasse;
    @FXML
    private ComboBox<String> comboTri;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnBulletin;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Label lblIndicationSelection;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final NoteDAO noteDAO = new NoteDaoSqlite();
    private final ParametreDAO parametreDAO = new ParametreDaoSqlite();
    private final BulletinPdfGenerator bulletinPdfGenerator = new BulletinPdfGenerator();

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMoyenne.setCellValueFactory(new PropertyValueFactory<>("moyenne"));
        colMoyenne.setCellFactory(colonne -> creerCelluleMoyenne());

        tableEtudiants.setPlaceholder(construireEtatVide());

        // Actions desactivees proprement tant qu'aucune ligne n'est selectionnee
        var selectionVide = Bindings.isNull(tableEtudiants.getSelectionModel().selectedItemProperty());
        btnModifier.disableProperty().bind(selectionVide);
        btnBulletin.disableProperty().bind(selectionVide);
        btnSupprimer.disableProperty().bind(selectionVide);
        lblIndicationSelection.visibleProperty().bind(selectionVide);
        lblIndicationSelection.managedProperty().bind(selectionVide);

        comboTri.setItems(FXCollections.observableArrayList(TRI_NOM_ASC, TRI_NOM_DESC, TRI_MOYENNE_ASC, TRI_MOYENNE_DESC));
        comboTri.setValue(TRI_NOM_ASC);
        comboTri.valueProperty().addListener((obs, ancien, nouveau) -> rechercher());

        chargerClassesDisponibles();
        rafraichir();
    }

    private VBox construireEtatVide() {
        Label titre = new Label("Aucun etudiant");
        titre.getStyleClass().add("empty-state-title");

        Label texte = new Label("Aucun etudiant n'est actuellement enregistre.");
        texte.getStyleClass().add("empty-state-text");

        Button bouton = new Button("+ Ajouter un etudiant");
        bouton.getStyleClass().add("btn-action-create");
        bouton.setOnAction(e -> ouvrirFormulaireAjout());

        VBox conteneur = new VBox(10, titre, texte, bouton);
        conteneur.setAlignment(javafx.geometry.Pos.CENTER);
        conteneur.setPadding(new javafx.geometry.Insets(40));
        return conteneur;
    }

    private void chargerClassesDisponibles() {
        List<String> classes = etudiantDAO.listerClassesDistinctes();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add(null);
        items.addAll(classes);
        comboFiltreClasse.setItems(items);
    }

    @FXML
    public void rechercher() {
        String motCle = champRecherche.getText();
        String classeChoisie = comboFiltreClasse.getValue();

        List<Etudiant> resultats;
        if (motCle == null || motCle.isBlank()) {
            resultats = etudiantDAO.listerTous();
        } else {
            resultats = etudiantDAO.rechercher(motCle.trim());
        }

        if (classeChoisie != null && !classeChoisie.isBlank()) {
            resultats = resultats.stream()
                .filter(e -> classeChoisie.equals(e.getClasse()))
                .collect(Collectors.toList());
        }

        chargerListe(resultats);
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void ouvrirFormulaireModification() {
        Etudiant selection = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    public void supprimerSelection() {
        Etudiant selection = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer l'etudiant ?");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Cette action supprimera definitivement la fiche de "
            + selection.getNomComplet() + " ainsi que ses notes associees.");

        Optional<ButtonType> reponse = confirmation.showAndWait();
        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            etudiantDAO.supprimer(selection.getId());
            chargerClassesDisponibles();
            rafraichir();
        }
    }

    @FXML
    public void exporterBulletin() {
        Etudiant selection = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le bulletin");
        fileChooser.setInitialFileName("bulletin_" + selection.getMatricule() + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));

        File destination = fileChooser.showSaveDialog(tableEtudiants.getScene().getWindow());
        if (destination == null) {
            return;
        }

        try {
            List<NoteDetaillee> notes = noteDAO.listerParEtudiant(selection.getId());
            Double moyenne = noteDAO.calculerMoyenneEtudiant(selection.getId());
            String nomEtablissement = parametreDAO.obtenir("nom_etablissement", "Etablissement");
            String anneeScolaire = parametreDAO.obtenir("annee_scolaire", "-");
            double seuilValidation = Double.parseDouble(parametreDAO.obtenir("seuil_validation", "10"));

            bulletinPdfGenerator.genererBulletin(destination, selection, notes, moyenne, nomEtablissement, anneeScolaire, seuilValidation);

            afficherInfo("Bulletin genere : " + destination.getAbsolutePath());
        } catch (IOException | NumberFormatException e) {
            afficherErreur("Impossible de generer le bulletin PDF.");
        }
    }

    private void ouvrirFormulaire(Etudiant etudiantAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iua/academy/fxml/etudiant-form.fxml"));
            Parent racine = loader.load();

            EtudiantFormController controller = loader.getController();
            if (etudiantAModifier != null) {
                controller.initPourModification(etudiantAModifier);
            }

            Stage fenetre = new Stage();
            fenetre.setTitle(etudiantAModifier == null ? "Nouvel etudiant" : "Modifier l'etudiant");
            fenetre.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/iua/academy/css/theme.css").toExternalForm());
            fenetre.setScene(scene);
            fenetre.showAndWait();

            if (controller.isSauvegarde()) {
                chargerClassesDisponibles();
                rafraichir();
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le formulaire etudiant", e);
        }
    }

    private void rafraichir() {
        chargerListe(etudiantDAO.listerTous());
    }

    private void chargerListe(List<Etudiant> etudiants) {
        for (Etudiant e : etudiants) {
            e.setMoyenne(noteDAO.calculerMoyenneEtudiant(e.getId()));
        }

        Comparator<Etudiant> comparateur = construireComparateur();
        List<Etudiant> trie = etudiants.stream().sorted(comparateur).collect(Collectors.toList());

        ObservableList<Etudiant> data = FXCollections.observableArrayList(trie);
        tableEtudiants.setItems(data);
    }

    private Comparator<Etudiant> construireComparateur() {
        String critere = comboTri.getValue();
        if (TRI_NOM_DESC.equals(critere)) {
            return Comparator.comparing(Etudiant::getNom, String.CASE_INSENSITIVE_ORDER).reversed();
        } else if (TRI_MOYENNE_ASC.equals(critere)) {
            return Comparator.comparing((Etudiant e) -> e.getMoyenne() == null ? -1.0 : e.getMoyenne());
        } else if (TRI_MOYENNE_DESC.equals(critere)) {
            return Comparator.comparing((Etudiant e) -> e.getMoyenne() == null ? -1.0 : e.getMoyenne()).reversed();
        }
        return Comparator.comparing(Etudiant::getNom, String.CASE_INSENSITIVE_ORDER);
    }

    private TableCell<Etudiant, Double> creerCelluleMoyenne() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double moyenne, boolean vide) {
                super.updateItem(moyenne, vide);
                if (vide || moyenne == null) {
                    setText("-");
                    setStyle("");
                } else {
                    setText(String.format("%.2f", moyenne));
                    if (moyenne >= 10) {
                        setStyle("-fx-text-fill: #1E8449; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #D64545; -fx-font-weight: bold;");
                    }
                }
            }
        };
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}