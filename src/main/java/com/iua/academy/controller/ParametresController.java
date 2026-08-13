package com.iua.academy.controller;

import com.iua.academy.dao.ParametreDAO;
import com.iua.academy.dao.ParametreDaoSqlite;
import com.iua.academy.util.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ParametresController {

    @FXML
    private TextField champNomEtablissement;
    @FXML
    private TextField champAnneeScolaire;
    @FXML
    private TextField champAdresse;
    @FXML
    private TextField champTelephone;
    @FXML
    private TextField champEmail;
    @FXML
    private TextField champSeuilValidation;
    @FXML
    private Label lblConfirmation;

    private final ParametreDAO parametreDAO = new ParametreDaoSqlite();

    @FXML
    public void initialize() {
        champNomEtablissement.setText(parametreDAO.obtenir("nom_etablissement", ""));
        champAnneeScolaire.setText(parametreDAO.obtenir("annee_scolaire", ""));
        champAdresse.setText(parametreDAO.obtenir("adresse_etablissement", ""));
        champTelephone.setText(parametreDAO.obtenir("telephone_etablissement", ""));
        champEmail.setText(parametreDAO.obtenir("email_etablissement", ""));
        champSeuilValidation.setText(parametreDAO.obtenir("seuil_validation", "10"));
    }

    @FXML
    public void enregistrer() {
        lblConfirmation.setText("");

        if (champNomEtablissement.getText().isBlank() || champAnneeScolaire.getText().isBlank()) {
            afficherErreur("Le nom de l'etablissement et l'annee scolaire sont obligatoires.");
            return;
        }
        try {
            Double.parseDouble(champSeuilValidation.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            afficherErreur("Le seuil de validation doit etre un nombre.");
            return;
        }

        parametreDAO.enregistrer("nom_etablissement", champNomEtablissement.getText().trim());
        parametreDAO.enregistrer("annee_scolaire", champAnneeScolaire.getText().trim());
        parametreDAO.enregistrer("adresse_etablissement", champAdresse.getText().trim());
        parametreDAO.enregistrer("telephone_etablissement", champTelephone.getText().trim());
        parametreDAO.enregistrer("email_etablissement", champEmail.getText().trim());
        parametreDAO.enregistrer("seuil_validation", champSeuilValidation.getText().trim());

        lblConfirmation.setText("Parametres enregistres.");
    }

    @FXML
    public void exporterSauvegarde() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter une sauvegarde");
        fileChooser.setInitialFileName("academy-sauvegarde.db");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Base de donnees SQLite", "*.db"));

        File destination = fileChooser.showSaveDialog(champNomEtablissement.getScene().getWindow());
        if (destination == null) {
            return;
        }

        try {
            Path source = DatabaseManager.getInstance().getDatabaseFilePath();
            Files.copy(source, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            afficherInfo("Sauvegarde exportee : " + destination.getAbsolutePath());
        } catch (IOException e) {
            afficherErreur("Impossible d'exporter la sauvegarde.");
        }
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
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}