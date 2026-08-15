package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.model.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controleur du formulaire etudiant, avec validation en temps reel
 * (erreurs affichees pres du champ concerne, sans popup).
 */
public class EtudiantFormController {

    @FXML
    private TextField champMatricule;
    @FXML
    private Label lblErreurMatricule;
    @FXML
    private TextField champNom;
    @FXML
    private Label lblErreurNom;
    @FXML
    private TextField champPrenom;
    @FXML
    private Label lblErreurPrenom;
    @FXML
    private DatePicker champDateNaissance;
    @FXML
    private TextField champEmail;
    @FXML
    private TextField champClasse;
    @FXML
    private Label lblErreurClasse;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private Etudiant etudiantEnCours;
    private boolean sauvegarde = false;

    @FXML
    public void initialize() {
        champMatricule.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerMatricule();
        });
        champNom.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerChampObligatoire(champNom, lblErreurNom, "Le nom est obligatoire.");
        });
        champPrenom.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerChampObligatoire(champPrenom, lblErreurPrenom, "Le prenom est obligatoire.");
        });
        champClasse.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerChampObligatoire(champClasse, lblErreurClasse, "La classe est obligatoire.");
        });
    }

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

    private boolean validerMatricule() {
        String matricule = champMatricule.getText();
        if (matricule == null || matricule.isBlank()) {
            afficherErreurChamp(champMatricule, lblErreurMatricule, "Le matricule est obligatoire.");
            return false;
        }
        Integer idAExclure = (etudiantEnCours != null) ? etudiantEnCours.getId() : null;
        if (etudiantDAO.matriculeExiste(matricule.trim(), idAExclure)) {
            afficherErreurChamp(champMatricule, lblErreurMatricule, "Ce matricule est deja utilise.");
            return false;
        }
        masquerErreurChamp(champMatricule, lblErreurMatricule);
        return true;
    }

    private boolean validerChampObligatoire(TextField champ, Label lblErreur, String messageErreur) {
        if (champ.getText() == null || champ.getText().isBlank()) {
            afficherErreurChamp(champ, lblErreur, messageErreur);
            return false;
        }
        masquerErreurChamp(champ, lblErreur);
        return true;
    }

    private void afficherErreurChamp(TextField champ, Label lblErreur, String message) {
        lblErreur.setText(message);
        lblErreur.setVisible(true);
        lblErreur.setManaged(true);
        champ.setStyle("-fx-border-color: #D64545; -fx-border-width: 1.5;");
    }

    private void masquerErreurChamp(TextField champ, Label lblErreur) {
        lblErreur.setVisible(false);
        lblErreur.setManaged(false);
        champ.setStyle("");
    }

    @FXML
    public void enregistrer() {
        boolean matriculeOk = validerMatricule();
        boolean nomOk = validerChampObligatoire(champNom, lblErreurNom, "Le nom est obligatoire.");
        boolean prenomOk = validerChampObligatoire(champPrenom, lblErreurPrenom, "Le prenom est obligatoire.");
        boolean classeOk = validerChampObligatoire(champClasse, lblErreurClasse, "La classe est obligatoire.");

        if (!matriculeOk || !nomOk || !prenomOk || !classeOk) {
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
                    champEmail.getText() == null ? "" : champEmail.getText().trim()
                );
                etudiantDAO.creer(nouveau);
            } else {
                etudiantEnCours.setMatricule(champMatricule.getText().trim());
                etudiantEnCours.setNom(champNom.getText().trim());
                etudiantEnCours.setPrenom(champPrenom.getText().trim());
                etudiantEnCours.setDateNaissance(champDateNaissance.getValue());
                etudiantEnCours.setClasse(champClasse.getText().trim());
                etudiantEnCours.setEmail(champEmail.getText() == null ? "" : champEmail.getText().trim());
                etudiantDAO.mettreAJour(etudiantEnCours);
            }
            sauvegarde = true;
            fermerFenetre();
        } catch (RuntimeException e) {
            afficherErreurChamp(champMatricule, lblErreurMatricule, "Impossible d'enregistrer l'etudiant.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champMatricule.getScene().getWindow();
        stage.close();
    }
}