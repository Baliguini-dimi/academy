package com.iua.academy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.awt.Desktop;
import java.net.URI;

/**
 * Controleur de l'ecran "A propos" : informations sur l'application et son concepteur.
 */
public class AboutController {

    private static final String EMAIL = "dbaliguini@gmail.com";
    private static final String GITHUB_URL = "https://github.com/Baliguini-dimi";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/in/dimitri-nelson-baligini-demba-4b17b32ba";

    @FXML
    public void ouvrirEmail() {
        ouvrirLien("mailto:" + EMAIL);
    }

    @FXML
    public void ouvrirGithub() {
        ouvrirLien(GITHUB_URL);
    }

    @FXML
    public void ouvrirLinkedin() {
        ouvrirLien(LINKEDIN_URL);
    }

    private void ouvrirLien(String lien) {
        try {
            Desktop.getDesktop().browse(new URI(lien));
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Lien");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'ouvrir automatiquement : " + lien);
            alert.showAndWait();
        }
    }
}