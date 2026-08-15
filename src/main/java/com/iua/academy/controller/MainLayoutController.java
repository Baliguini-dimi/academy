package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.model.Etudiant;
import com.iua.academy.model.Matiere;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controleur du layout principal : navigation, sidebar reductible et recherche globale.
 * Expose une instance statique pour permettre aux ecrans enfants (dashboard) de naviguer.
 */
public class MainLayoutController {

    private static MainLayoutController instance;

    public static MainLayoutController getInstance() {
        return instance;
    }

    @FXML
    private StackPane contentArea;
    @FXML
    private VBox sidebarBox;
    @FXML
    private VBox sidebarHeaderText;
    @FXML
    private javafx.scene.control.Label lblGroupePrincipal;
    @FXML
    private javafx.scene.control.Label lblGroupeConfiguration;
    @FXML
    private TextField champRechercheGlobale;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnEtudiants;
    @FXML
    private Button btnMatieres;
    @FXML
    private Button btnNotes;
    @FXML
    private Button btnParametres;
    @FXML
    private Button btnAbout;

    private boolean sidebarReduite = false;
    private static final double LARGEUR_ETENDUE = 240;
    private static final double LARGEUR_REDUITE = 74;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private final ContextMenu menuResultatsRecherche = new ContextMenu();

    @FXML
    public void initialize() {
        instance = this;
        installerTooltips();
        initialiserRechercheGlobale();
        afficherDashboard();
    }

    private void installerTooltips() {
        Tooltip.install(btnDashboard, new Tooltip("Tableau de bord"));
        Tooltip.install(btnEtudiants, new Tooltip("Etudiants"));
        Tooltip.install(btnMatieres, new Tooltip("Matieres"));
        Tooltip.install(btnNotes, new Tooltip("Notes"));
        Tooltip.install(btnParametres, new Tooltip("Parametres"));
        Tooltip.install(btnAbout, new Tooltip("A propos"));
    }

    @FXML
    public void toggleSidebar() {
        sidebarReduite = !sidebarReduite;
        double largeur = sidebarReduite ? LARGEUR_REDUITE : LARGEUR_ETENDUE;
        sidebarBox.setPrefWidth(largeur);
        sidebarBox.setMinWidth(largeur);
        sidebarBox.setMaxWidth(largeur);

        sidebarHeaderText.setVisible(!sidebarReduite);
        sidebarHeaderText.setManaged(!sidebarReduite);
        lblGroupePrincipal.setVisible(!sidebarReduite);
        lblGroupePrincipal.setManaged(!sidebarReduite);
        lblGroupeConfiguration.setVisible(!sidebarReduite);
        lblGroupeConfiguration.setManaged(!sidebarReduite);

        ContentDisplay affichage = sidebarReduite ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT;
        for (Button b : new Button[]{btnDashboard, btnEtudiants, btnMatieres, btnNotes, btnParametres, btnAbout}) {
            b.setContentDisplay(affichage);
            if (sidebarReduite) {
                b.getStyleClass().add("nav-button-collapsed");
            } else {
                b.getStyleClass().remove("nav-button-collapsed");
            }
        }
    }

    private void initialiserRechercheGlobale() {
        champRechercheGlobale.setOnKeyReleased(e -> executerRechercheGlobale());
        champRechercheGlobale.sceneProperty().addListener((obs, ancienne, nouvelle) -> {
            if (nouvelle != null) {
                nouvelle.getAccelerators().put(
                    KeyCombination.keyCombination("Ctrl+K"),
                    () -> champRechercheGlobale.requestFocus()
                );
            }
        });
    }

    private void executerRechercheGlobale() {
        menuResultatsRecherche.hide();
        menuResultatsRecherche.getItems().clear();

        String motCle = champRechercheGlobale.getText();
        if (motCle == null || motCle.trim().length() < 2) {
            return;
        }
        String recherche = motCle.trim();

        List<Etudiant> etudiants = etudiantDAO.rechercher(recherche).stream().limit(4).collect(Collectors.toList());
        List<Matiere> matieres = matiereDAO.rechercher(recherche).stream().limit(4).collect(Collectors.toList());

        for (Etudiant e : etudiants) {
            MenuItem item = new MenuItem("Etudiant : " + e.getNomComplet() + " (" + e.getMatricule() + ")");
            item.setOnAction(ev -> {
                afficherEtudiants();
                menuResultatsRecherche.hide();
            });
            menuResultatsRecherche.getItems().add(item);
        }
        for (Matiere m : matieres) {
            MenuItem item = new MenuItem("Matiere : " + m.getNom());
            item.setOnAction(ev -> {
                afficherMatieres();
                menuResultatsRecherche.hide();
            });
            menuResultatsRecherche.getItems().add(item);
        }

        if (menuResultatsRecherche.getItems().isEmpty()) {
            MenuItem aucun = new MenuItem("Aucun resultat");
            aucun.setDisable(true);
            menuResultatsRecherche.getItems().add(aucun);
        }

        if (!menuResultatsRecherche.isShowing()) {
            menuResultatsRecherche.show(champRechercheGlobale, Side.BOTTOM, 0, 4);
        }
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

    @FXML
    public void afficherParametres() {
        chargerEcran("/com/iua/academy/fxml/parametres.fxml", btnParametres);
    }

    @FXML
    public void afficherAbout() {
        chargerEcran("/com/iua/academy/fxml/about.fxml", btnAbout);
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
        for (Button b : new Button[]{btnDashboard, btnEtudiants, btnMatieres, btnNotes, btnParametres, btnAbout}) {
            b.getStyleClass().remove("nav-button-active");
        }
        boutonActif.getStyleClass().add("nav-button-active");
    }
}