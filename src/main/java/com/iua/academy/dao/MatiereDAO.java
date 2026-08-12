package com.iua.academy.dao;

import com.iua.academy.model.Matiere;

import java.util.List;
import java.util.Optional;

public interface MatiereDAO {

    Matiere creer(Matiere matiere);

    Optional<Matiere> trouverParId(int id);

    List<Matiere> listerTous();

    List<Matiere> rechercher(String motCle);

    boolean mettreAJour(Matiere matiere);

    boolean supprimer(int id);
}