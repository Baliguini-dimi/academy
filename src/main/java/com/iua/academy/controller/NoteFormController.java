package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.model.Etudiant;
import com.iua.academy.model.Matiere;
import com.iua.academy.model.Note;
import com.iua.academy.model.NoteDetaillee;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class NoteFormController {

    @FXML
    private ComboBox<Etudiant> comboEtudiant;
    @FXML
    private ComboBox<Matiere> comboMatiere;
    @FXML
    private TextField champValeur;
    @FXML
    private Label lblErreurValeur;
    @FXML
    private ComboBox<String> comboType;
    @FXML
    private DatePicker champDate;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private final NoteDAO noteDAO = new NoteDaoSqlite();

    private NoteDetaillee noteEnCours;
    private boolean sauvegarde = false;

    @FXML
    public void initialize() {
        comboEtudiant.setItems(FXCollections.observableArrayList(etudiantDAO.listerTous()));
        comboMatiere.setItems(FXCollections.observableArrayList(matiereDAO.listerTous()));
        comboType.setItems(FXCollections.observableArrayList("Devoir", "Interrogation", "Examen", "Projet"));
        champDate.setValue(LocalDate.now());

        // Validation en temps reel : des que l'utilisateur quitte le champ Note
        champValeur.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) {
                validerChampValeur();
            }
        });
    }

    public void initPourModification(NoteDetaillee note) {
        this.noteEnCours = note;

        comboEtudiant.getItems().stream()
            .filter(e -> e.getId() == note.getEtudiantId())
            .findFirst()
            .ifPresent(comboEtudiant::setValue);

        comboMatiere.getItems().stream()
            .filter(m -> m.getId() == note.getMatiereId())
            .findFirst()
            .ifPresent(comboMatiere::setValue);

        champValeur.setText(String.valueOf(note.getValeur()));
        comboType.setValue(note.getTypeEvaluation());
        champDate.setValue(note.getDateEvaluation());
    }

    public boolean isSauvegarde() {
        return sauvegarde;
    }

    /** Retourne la note validee (0-20) si le champ est correct, sinon affiche l'erreur inline et retourne null. */
    private Double validerChampValeur() {
        String texte = champValeur.getText();
        if (texte == null || texte.isBlank()) {
            masquerErreurValeur();
            return null;
        }

        double valeur;
        try {
            valeur = Double.parseDouble(texte.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            afficherErreurValeur("La note doit etre un nombre.");
            return null;
        }

        if (valeur < 0 || valeur > 20) {
            afficherErreurValeur("La note doit etre comprise entre 0 et 20.");
            return null;
        }

        masquerErreurValeur();
        return valeur;
    }

    private void afficherErreurValeur(String message) {
        lblErreurValeur.setText(message);
        lblErreurValeur.setVisible(true);
        lblErreurValeur.setManaged(true);
        champValeur.setStyle("-fx-border-color: #D64545; -fx-border-width: 1.5;");
    }

    private void masquerErreurValeur() {
        lblErreurValeur.setVisible(false);
        lblErreurValeur.setManaged(false);
        champValeur.setStyle("");
    }

    @FXML
    public void enregistrer() {
        Double valeur = validerChampValeur();
        if (valeur == null && champValeur.getText() != null && !champValeur.getText().isBlank()) {
            return; // erreur deja affichee par validerChampValeur()
        }
        if (!champsValides(valeur)) {
            return;
        }

        try {
            if (noteEnCours == null) {
                Note nouvelle = new Note(
                    comboEtudiant.getValue().getId(),
                    comboMatiere.getValue().getId(),
                    valeur,
                    comboType.getValue().trim(),
                    champDate.getValue()
                );
                noteDAO.creer(nouvelle);
            } else {
                Note miseAJour = new Note(
                    comboEtudiant.getValue().getId(),
                    comboMatiere.getValue().getId(),
                    valeur,
                    comboType.getValue().trim(),
                    champDate.getValue()
                );
                miseAJour.setId(noteEnCours.getId());
                noteDAO.mettreAJour(miseAJour);
            }
            sauvegarde = true;
            fermerFenetre();
        } catch (RuntimeException e) {
            afficherErreur("Impossible d'enregistrer la note.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private boolean champsValides(Double valeur) {
        boolean ok = comboEtudiant.getValue() != null
            && comboMatiere.getValue() != null
            && valeur != null
            && comboType.getValue() != null && !comboType.getValue().isBlank()
            && champDate.getValue() != null;
        if (!ok && valeur == null) {
            afficherErreurValeur("La note est obligatoire (entre 0 et 20).");
        } else if (!ok) {
            afficherErreur("Tous les champs marques * sont obligatoires.");
        }
        return ok;
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champValeur.getScene().getWindow();
        stage.close();
    }
}