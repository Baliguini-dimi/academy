package com.iua.academy.dao;

import com.iua.academy.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

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
}