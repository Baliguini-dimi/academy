package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.model.Etudiant;
import com.iua.academy.model.Matiere;
import com.iua.academy.model.NoteDetaillee;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class NotesController {

    private static final String PERIODE_7J = "7 derniers jours";
    private static final String PERIODE_30J = "30 derniers jours";
    private static final String PERIODE_3M = "3 derniers mois";

    @FXML
    private TableView<NoteDetaillee> tableNotes;
    @FXML
    private TableColumn<NoteDetaillee, String> colEtudiant;
    @FXML
    private TableColumn<NoteDetaillee, String> colMatiere;
    @FXML
    private TableColumn<NoteDetaillee, Double> colValeur;
    @FXML
    private TableColumn<NoteDetaillee, Double> colNiveau;
    @FXML
    private TableColumn<NoteDetaillee, String> colType;
    @FXML
    private TableColumn<NoteDetaillee, String> colDate;
    @FXML
    private TextField champRecherche;
    @FXML
    private ComboBox<String> comboFiltreEtudiant;
    @FXML
    private ComboBox<String> comboFiltreMatiere;
    @FXML
    private ComboBox<String> comboFiltreType;
    @FXML
    private ComboBox<String> comboFiltrePeriode;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Label lblIndicationSelection;

    private final NoteDAO noteDAO = new NoteDaoSqlite();
    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();

    @FXML
    public void initialize() {
        colEtudiant.setCellValueFactory(new PropertyValueFactory<>("etudiantNomComplet"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiereNom"));
        colValeur.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        colNiveau.setCellFactory(colonne -> creerCelluleNiveau());
        colType.setCellValueFactory(new PropertyValueFactory<>("typeEvaluation"));
        colDate.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateEvaluation().toString())
        );

        tableNotes.setPlaceholder(construireEtatVide());

        var selectionVide = Bindings.isNull(tableNotes.getSelectionModel().selectedItemProperty());
        btnModifier.disableProperty().bind(selectionVide);
        btnSupprimer.disableProperty().bind(selectionVide);
        lblIndicationSelection.visibleProperty().bind(selectionVide);
        lblIndicationSelection.managedProperty().bind(selectionVide);

        chargerFiltres();
        rafraichir();
    }

    private void chargerFiltres() {
        ObservableList<String> etudiants = FXCollections.observableArrayList();
        etudiants.add(null);
        etudiants.addAll(etudiantDAO.listerTous().stream().map(Etudiant::getNomComplet).collect(Collectors.toList()));
        comboFiltreEtudiant.setItems(etudiants);

        ObservableList<String> matieres = FXCollections.observableArrayList();
        matieres.add(null);
        matieres.addAll(matiereDAO.listerTous().stream().map(Matiere::getNom).collect(Collectors.toList()));
        comboFiltreMatiere.setItems(matieres);

        comboFiltreType.setItems(FXCollections.observableArrayList(null, "Devoir", "Interrogation", "Examen", "Projet"));
        comboFiltrePeriode.setItems(FXCollections.observableArrayList(null, PERIODE_7J, PERIODE_30J, PERIODE_3M));
    }

    private VBox construireEtatVide() {
        Label titre = new Label("Aucune note");
        titre.getStyleClass().add("empty-state-title");

        Label texte = new Label("Aucune note n'est actuellement enregistree.");
        texte.getStyleClass().add("empty-state-text");

        Button bouton = new Button("+ Ajouter une note");
        bouton.getStyleClass().add("btn-action-create");
        bouton.setOnAction(e -> ouvrirFormulaireAjout());

        VBox conteneur = new VBox(10, titre, texte, bouton);
        conteneur.setAlignment(Pos.CENTER);
        conteneur.setPadding(new Insets(40));
        return conteneur;
    }

    @FXML
    public void rechercher() {
        String motCle = champRecherche.getText();
        List<NoteDetaillee> resultats = (motCle == null || motCle.isBlank())
            ? noteDAO.listerToutesDetaillees()
            : noteDAO.rechercherDetaillees(motCle.trim());

        String etudiantChoisi = comboFiltreEtudiant.getValue();
        if (etudiantChoisi != null) {
            resultats = resultats.stream().filter(n -> etudiantChoisi.equals(n.getEtudiantNomComplet())).collect(Collectors.toList());
        }

        String matiereChoisie = comboFiltreMatiere.getValue();
        if (matiereChoisie != null) {
            resultats = resultats.stream().filter(n -> matiereChoisie.equals(n.getMatiereNom())).collect(Collectors.toList());
        }

        String typeChoisi = comboFiltreType.getValue();
        if (typeChoisi != null) {
            resultats = resultats.stream().filter(n -> typeChoisi.equals(n.getTypeEvaluation())).collect(Collectors.toList());
        }

        String periodeChoisie = comboFiltrePeriode.getValue();
        if (periodeChoisie != null) {
            LocalDate limite = switch (periodeChoisie) {
                case PERIODE_7J -> LocalDate.now().minusDays(7);
                case PERIODE_30J -> LocalDate.now().minusDays(30);
                case PERIODE_3M -> LocalDate.now().minusMonths(3);
                default -> LocalDate.MIN;
            };
            resultats = resultats.stream().filter(n -> !n.getDateEvaluation().isBefore(limite)).collect(Collectors.toList());
        }

        chargerListe(resultats);
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void ouvrirFormulaireModification() {
        NoteDetaillee selection = tableNotes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    public void supprimerSelection() {
        NoteDetaillee selection = tableNotes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer cette note ?");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Cette action supprimera definitivement la note de "
            + selection.getEtudiantNomComplet() + " en " + selection.getMatiereNom() + ".");

        var reponse = confirmation.showAndWait();
        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            noteDAO.supprimer(selection.getId());
            rafraichir();
        }
    }

    private void ouvrirFormulaire(NoteDetaillee noteAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iua/academy/fxml/note-form.fxml"));
            Parent racine = loader.load();

            NoteFormController controller = loader.getController();
            if (noteAModifier != null) {
                controller.initPourModification(noteAModifier);
            }

            Stage fenetre = new Stage();
            fenetre.setTitle(noteAModifier == null ? "Nouvelle note" : "Modifier la note");
            fenetre.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/iua/academy/css/theme.css").toExternalForm());
            fenetre.setScene(scene);
            fenetre.showAndWait();

            if (controller.isSauvegarde()) {
                chargerFiltres();
                rafraichir();
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le formulaire note", e);
        }
    }

    private void rafraichir() {
        chargerListe(noteDAO.listerToutesDetaillees());
    }

    private void chargerListe(List<NoteDetaillee> notes) {
        ObservableList<NoteDetaillee> data = FXCollections.observableArrayList(notes);
        tableNotes.setItems(data);
    }

    /** Colonne Niveau : texte + couleur, pour ne pas reposer uniquement sur la couleur. */
    private TableCell<NoteDetaillee, Double> creerCelluleNiveau() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double valeur, boolean vide) {
                super.updateItem(valeur, vide);
                if (vide || valeur == null) {
                    setText("");
                    setStyle("");
                    return;
                }
                String niveau;
                String couleur;
                if (valeur < 10) {
                    niveau = "Insuffisant";
                    couleur = "#D64545";
                } else if (valeur < 12) {
                    niveau = "Passable";
                    couleur = "#B8860B";
                } else if (valeur < 14) {
                    niveau = "Assez bien";
                    couleur = "#2E9FE0";
                } else if (valeur < 16) {
                    niveau = "Bien";
                    couleur = "#0F2A4D";
                } else {
                    niveau = "Excellent";
                    couleur = "#1E8449";
                }
                setText(niveau);
                setStyle("-fx-text-fill: " + couleur + "; -fx-font-weight: bold; -fx-font-size: 12px;");
            }
        };
    }
}