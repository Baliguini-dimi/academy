package com.iua.academy.controller;

import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.model.Matiere;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MatiereFormController {

    @FXML
    private TextField champNom;
    @FXML
    private Label lblErreurNom;
    @FXML
    private TextField champCoefficient;
    @FXML
    private Label lblErreurCoefficient;
    @FXML
    private TextField champEnseignant;

    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private Matiere matiereEnCours;
    private boolean sauvegarde = false;

    @FXML
    public void initialize() {
        champNom.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerNom();
        });
        champCoefficient.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerCoefficient();
        });
    }

    public void initPourModification(Matiere matiere) {
        this.matiereEnCours = matiere;
        champNom.setText(matiere.getNom());
        champCoefficient.setText(String.valueOf(matiere.getCoefficient()));
        champEnseignant.setText(matiere.getEnseignant());
    }

    public boolean isSauvegarde() {
        return sauvegarde;
    }

    private boolean validerNom() {
        if (champNom.getText() == null || champNom.getText().isBlank()) {
            afficherErreur(champNom, lblErreurNom, "Le nom de la matiere est obligatoire.");
            return false;
        }
        masquerErreur(champNom, lblErreurNom);
        return true;
    }

    private Double validerCoefficient() {
        String texte = champCoefficient.getText();
        if (texte == null || texte.isBlank()) {
            afficherErreur(champCoefficient, lblErreurCoefficient, "Le coefficient est obligatoire.");
            return null;
        }
        try {
            double valeur = Double.parseDouble(texte.trim().replace(",", "."));
            if (valeur <= 0) {
                afficherErreur(champCoefficient, lblErreurCoefficient, "Le coefficient doit etre superieur a 0.");
                return null;
            }
            masquerErreur(champCoefficient, lblErreurCoefficient);
            return valeur;
        } catch (NumberFormatException e) {
            afficherErreur(champCoefficient, lblErreurCoefficient, "Le coefficient doit etre un nombre.");
            return null;
        }
    }

    private void afficherErreur(TextField champ, Label lblErreur, String message) {
        lblErreur.setText(message);
        lblErreur.setVisible(true);
        lblErreur.setManaged(true);
        champ.setStyle("-fx-border-color: #D64545; -fx-border-width: 1.5;");
    }

    private void masquerErreur(TextField champ, Label lblErreur) {
        lblErreur.setVisible(false);
        lblErreur.setManaged(false);
        champ.setStyle("");
    }

    @FXML
    public void enregistrer() {
        boolean nomOk = validerNom();
        Double coefficient = validerCoefficient();

        if (!nomOk || coefficient == null) {
            return;
        }

        try {
            if (matiereEnCours == null) {
                Matiere nouvelle = new Matiere(champNom.getText().trim(), coefficient, champEnseignant.getText());
                matiereDAO.creer(nouvelle);
            } else {
                matiereEnCours.setNom(champNom.getText().trim());
                matiereEnCours.setCoefficient(coefficient);
                matiereEnCours.setEnseignant(champEnseignant.getText());
                matiereDAO.mettreAJour(matiereEnCours);
            }
            sauvegarde = true;
            fermerFenetre();
        } catch (RuntimeException e) {
            afficherErreur(champNom, lblErreurNom, "Impossible d'enregistrer la matiere.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champNom.getScene().getWindow();
        stage.close();
    }
}