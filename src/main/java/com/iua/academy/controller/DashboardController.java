package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import com.iua.academy.dao.StatistiquesDAO;
import com.iua.academy.dao.StatistiquesDaoSqlite;
import com.iua.academy.model.ActiviteRecente;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Controleur du tableau de bord : statistiques globales, graphiques
 * de pilotage et activite recente, recalcules a chaque affichage de l'ecran.
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
    private BarChart<String, Number> chartClasses;
    @FXML
    private PieChart chartMentions;
    @FXML
    private BarChart<String, Number> chartMatieres;
    @FXML
    private LineChart<String, Number> chartEvolution;

    @FXML
    private VBox listeActivite;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private final NoteDAO noteDAO = new NoteDaoSqlite();
    private final StatistiquesDAO statistiquesDAO = new StatistiquesDaoSqlite();

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        chargerCartesKpi();
        chargerGraphiqueClasses();
        chargerGraphiqueMentions();
        chargerGraphiqueMatieres();
        chargerGraphiqueEvolution();
        chargerActiviteRecente();
    }

    private void chargerCartesKpi() {
        lblNbEtudiants.setText(String.valueOf(etudiantDAO.listerTous().size()));
        lblNbMatieres.setText(String.valueOf(matiereDAO.listerTous().size()));
        lblNbNotes.setText(String.valueOf(noteDAO.listerToutesDetaillees().size()));

        Double moyenneGenerale = statistiquesDAO.moyenneGenerale();
        lblMoyenneGenerale.setText(moyenneGenerale == null ? "-" : String.format("%.2f", moyenneGenerale));
    }

    private void chargerGraphiqueClasses() {
        LinkedHashMap<String, Integer> donnees = statistiquesDAO.etudiantsParClasse();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        donnees.forEach((classe, total) -> serie.getData().add(new XYChart.Data<>(classe, total)));

        chartClasses.getData().clear();
        chartClasses.getData().add(serie);
    }

    private void chargerGraphiqueMentions() {
        LinkedHashMap<String, Integer> donnees = statistiquesDAO.repartitionMentions();
        chartMentions.getData().clear();
        donnees.forEach((mention, total) ->
            chartMentions.getData().add(new PieChart.Data(mention + " (" + total + ")", total))
        );
    }

    private void chargerGraphiqueMatieres() {
        LinkedHashMap<String, Double> donnees = statistiquesDAO.moyenneParMatiere();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        donnees.forEach((matiere, moyenne) -> serie.getData().add(new XYChart.Data<>(matiere, moyenne)));

        chartMatieres.getData().clear();
        chartMatieres.getData().add(serie);
    }

    private void chargerGraphiqueEvolution() {
        LinkedHashMap<String, Integer> donnees = statistiquesDAO.notesParSemaine(8);
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        donnees.forEach((semaine, total) -> serie.getData().add(new XYChart.Data<>(semaine, total)));

        chartEvolution.getData().clear();
        chartEvolution.getData().add(serie);
    }

    private void chargerActiviteRecente() {
        listeActivite.getChildren().clear();
        List<ActiviteRecente> activites = statistiquesDAO.activiteRecente(6);

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

        Label description = new Label(activite.getDescription());
        description.getStyleClass().add("activity-desc");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label date = new Label(activite.getDate().format(FORMAT_DATE));
        date.getStyleClass().add("activity-date");

        HBox ligne = new HBox(10, icone, description, spacer, date);
        ligne.getStyleClass().add("activity-row");
        ligne.setPadding(new Insets(10, 4, 10, 4));
        return ligne;
    }
}