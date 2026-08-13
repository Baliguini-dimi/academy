package com.iua.academy;

import com.iua.academy.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entree de l'application Academy.
 * Academy est un outil de gestion des etudiants, des matieres et des notes,
 * developpe pour l'Institut Universitaire d'Abidjan.
 */
public class App extends Application {

    private static final String APP_TITLE = "Academy";
    private static final int WINDOW_MIN_WIDTH = 1100;
    private static final int WINDOW_MIN_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialise la base de donnees (creation du fichier + tables si absentes)
        DatabaseManager.getInstance().initialize();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/iua/academy/fxml/main-layout.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/com/iua/academy/css/theme.css").toExternalForm());

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        try {
            primaryStage.getIcons().add(
                new Image(getClass().getResourceAsStream("/com/iua/academy/images/logo.png"))
            );
        } catch (Exception e) {
            // Icone optionnelle : on ne bloque pas le lancement si l'image n'existe pas encore
        }

        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
