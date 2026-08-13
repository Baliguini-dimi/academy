package com.iua.academy.dao;

import com.iua.academy.model.Etudiant;

import java.util.List;
import java.util.Optional;

/**
 * Contrat d'acces aux donnees pour l'entite Etudiant.
 * L'interface permet de changer d'implementation (SQLite aujourd'hui,
 * MySQL demain par exemple) sans impacter les controllers.
 */
public interface EtudiantDAO {

    Etudiant creer(Etudiant etudiant);

    Optional<Etudiant> trouverParId(int id);

    List<Etudiant> listerTous();

    List<Etudiant> rechercher(String motCle);

    boolean mettreAJour(Etudiant etudiant);

    boolean supprimer(int id);

    boolean matriculeExiste(String matricule, Integer idAExclure);

    List<Etudiant> listerParClasse(String classe);

    List<String> listerClassesDistinctes();
}
