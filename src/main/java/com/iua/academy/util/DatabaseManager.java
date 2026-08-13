package com.iua.academy.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gestionnaire centralise de la connexion a la base SQLite.
 * Le fichier de base est cree dans un dossier applicatif dedie (hors du jar),
 * pour que les donnees survivent aux mises a jour de l'application.
 */
public final class DatabaseManager {

    private static final DatabaseManager INSTANCE = new DatabaseManager();

    private static final String APP_FOLDER_NAME = "Academy";
    private static final String DB_FILE_NAME = "academy.db";
    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private Connection connection;

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    /**
     * Ouvre la connexion SQLite et cree les tables si elles n'existent pas encore.
     */
    public void initialize() {
        try {
            Path dbPath = resolveDatabasePath();
            Files.createDirectories(dbPath.getParent());

            String url = "jdbc:sqlite:" + dbPath.toAbsolutePath();
            connection = DriverManager.getConnection(url);

            try (Statement pragma = connection.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON");
            }

            runSchemaScript();
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Impossible d'initialiser la base de donnees Academy", e);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException("La base de donnees n'a pas ete initialisee. Appelez initialize() au demarrage.");
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Fermeture silencieuse : on quitte l'application de toute facon
        }
    }

    /**
     * Le fichier .db est stocke dans le dossier utilisateur (~/.academy/academy.db)
     * plutot que dans le dossier d'installation, pour eviter les problemes de droits d'ecriture.
     */
    private Path resolveDatabasePath() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, "Documents", APP_FOLDER_NAME, DB_FILE_NAME);
    }

    private void runSchemaScript() throws SQLException, IOException {
        try (InputStream is = getClass().getResourceAsStream(SCHEMA_RESOURCE)) {
            if (is == null) {
                throw new IOException("Script de schema introuvable : " + SCHEMA_RESOURCE);
            }
            String script = new String(is.readAllBytes());
            try (Statement statement = connection.createStatement()) {
                for (String rawStatement : script.split(";")) {
                    String sql = rawStatement.trim();
                    if (!sql.isEmpty()) {
                        statement.execute(sql);
                    }
                }
            }
        }
    }

    /** Chemin absolu du fichier de base, utilise notamment pour l'export de sauvegarde. */
    public java.nio.file.Path getDatabaseFilePath() {
        return resolveDatabasePath();
    }
}
