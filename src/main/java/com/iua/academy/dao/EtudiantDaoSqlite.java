package com.iua.academy.dao;

import com.iua.academy.model.Etudiant;
import com.iua.academy.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation SQLite de EtudiantDAO.
 * Toutes les requetes utilisent des PreparedStatement (protection anti-injection SQL).
 */
public class EtudiantDaoSqlite implements EtudiantDAO {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    @Override
    public Etudiant creer(Etudiant etudiant) {
        String sql = """
            INSERT INTO etudiant (matricule, nom, prenom, date_naissance, classe, email)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setString(4, etudiant.getDateNaissance() != null ? etudiant.getDateNaissance().toString() : null);
            ps.setString(5, etudiant.getClasse());
            ps.setString(6, etudiant.getEmail());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    etudiant.setId(keys.getInt(1));
                }
            }
            return etudiant;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de l'etudiant", e);
        }
    }

    @Override
    public Optional<Etudiant> trouverParId(int id) {
        String sql = "SELECT * FROM etudiant WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'etudiant #" + id, e);
        }
    }

    @Override
    public List<Etudiant> listerTous() {
        String sql = "SELECT * FROM etudiant ORDER BY nom, prenom";
        List<Etudiant> resultats = new ArrayList<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapper(rs));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du listing des etudiants", e);
        }
    }

    @Override
    public List<Etudiant> rechercher(String motCle) {
        String sql = """
            SELECT * FROM etudiant
            WHERE nom LIKE ? OR prenom LIKE ? OR matricule LIKE ?
            ORDER BY nom, prenom
            """;
        String pattern = "%" + motCle + "%";
        List<Etudiant> resultats = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapper(rs));
                }
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'etudiants", e);
        }
    }

    @Override
    public boolean mettreAJour(Etudiant etudiant) {
        String sql = """
            UPDATE etudiant
            SET matricule = ?, nom = ?, prenom = ?, date_naissance = ?, classe = ?, email = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, etudiant.getMatricule());
            ps.setString(2, etudiant.getNom());
            ps.setString(3, etudiant.getPrenom());
            ps.setString(4, etudiant.getDateNaissance() != null ? etudiant.getDateNaissance().toString() : null);
            ps.setString(5, etudiant.getClasse());
            ps.setString(6, etudiant.getEmail());
            ps.setInt(7, etudiant.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour de l'etudiant #" + etudiant.getId(), e);
        }
    }

    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM etudiant WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'etudiant #" + id, e);
        }
    }

    private Etudiant mapper(ResultSet rs) throws SQLException {
        Etudiant e = new Etudiant();
        e.setId(rs.getInt("id"));
        e.setMatricule(rs.getString("matricule"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        String dateNaissance = rs.getString("date_naissance");
        if (dateNaissance != null) {
            e.setDateNaissance(LocalDate.parse(dateNaissance));
        }
        e.setClasse(rs.getString("classe"));
        e.setEmail(rs.getString("email"));
        return e;
    }

    @Override
    public boolean matriculeExiste(String matricule, Integer idAExclure) {
        String sql = idAExclure == null
            ? "SELECT COUNT(*) FROM etudiant WHERE matricule = ?"
            : "SELECT COUNT(*) FROM etudiant WHERE matricule = ? AND id != ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, matricule);
            if (idAExclure != null) {
                ps.setInt(2, idAExclure);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la verification du matricule", e);
        }
    }
}
