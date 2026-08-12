package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.model.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controleur du formulaire etudiant.
 * Sert a la fois pour la creation (etudiantEnCours == null)
 * et pour la modification (etudiantEnCours pre-rempli via initPourModification).
 */
public class EtudiantFormController {

    @FXML
    private TextField champMatricule;
    @FXML
    private TextField champNom;
    @FXML
    private TextField champPrenom;
    @FXML
    private DatePicker champDateNaissance;
    @FXML
    private TextField champClasse;
    @FXML
    private TextField champEmail;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private Etudiant etudiantEnCours;
    private boolean sauvegarde = false;

    /** A appeler juste apres le chargement du FXML pour passer en mode modification. */
    public void initPourModification(Etudiant etudiant) {
        this.etudiantEnCours = etudiant;
        champMatricule.setText(etudiant.getMatricule());
        champNom.setText(etudiant.getNom());
        champPrenom.setText(etudiant.getPrenom());
        champDateNaissance.setValue(etudiant.getDateNaissance());
        champClasse.setText(etudiant.getClasse());
        champEmail.setText(etudiant.getEmail());
    }

    public boolean isSauvegarde() {
        return sauvegarde;
    }

    @FXML
    public void enregistrer() {
        if (!champsValides()) {
            return;
        }
        try {
            if (etudiantEnCours == null) {
                Etudiant nouveau = new Etudiant(
                    champMatricule.getText().trim(),
                    champNom.getText().trim(),
                    champPrenom.getText().trim(),
                    champDateNaissance.getValue(),
                    champClasse.getText().trim(),
                    champEmail.getText().trim()
                );
                etudiantDAO.creer(nouveau);
            } else {
                etudiantEnCours.setMatricule(champMatricule.getText().trim());
                etudiantEnCours.setNom(champNom.getText().trim());
                etudiantEnCours.setPrenom(champPrenom.getText().trim());
                etudiantEnCours.setDateNaissance(champDateNaissance.getValue());
                etudiantEnCours.setClasse(champClasse.getText().trim());
                etudiantEnCours.setEmail(champEmail.getText().trim());
                etudiantDAO.mettreAJour(etudiantEnCours);
            }
            sauvegarde = true;
            fermerFenetre();
        } catch (RuntimeException e) {
            afficherErreur("Impossible d'enregistrer l'etudiant. Le matricule est peut-etre deja utilise par un autre etudiant.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private boolean champsValides() {
        boolean ok = notBlank(champMatricule.getText())
            && notBlank(champNom.getText())
            && notBlank(champPrenom.getText())
            && notBlank(champClasse.getText());
        if (!ok) {
            afficherErreur("Matricule, nom, prenom et classe sont obligatoires.");
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
        Stage stage = (Stage) champMatricule.getScene().getWindow();
        stage.close();
    }
}