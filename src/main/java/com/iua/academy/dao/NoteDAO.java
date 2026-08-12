package com.iua.academy.dao;

import com.iua.academy.model.Note;
import com.iua.academy.model.NoteDetaillee;

import java.util.List;

public interface NoteDAO {

    Note creer(Note note);

    List<NoteDetaillee> listerToutesDetaillees();

    List<NoteDetaillee> rechercherDetaillees(String motCle);

    List<NoteDetaillee> listerParEtudiant(int etudiantId);

    boolean mettreAJour(Note note);

    boolean supprimer(int id);

    /** Moyenne generale ponderee par coefficient pour un etudiant donne (null si aucune note). */
    Double calculerMoyenneEtudiant(int etudiantId);
}