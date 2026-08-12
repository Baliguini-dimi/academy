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
    }

    /** A appeler juste apres le chargement du FXML pour passer en mode modification. */
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

    @FXML
    public void enregistrer() {
        if (!champsValides()) {
            return;
        }

        double valeur;
        try {
            valeur = Double.parseDouble(champValeur.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            afficherErreur("La note doit etre un nombre.");
            return;
        }
        if (valeur < 0 || valeur > 20) {
            afficherErreur("La note doit etre comprise entre 0 et 20.");
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

    private boolean champsValides() {
        boolean ok = comboEtudiant.getValue() != null
            && comboMatiere.getValue() != null
            && champValeur.getText() != null && !champValeur.getText().isBlank()
            && comboType.getValue() != null && !comboType.getValue().isBlank()
            && champDate.getValue() != null;
        if (!ok) {
            afficherErreur("Tous les champs marques * sont obligatoires.");
        }
        return ok;
    }

    private void afficherErreur(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champValeur.getScene().getWindow();
        stage.close();
    }
}