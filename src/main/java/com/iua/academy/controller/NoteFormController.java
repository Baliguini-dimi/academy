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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

/**
 * Controleur du formulaire note, avec validation en temps reel :
 * chaque erreur s'affiche pres du champ concerne, jamais en popup.
 */
public class NoteFormController {

    @FXML
    private ComboBox<Etudiant> comboEtudiant;
    @FXML
    private Label lblErreurEtudiant;
    @FXML
    private ComboBox<Matiere> comboMatiere;
    @FXML
    private Label lblErreurMatiere;
    @FXML
    private TextField champValeur;
    @FXML
    private Label lblErreurValeur;
    @FXML
    private ComboBox<String> comboType;
    @FXML
    private Label lblErreurType;
    @FXML
    private DatePicker champDate;
    @FXML
    private Label lblErreurDate;

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

        champValeur.focusedProperty().addListener((obs, etaitFocus, estFocus) -> {
            if (!estFocus) validerValeur();
        });
        comboEtudiant.valueProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) masquerErreur(comboEtudiant, lblErreurEtudiant);
        });
        comboMatiere.valueProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) masquerErreur(comboMatiere, lblErreurMatiere);
        });
        comboType.valueProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null && !nouveau.isBlank()) masquerErreur(comboType, lblErreurType);
        });
        champDate.valueProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau != null) masquerErreur(champDate, lblErreurDate);
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

    private Double validerValeur() {
        String texte = champValeur.getText();
        if (texte == null || texte.isBlank()) {
            afficherErreur(champValeur, lblErreurValeur, "La note est obligatoire.");
            return null;
        }
        double valeur;
        try {
            valeur = Double.parseDouble(texte.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            afficherErreur(champValeur, lblErreurValeur, "La note doit etre un nombre.");
            return null;
        }
        if (valeur < 0 || valeur > 20) {
            afficherErreur(champValeur, lblErreurValeur, "La note doit etre comprise entre 0 et 20.");
            return null;
        }
        masquerErreur(champValeur, lblErreurValeur);
        return valeur;
    }

    private void afficherErreur(Control champ, Label lblErreur, String message) {
        lblErreur.setText(message);
        lblErreur.setVisible(true);
        lblErreur.setManaged(true);
        champ.setStyle("-fx-border-color: #D64545; -fx-border-width: 1.5;");
    }

    private void masquerErreur(Control champ, Label lblErreur) {
        lblErreur.setVisible(false);
        lblErreur.setManaged(false);
        champ.setStyle("");
    }

    @FXML
    public void enregistrer() {
        Double valeur = validerValeur();
        boolean etudiantOk = comboEtudiant.getValue() != null;
        boolean matiereOk = comboMatiere.getValue() != null;
        boolean typeOk = comboType.getValue() != null && !comboType.getValue().isBlank();
        boolean dateOk = champDate.getValue() != null;

        if (!etudiantOk) afficherErreur(comboEtudiant, lblErreurEtudiant, "Selectionnez un etudiant.");
        if (!matiereOk) afficherErreur(comboMatiere, lblErreurMatiere, "Selectionnez une matiere.");
        if (!typeOk) afficherErreur(comboType, lblErreurType, "Le type d'evaluation est obligatoire.");
        if (!dateOk) afficherErreur(champDate, lblErreurDate, "La date est obligatoire.");

        if (valeur == null || !etudiantOk || !matiereOk || !typeOk || !dateOk) {
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
            afficherErreur(champValeur, lblErreurValeur, "Impossible d'enregistrer la note.");
        }
    }

    @FXML
    public void annuler() {
        sauvegarde = false;
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) champValeur.getScene().getWindow();
        stage.close();
    }
}