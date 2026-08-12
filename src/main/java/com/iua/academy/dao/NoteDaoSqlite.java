package com.iua.academy.dao;

import com.iua.academy.model.Note;
import com.iua.academy.model.NoteDetaillee;
import com.iua.academy.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NoteDaoSqlite implements NoteDAO {

    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    private static final String SELECT_DETAILLE = """
        SELECT n.id, n.etudiant_id, n.matiere_id, n.valeur, n.type_evaluation, n.date_evaluation,
               e.nom AS etudiant_nom, e.prenom AS etudiant_prenom,
               m.nom AS matiere_nom, m.coefficient AS matiere_coefficient
        FROM note n
        JOIN etudiant e ON e.id = n.etudiant_id
        JOIN matiere m ON m.id = n.matiere_id
        """;

    @Override
    public Note creer(Note note) {
        String sql = """
            INSERT INTO note (etudiant_id, matiere_id, valeur, type_evaluation, date_evaluation)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, note.getEtudiantId());
            ps.setInt(2, note.getMatiereId());
            ps.setDouble(3, note.getValeur());
            ps.setString(4, note.getTypeEvaluation());
            ps.setString(5, note.getDateEvaluation().toString());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    note.setId(keys.getInt(1));
                }
            }
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la creation de la note", e);
        }
    }

    @Override
    public List<NoteDetaillee> listerToutesDetaillees() {
        String sql = SELECT_DETAILLE + " ORDER BY n.date_evaluation DESC";
        List<NoteDetaillee> resultats = new ArrayList<>();
        try (Statement st = getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                resultats.add(mapper(rs));
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du listing des notes", e);
        }
    }

    @Override
    public List<NoteDetaillee> rechercherDetaillees(String motCle) {
        String sql = SELECT_DETAILLE + """
             WHERE e.nom LIKE ? OR e.prenom LIKE ? OR m.nom LIKE ?
             ORDER BY n.date_evaluation DESC
            """;
        String pattern = "%" + motCle + "%";
        List<NoteDetaillee> resultats = new ArrayList<>();
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
            throw new RuntimeException("Erreur lors de la recherche de notes", e);
        }
    }

    @Override
    public List<NoteDetaillee> listerParEtudiant(int etudiantId) {
        String sql = SELECT_DETAILLE + " WHERE n.etudiant_id = ? ORDER BY n.date_evaluation DESC";
        List<NoteDetaillee> resultats = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultats.add(mapper(rs));
                }
            }
            return resultats;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du listing des notes de l'etudiant #" + etudiantId, e);
        }
    }

    @Override
    public boolean mettreAJour(Note note) {
        String sql = """
            UPDATE note
            SET etudiant_id = ?, matiere_id = ?, valeur = ?, type_evaluation = ?, date_evaluation = ?
            WHERE id = ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, note.getEtudiantId());
            ps.setInt(2, note.getMatiereId());
            ps.setDouble(3, note.getValeur());
            ps.setString(4, note.getTypeEvaluation());
            ps.setString(5, note.getDateEvaluation().toString());
            ps.setInt(6, note.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise a jour de la note #" + note.getId(), e);
        }
    }

    @Override
    public boolean supprimer(int id) {
        String sql = "DELETE FROM note WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la note #" + id, e);
        }
    }

    @Override
    public Double calculerMoyenneEtudiant(int etudiantId) {
        String sql = """
            SELECT SUM(n.valeur * m.coefficient) AS total_pondere, SUM(m.coefficient) AS total_coefficient
            FROM note n
            JOIN matiere m ON m.id = n.matiere_id
            WHERE n.etudiant_id = ?
            """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double totalCoefficient = rs.getDouble("total_coefficient");
                    if (totalCoefficient == 0) {
                        return null;
                    }
                    return rs.getDouble("total_pondere") / totalCoefficient;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul de la moyenne de l'etudiant #" + etudiantId, e);
        }
    }

    private NoteDetaillee mapper(ResultSet rs) throws SQLException {
        NoteDetaillee n = new NoteDetaillee();
        n.setId(rs.getInt("id"));
        n.setEtudiantId(rs.getInt("etudiant_id"));
        n.setMatiereId(rs.getInt("matiere_id"));
        n.setEtudiantNomComplet(rs.getString("etudiant_prenom") + " " + rs.getString("etudiant_nom"));
        n.setMatiereNom(rs.getString("matiere_nom"));
        n.setCoefficient(rs.getDouble("matiere_coefficient"));
        n.setValeur(rs.getDouble("valeur"));
        n.setTypeEvaluation(rs.getString("type_evaluation"));
        n.setDateEvaluation(LocalDate.parse(rs.getString("date_evaluation")));
        return n;
    }
}