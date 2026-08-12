package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.model.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controleur de l'ecran de gestion des etudiants : liste, recherche, CRUD complet.
 */
public class EtudiantsController {

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
    private TextField champRecherche;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();

    @FXML
    public void initialize() {
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colClasse.setCellValueFactory(new PropertyValueFactory<>("classe"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        rafraichir();
    }

    @FXML
    public void rechercher() {
        String motCle = champRecherche.getText();
        if (motCle == null || motCle.isBlank()) {
            chargerListe(etudiantDAO.listerTous());
        } else {
            chargerListe(etudiantDAO.rechercher(motCle.trim()));
        }
    }

    @FXML
    public void ouvrirFormulaireAjout() {
        ouvrirFormulaire(null);
    }

    @FXML
    public void ouvrirFormulaireModification() {
        Etudiant selection = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord un etudiant dans la liste.");
            return;
        }
        ouvrirFormulaire(selection);
    }

    @FXML
    public void supprimerSelection() {
        Etudiant selection = tableEtudiants.getSelectionModel().getSelectedItem();
        if (selection == null) {
            afficherInfo("Selectionnez d'abord un etudiant dans la liste.");
            return;
        }

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Supprimer definitivement " + selection.getNomComplet() + " ?");

        Optional<ButtonType> reponse = confirmation.showAndWait();
        if (reponse.isPresent() && reponse.get() == ButtonType.OK) {
            etudiantDAO.supprimer(selection.getId());
            rafraichir();
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
        ObservableList<Etudiant> data = FXCollections.observableArrayList(etudiants);
        tableEtudiants.setItems(data);
    }

    private void afficherInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}