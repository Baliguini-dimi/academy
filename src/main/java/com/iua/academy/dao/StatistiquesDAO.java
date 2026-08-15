package com.iua.academy.dao;

import java.util.LinkedHashMap;

public interface StatistiquesDAO {

    /** Nombre d'etudiants par classe, trie par nom de classe. */
    LinkedHashMap<String, Integer> etudiantsParClasse();

    /** Moyenne (ponderee par les notes) de chaque matiere, triee par nom de matiere. */
    LinkedHashMap<String, Double> moyenneParMatiere();

    /** Repartition des notes individuelles par tranche de mention. */
    LinkedHashMap<String, Integer> repartitionMentions();

    /** Nombre de notes saisies par semaine, sur les N dernieres semaines (ordre chronologique). */
    LinkedHashMap<String, Integer> notesParSemaine(int nombreSemaines);

    /** Moyenne generale ponderee par coefficient, tous etudiants et matieres confondus. */
    Double moyenneGenerale();

    /** Combine les inscriptions d'etudiants et les notes recentes, triees par date decroissante. */
    java.util.List<com.iua.academy.model.ActiviteRecente> activiteRecente(int limite);

    /** Moyenne des notes par semaine (evolution), sur les N dernieres semaines, ordre chronologique. */
    java.util.LinkedHashMap<String, Double> moyenneParSemaine(int nombreSemaines);

    /** Pourcentage de notes superieures ou egales au seuil donne (null si aucune note). */
    Double tauxReussite(double seuil);

    /** Nombre de notes saisies au cours du mois calendaire courant. */
    int notesSaisiesCeMois();
}
