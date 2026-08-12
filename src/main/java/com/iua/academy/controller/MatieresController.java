package com.iua.academy.controller;

import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.model.Matiere;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MatieresController {

    @FXML
    private TableView<Matiere> tableMatieres;
    @FXML
    private TableColumn<Matiere, String> colNom;
    @FXML
    private TableColumn<Matiere, Double> colCoefficient;
    @FXML
    private TableColumn<Matiere, String> colEnseignant;
    @FXML
    private TextField champRecherche;

    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCoefficient.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        colEnseignant.setCellValueFactory(new PropertyValueFactory<>("enseignant"));

        rafraichir();
    }

    @FXML
    public void rechercher() {
        String motCle = champRecherche.getText();
        if (motCle == null || motCle.isBlank()) {
            chargerListe(matiereDAO.listerTous());
        } else {
            chargerListe(matiereDAO.rechercher(motCle.trim()));
        }
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void ouvrirFormulaireModification() {
        Matiere selection = tableMatieres.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord une matiere dans la liste.");
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    public void supprimerSelection() {
        Matiere selection = tableMatieres.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord une matiere dans la liste.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer definitivement la matiere " + selection.getNom() + " ?");

        Optional<ButtonType> reponse = confirmation.showAndWait();
        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            matiereDAO.supprimer(selection.getId());
            rafraichir();
        }
    }

    private void ouvrirFormulaire(Matiere matiereAModifier) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iua/academy/fxml/matiere-form.fxml"));
            Parent racine = loader.load();

            MatiereFormController controller = loader.getController();
            if (matiereAModifier != null) {
                controller.initPourModification(matiereAModifier);
            }

            Stage fenetre = new Stage();
            fenetre.setTitle(matiereAModifier == null ? "Nouvelle matiere" : "Modifier la matiere");
            fenetre.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/iua/academy/css/theme.css").toExternalForm());
            fenetre.setScene(scene);
            fenetre.showAndWait();

            if (controller.isSauvegarde()) {
                rafraichir();
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le formulaire matiere", e);
        }
    }

    private void rafraichir() {
        chargerListe(matiereDAO.listerTous());
    }

    private void chargerListe(List<Matiere> matieres) {
        ObservableList<Matiere> data = FXCollections.observableArrayList(matieres);
        tableMatieres.setItems(data);
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}