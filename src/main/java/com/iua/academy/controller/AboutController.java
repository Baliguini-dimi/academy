package com.iua.academy.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.net.URI;

/**
 * Controleur de l'ecran "A propos" : informations sur l'application et son concepteur.
 * Les liens (email, GitHub, LinkedIn) sont a completer dans les constantes ci-dessous.
 */
public class AboutController {

    // TODO : remplacer par ton vrai lien LinkedIn quand tu l'auras
    private static final String EMAIL = "dbaliguini@gmail.com";
    private static final String GITHUB_URL = "https://github.com/Baliguini-dimi";
    private static final String LINKEDIN_URL = "https://www.linkedin.com/in/TON-PROFIL";

    @FXML
    private ImageView photoConcepteur;

    @FXML
    public void initialize() {
        try {
            // Place ta photo dans src/main/resources/com/iua/academy/images/concepteur.jpg
            Image photo = new Image(getClass().getResourceAsStream("/com/iua/academy/images/concepteur.jpg"));
            photoConcepteur.setImage(photo);
        } catch (Exception e) {
            // Pas de photo pour l'instant : le cercle reste vide, sans bloquer l'ecran
        }
    }

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