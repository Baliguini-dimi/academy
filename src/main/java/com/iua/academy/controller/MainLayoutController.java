package com.iua.academy.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controleur du layout principal : gere la barre laterale et le chargement
 * dynamique des differents ecrans dans la zone de contenu.
 */
public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnEtudiants;
    @FXML
    private Button btnMatieres;
    @FXML
    private Button btnNotes;

    @FXML
    public void initialize() {
        afficherDashboard();
    }

    @FXML
    public void afficherDashboard() {
        chargerEcran("/com/iua/academy/fxml/dashboard.fxml", btnDashboard);
    }

    @FXML
    public void afficherEtudiants() {
        chargerEcran("/com/iua/academy/fxml/etudiants.fxml", btnEtudiants);
    }

    @FXML
    public void afficherMatieres() {
        chargerEcran("/com/iua/academy/fxml/matieres.fxml", btnMatieres);
    }

    @FXML
    public void afficherNotes() {
        chargerEcran("/com/iua/academy/fxml/notes.fxml", btnNotes);
    }

    private void chargerEcran(String fxmlPath, Button boutonActif) {
        try {
            Parent ecran = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(ecran);
            mettreAJourBoutonActif(boutonActif);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de charger l'ecran : " + fxmlPath, e);
        }
    }

    private void mettreAJourBoutonActif(Button boutonActif) {
        for (Button b : new Button[]{btnDashboard, btnEtudiants, btnMatieres, btnNotes}) {
            b.getStyleClass().remove("nav-button-active");
        }
        boutonActif.getStyleClass().add("nav-button-active");
    }
}
