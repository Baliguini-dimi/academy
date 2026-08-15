package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.dao.ParametreDAO;
import com.iua.academy.dao.ParametreDaoSqlite;
import com.iua.academy.dao.StatistiquesDAO;
import com.iua.academy.dao.StatistiquesDaoSqlite;
import com.iua.academy.model.ActiviteRecente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Controleur du tableau de bord : KPI, graphiques, actions rapides et activite recente.
 */
public class DashboardController {

    @FXML
    private Label lblNbEtudiants;
    @FXML
    private Label lblNbMatieres;
    @FXML
    private Label lblNbNotes;
    @FXML
    private Label lblMoyenneGenerale;
    @FXML
    private Label lblDonutTotal;
    @FXML
    private Label lblTauxReussite;
    @FXML
    private Label lblNotesCeMois;

    @FXML
    private javafx.scene.chart.LineChart<String, Number> chartEvolution;
    @FXML
    private PieChart chartClasses;

    @FXML
    private VBox listeActivite;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private final NoteDAO noteDAO = new NoteDaoSqlite();
    private final ParametreDAO parametreDAO = new ParametreDaoSqlite();
    private final StatistiquesDAO statistiquesDAO = new StatistiquesDaoSqlite();

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        rafraichirTout();
    }

    private void rafraichirTout() {
        chargerCartesKpi();
        chargerGraphiqueEvolution();
        chargerDonutClasses();
        chargerStatistiquesRapides();
        chargerActiviteRecente();
    }

    private void chargerCartesKpi() {
        lblNbEtudiants.setText(String.valueOf(etudiantDAO.listerTous().size()));
        lblNbMatieres.setText(String.valueOf(matiereDAO.listerTous().size()));
        lblNbNotes.setText(String.valueOf(noteDAO.listerToutesDetaillees().size()));

        Double moyenneGenerale = statistiquesDAO.moyenneGenerale();
        lblMoyenneGenerale.setText(moyenneGenerale == null ? "-" : String.format("%.2f", moyenneGenerale));
    }

    private void chargerGraphiqueEvolution() {
        LinkedHashMap<String, Double> donnees = statistiquesDAO.moyenneParSemaine(8);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        donnees.forEach((semaine, moyenne) -> serie.getData().add(new XYChart.Data<>(semaine, moyenne)));

        chartEvolution.getData().clear();
        chartEvolution.getData().add(serie);
    }

    private void chargerDonutClasses() {
        LinkedHashMap<String, Integer> donnees = statistiquesDAO.etudiantsParClasse();
        chartClasses.getData().clear();
        int total = 0;
        for (var entry : donnees.entrySet()) {
            chartClasses.getData().add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            total += entry.getValue();
        }
        lblDonutTotal.setText(String.valueOf(total));
    }

    private void chargerStatistiquesRapides() {
        double seuil = Double.parseDouble(parametreDAO.obtenir("seuil_validation", "10"));
        Double taux = statistiquesDAO.tauxReussite(seuil);
        lblTauxReussite.setText(taux == null ? "-" : String.format("%.1f %%", taux));
        lblNotesCeMois.setText(String.valueOf(statistiquesDAO.notesSaisiesCeMois()));
    }

    private void chargerActiviteRecente() {
        listeActivite.getChildren().clear();
        List<ActiviteRecente> activites = statistiquesDAO.activiteRecente(8);

        if (activites.isEmpty()) {
            Label vide = new Label("Aucune activite pour le moment.");
            vide.getStyleClass().add("empty-state-text");
            listeActivite.getChildren().add(vide);
            return;
        }

        for (ActiviteRecente activite : activites) {
            listeActivite.getChildren().add(construireLigneActivite(activite));
        }
    }

    private HBox construireLigneActivite(ActiviteRecente activite) {
        FontIcon icone = new FontIcon();
        icone.setIconSize(16);
        if ("etudiant".equals(activite.getType())) {
            icone.setIconLiteral("mdi2a-account-plus-outline");
            icone.getStyleClass().add("activity-icon-etudiant");
        } else {
            icone.setIconLiteral("mdi2c-clipboard-check-outline");
            icone.getStyleClass().add("activity-icon-note");
        }

        VBox texte = new VBox(2);
        Label description = new Label(activite.getDescription());
        description.getStyleClass().add("activity-desc");
        description.setWrapText(true);
        Label date = new Label(activite.getDate().format(FORMAT_DATE));
        date.getStyleClass().add("activity-date");
        texte.getChildren().addAll(description, date);

        HBox ligne = new HBox(10, icone, texte);
        ligne.getStyleClass().add("activity-row");
        ligne.setPadding(new Insets(10, 4, 10, 4));
        return ligne;
    }

    // ===== Actions rapides =====

    @FXML
    public void nouvelEtudiant() {
        ouvrirFormulaireModal("/com/iua/academy/fxml/etudiant-form.fxml", "Nouvel etudiant", "EtudiantFormController");
    }

    @FXML
    public void nouvelleMatiere() {
        ouvrirFormulaireModal("/com/iua/academy/fxml/matiere-form.fxml", "Nouvelle matiere", "MatiereFormController");
    }

    @FXML
    public void nouvelleNote() {
        ouvrirFormulaireModal("/com/iua/academy/fxml/note-form.fxml", "Nouvelle note", "NoteFormController");
    }

    private void ouvrirFormulaireModal(String fxmlPath, String titre, String typeControleur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent racine = loader.load();

            Stage fenetre = new Stage();
            fenetre.setTitle(titre);
            fenetre.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(racine);
            scene.getStylesheets().add(getClass().getResource("/com/iua/academy/css/theme.css").toExternalForm());
            fenetre.setScene(scene);
            fenetre.showAndWait();

            // Quel que soit le resultat, on rafraichit : simple et sans risque
            rafraichirTout();
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'ouvrir le formulaire : " + fxmlPath, e);
        }
    }

    @FXML
    public void allerVersBulletin() {
        MainLayoutController.getInstance().afficherEtudiants();
    }

    @FXML
    public void allerVersEtudiants() {
        MainLayoutController.getInstance().afficherEtudiants();
    }

    @FXML
    public void allerVersMatieres() {
        MainLayoutController.getInstance().afficherMatieres();
    }

    @FXML
    public void allerVersNotes() {
        MainLayoutController.getInstance().afficherNotes();
    }
}