package com.iua.academy.dao;

import com.iua.academy.model.Matiere;
import com.iua.academy.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatiereDaoSqlite implements MatiereDAO {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    @Override
    public Matiere creer(Matiere matiere) {
        String sql = "INSERT INTO matiere (nom, coefficient, enseignant) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, matiere.getNom());
            ps.setDouble(2, matiere.getCoefficient());
            ps.setString(3, matiere.getEnseignant());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    matiere.setId(keys.getInt(1));
                }
            }
            return matiere;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la matiere", e);
        }
    }

    @Override
    public Optional<Matiere> trouverParId(int id) {
        String sql = "SELECT * FROM matiere WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la matiere #" + id, e);
        }
    }

    @Override
    public List<Matiere> listerTous() {
        String sql = "SELECT * FROM matiere ORDER BY nom";
        List<Matiere> resultats = new ArrayList<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapper(rs));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du listing des matieres", e);
        }
    }

    @Override
    public List<Matiere> rechercher(String motCle) {
        String sql = "SELECT * FROM matiere WHERE nom LIKE ? OR enseignant LIKE ? ORDER BY nom";
        String pattern = "%" + motCle + "%";
        List<Matiere> resultats = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapper(rs));
                }
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de matieres", e);
        }
    }

    @Override
    public boolean mettreAJour(Matiere matiere) {
        String sql = "UPDATE matiere SET nom = ?, coefficient = ?, enseignant = ? WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, matiere.getNom());
            ps.setDouble(2, matiere.getCoefficient());
            ps.setString(3, matiere.getEnseignant());
            ps.setInt(4, matiere.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour de la matiere #" + matiere.getId(), e);
        }
    }

    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM matiere WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la matiere #" + id, e);
        }
    }

    private Matiere mapper(ResultSet rs) throws SQLException {
        Matiere m = new Matiere();
        m.setId(rs.getInt("id"));
        m.setNom(rs.getString("nom"));
        m.setCoefficient(rs.getDouble("coefficient"));
        m.setEnseignant(rs.getString("enseignant"));
        return m;
    }
}