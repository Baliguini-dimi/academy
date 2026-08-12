package com.iua.academy.model;

import java.time.LocalDate;

/**
 * Vue enrichie d'une Note pour l'affichage : porte le nom de l'etudiant
 * et de la matiere (via jointure SQL) plutot que leurs seuls IDs.
 */
public class NoteDetaillee {

    private int id;
    private int etudiantId;
    private int matiereId;
    private String etudiantNomComplet;
    private String matiereNom;
    private double valeur;
    private double coefficient;
    private String typeEvaluation;
    private LocalDate dateEvaluation;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEtudiantId() { return etudiantId; }
    public void setEtudiantId(int etudiantId) { this.etudiantId = etudiantId; }

    public int getMatiereId() { return matiereId; }
    public void setMatiereId(int matiereId) { this.matiereId = matiereId; }

    public String getEtudiantNomComplet() { return etudiantNomComplet; }
    public void setEtudiantNomComplet(String etudiantNomComplet) { this.etudiantNomComplet = etudiantNomComplet; }

    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }

    public double getValeur() { return valeur; }
    public void setValeur(double valeur) { this.valeur = valeur; }

    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }

    public String getTypeEvaluation() { return typeEvaluation; }
    public void setTypeEvaluation(String typeEvaluation) { this.typeEvaluation = typeEvaluation; }

    public LocalDate getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(LocalDate dateEvaluation) { this.dateEvaluation = dateEvaluation; }
}