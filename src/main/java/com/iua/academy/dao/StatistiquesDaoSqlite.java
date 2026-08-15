package com.iua.academy.dao;

import com.iua.academy.model.ActiviteRecente;
import com.iua.academy.util.DatabaseManager;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class StatistiquesDaoSqlite implements StatistiquesDAO {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    @Override
    public LinkedHashMap<String, Integer> etudiantsParClasse() {
        String sql = "SELECT classe, COUNT(*) AS total FROM etudiant GROUP BY classe ORDER BY classe";
        LinkedHashMap<String, Integer> resultats = new LinkedHashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.put(rs.getString("classe"), rs.getInt("total"));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la repartition par classe", e);
        }
    }

    @Override
    public LinkedHashMap<String, Double> moyenneParMatiere() {
        String sql = """
            SELECT m.nom AS matiere, AVG(n.valeur) AS moyenne
            FROM note n
            JOIN matiere m ON m.id = n.matiere_id
            GROUP BY m.nom
            ORDER BY m.nom
            """;
        LinkedHashMap<String, Double> resultats = new LinkedHashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.put(rs.getString("matiere"), rs.getDouble("moyenne"));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la moyenne par matiere", e);
        }
    }

    @Override
    public LinkedHashMap<String, Integer> repartitionMentions() {
        String sql = """
            SELECT
                CASE
                    WHEN valeur < 10 THEN '1-Insuffisant'
                    WHEN valeur < 12 THEN '2-Passable'
                    WHEN valeur < 14 THEN '3-Assez bien'
                    WHEN valeur < 16 THEN '4-Bien'
                    ELSE '5-Excellent'
                END AS mention,
                COUNT(*) AS total
            FROM note
            GROUP BY mention
            ORDER BY mention
            """;
        LinkedHashMap<String, Integer> resultats = new LinkedHashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                // On retire le prefixe numerique utilise seulement pour trier
                String mention = rs.getString("mention").substring(2);
                resultats.put(mention, rs.getInt("total"));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la repartition des mentions", e);
        }
    }

    @Override
    public LinkedHashMap<String, Integer> notesParSemaine(int nombreSemaines) {
        String sql = """
            SELECT strftime('%Y-W%W', date_evaluation) AS semaine, COUNT(*) AS total
            FROM note
            GROUP BY semaine
            ORDER BY semaine DESC
            LIMIT ?
            """;
        LinkedHashMap<String, Integer> brut = new LinkedHashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, nombreSemaines);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    brut.put(rs.getString("semaine"), rs.getInt("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de l'evolution des notes", e);
        }

        // On remet dans l'ordre chronologique (LIMIT + DESC nous donne les plus recentes d'abord)
        LinkedHashMap<String, Integer> ordonne = new LinkedHashMap<>();
        brut.entrySet().stream()
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .forEach(entry -> ordonne.put(entry.getKey(), entry.getValue()));
        return ordonne;
    }

    @Override
    public Double moyenneGenerale() {
        String sql = """
            SELECT SUM(n.valeur * m.coefficient) AS total_pondere, SUM(m.coefficient) AS total_coefficient
            FROM note n
            JOIN matiere m ON m.id = n.matiere_id
            """;
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                double totalCoefficient = rs.getDouble("total_coefficient");
                if (totalCoefficient == 0) {
                    return null;
                }
                return rs.getDouble("total_pondere") / totalCoefficient;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la moyenne generale", e);
        }
    }

    @Override
    public List<ActiviteRecente> activiteRecente(int limite) {
        List<ActiviteRecente> resultats = new ArrayList<>();

        String sqlEtudiants = """
            SELECT prenom, nom, classe, date_creation
            FROM etudiant
            ORDER BY date_creation DESC
            LIMIT ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sqlEtudiants)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String description = rs.getString("prenom") + " " + rs.getString("nom")
                        + " a rejoint " + rs.getString("classe");
                    LocalDate date = LocalDate.parse(rs.getString("date_creation").substring(0, 10));
                    resultats.add(new ActiviteRecente("etudiant", description, date));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des inscriptions recentes", e);
        }

        String sqlNotes = """
            SELECT e.prenom, e.nom, m.nom AS matiere, n.valeur, n.date_evaluation
            FROM note n
            JOIN etudiant e ON e.id = n.etudiant_id
            JOIN matiere m ON m.id = n.matiere_id
            ORDER BY n.date_evaluation DESC
            LIMIT ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sqlNotes)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String description = rs.getString("prenom") + " " + rs.getString("nom")
                        + " - " + rs.getString("matiere") + " : "
                        + String.format("%.1f", rs.getDouble("valeur")) + "/20";
                    LocalDate date = LocalDate.parse(rs.getString("date_evaluation"));
                    resultats.add(new ActiviteRecente("note", description, date));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des notes recentes", e);
        }

        resultats.sort(Comparator.comparing(ActiviteRecente::getDate).reversed());
        return resultats.size() > limite ? resultats.subList(0, limite) : resultats;
    }

    @Override
    public LinkedHashMap<String, Double> moyenneParSemaine(int nombreSemaines) {
        String sql = """
            SELECT strftime('%Y-W%W', date_evaluation) AS semaine, AVG(valeur) AS moyenne
            FROM note
            GROUP BY semaine
            ORDER BY semaine DESC
            LIMIT ?
            """;
        LinkedHashMap<String, Double> brut = new LinkedHashMap<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, nombreSemaines);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    brut.put(rs.getString("semaine"), rs.getDouble("moyenne"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de l'evolution des moyennes", e);
        }

        LinkedHashMap<String, Double> ordonne = new LinkedHashMap<>();
        brut.entrySet().stream()
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .forEach(entry -> ordonne.put(entry.getKey(), entry.getValue()));
        return ordonne;
    }

    @Override
    public Double tauxReussite(double seuil) {
        String sql = "SELECT COUNT(*) AS total, SUM(CASE WHEN valeur >= ? THEN 1 ELSE 0 END) AS reussies FROM note";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setDouble(1, seuil);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    if (total == 0) {
                        return null;
                    }
                    return rs.getInt("reussies") * 100.0 / total;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du taux de reussite", e);
        }
    }

    @Override
    public int notesSaisiesCeMois() {
        String sql = "SELECT COUNT(*) AS total FROM note WHERE strftime('%Y-%m', date_evaluation) = strftime('%Y-%m', 'now')";
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des notes du mois", e);
        }
    }
}
