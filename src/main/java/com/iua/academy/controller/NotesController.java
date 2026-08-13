package com.iua.academy.controller;

import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.model.NoteDetaillee;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class NotesController {

    @FXML
    private TableView<NoteDetaillee> tableNotes;
    @FXML
    private TableColumn<NoteDetaillee, String> colEtudiant;
    @FXML
    private TableColumn<NoteDetaillee, String> colMatiere;
    @FXML
    private TableColumn<NoteDetaillee, Double> colValeur;
    @FXML
    private TableColumn<NoteDetaillee, String> colType;
    @FXML
    private TableColumn<NoteDetaillee, String> colDate;
    @FXML
    private TextField champRecherche;

    private final NoteDAO noteDAO = new NoteDaoSqlite();

    @FXML
    public void initialize() {
        colEtudiant.setCellValueFactory(new PropertyValueFactory<>("etudiantNomComplet"));
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("matiereNom"));
        colValeur.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeEvaluation"));
        colDate.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateEvaluation().toString())
        );

        tableNotes.setPlaceholder(construireEtatVide());

        rafraichir();
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
        if (motCle == null || motCle.isBlank()) {
            chargerListe(noteDAO.listerToutesDetaillees());
        } else {
            chargerListe(noteDAO.rechercherDetaillees(motCle.trim()));
        }
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void ouvrirFormulaireModification() {
        NoteDetaillee selection = tableNotes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord une note dans la liste.");
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    public void supprimerSelection() {
        NoteDetaillee selection = tableNotes.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord une note dans la liste.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer cette note ?");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Cette action est definitive. La note de "
            + selection.getEtudiantNomComplet() + " en " + selection.getMatiereNom() + " sera supprimee.");

        Optional<ButtonType> reponse = confirmation.showAndWait();
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

    private void afficherInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}