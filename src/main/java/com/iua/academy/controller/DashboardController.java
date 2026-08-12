package com.iua.academy.controller;

import com.iua.academy.dao.EtudiantDAO;
import com.iua.academy.dao.EtudiantDaoSqlite;
import com.iua.academy.dao.MatiereDAO;
import com.iua.academy.dao.MatiereDaoSqlite;
import com.iua.academy.dao.NoteDAO;
import com.iua.academy.dao.NoteDaoSqlite;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label lblNbEtudiants;
    @FXML
    private Label lblNbMatieres;
    @FXML
    private Label lblNbNotes;

    private final EtudiantDAO etudiantDAO = new EtudiantDaoSqlite();
    private final MatiereDAO matiereDAO = new MatiereDaoSqlite();
    private final NoteDAO noteDAO = new NoteDaoSqlite();

    @FXML
    public void initialize() {
        lblNbEtudiants.setText(String.valueOf(etudiantDAO.listerTous().size()));
        lblNbMatieres.setText(String.valueOf(matiereDAO.listerTous().size()));
        lblNbNotes.setText(String.valueOf(noteDAO.listerToutesDetaillees().size()));
    }
}