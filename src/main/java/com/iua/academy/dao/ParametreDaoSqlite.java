package com.iua.academy.dao;

import com.iua.academy.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ParametreDaoSqlite implements ParametreDAO {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    @Override
    public Map<String, String> listerTous() {
        String sql = "SELECT cle, valeur FROM parametre";
        Map<String, String> resultats = new HashMap<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.put(rs.getString("cle"), rs.getString("valeur"));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du listing des parametres", e);
        }
    }

    @Override
    public String obtenir(String cle, String valeurParDefaut) {
        String sql = "SELECT valeur FROM parametre WHERE cle = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, cle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valeur");
                }
            }
            return valeurParDefaut;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la lecture du parametre " + cle, e);
        }
    }

    @Override
    public void enregistrer(String cle, String valeur) {
        String sql = """
            INSERT INTO parametre (cle, valeur) VALUES (?, ?)
            ON CONFLICT(cle) DO UPDATE SET valeur = excluded.valeur
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, cle);
            ps.setString(2, valeur);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du parametre " + cle, e);
        }
    }
}