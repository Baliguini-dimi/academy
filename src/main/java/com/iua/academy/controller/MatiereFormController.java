package com.iua.academy.controller;

import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.model.Matiere;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MatiereFormController {

    @FXML
    private TextField champNom;
    @FXML
    private TextField champCoefficient;
    @FXML
    private TextField champEnseignant;

    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private Matiere matiereEnCours;
    private boolean sauvegarde = false;

    public void initPourModification(Matiere matiere) {
        this.matiereEnCours = matiere;
        champNom.setText(matiere.getNom());
        champCoefficient.setText(String.valueOf(matiere.getCoefficient()));
        champEnseignant.setText(matiere.getEnseignant());
    }

    public boolean isSauvegarde() {
        return sauvegarde;
    }

    @FXML
    public void enregistrer() {
        if (!champsValides()) {
            return;
        }
        double coefficient;
        try {
            coefficient = Double.parseDouble(champCoefficient.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            afficherErreur("Le coefficient doit etre un nombre (ex : 1, 2, 1.5).");
            return;
        }

        try {
            if (matiereEnCours == null) {
                Matiere nouvelle = new Matiere(
                    champNom.getText().trim(),
                    coefficient,
                    champEnseignant.getText().trim()
                );
                matiereDAO.creer(nouvelle);
            } else {
                matiereEnCours.setNom(champNom.getText().trim());
                matiereEnCours.setCoefficient(coefficient);
                matiereEnCours.setEnseignant(champEnseignant.getText().trim());
                matiereDAO.mettreAJour(matiereEnCours);
            }
            sauvegarde = true;
            fermerFenetre();
        } catch (RuntimeException e) {
            afficherErreur("Impossible d'enregistrer la matiere.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private boolean champsValides() {
        boolean ok = notBlank(champNom.getText()) && notBlank(champCoefficient.getText());
        if (!ok) {
            afficherErreur("Nom et coefficient sont obligatoires.");
        }
        return ok;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champNom.getScene().getWindow();
        stage.close();
    }
}