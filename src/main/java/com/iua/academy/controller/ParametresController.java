package com.iua.academy.controller;

import com.iua.academy.dao.ParametreDAO;
import com.iua.academy.dao.ParametreDaoSqlite;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ParametresController {

    @FXML
    private TextField champNomEtablissement;
    @FXML
    private TextField champAnneeScolaire;
    @FXML
    private TextField champSeuilValidation;
    @FXML
    private Label lblConfirmation;

    private final ParametreDAO parametreDAO = new ParametreDaoSqlite();

    @FXML
    public void initialize() {
        champNomEtablissement.setText(parametreDAO.obtenir("nom_etablissement", ""));
        champAnneeScolaire.setText(parametreDAO.obtenir("annee_scolaire", ""));
        champSeuilValidation.setText(parametreDAO.obtenir("seuil_validation", "10"));
    }

    @FXML
    public void enregistrer() {
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
        parametreDAO.enregistrer("seuil_validation", champSeuilValidation.getText().trim());

        lblConfirmation.setText("Parametres enregistres.");
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}