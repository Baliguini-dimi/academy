package com.iua.academy.model;

import java.time.LocalDate;

/**
 * Represente une note obtenue par un etudiant dans une matiere.
 * Fait le lien (table d'association) entre Etudiant et Matiere.
 */
public class Note {

    private int id;
    private int etudiantId;
    private int matiereId;
    private double valeur;
    private String typeEvaluation;
    private LocalDate dateEvaluation;

    public Note() {
    }

    public Note(int etudiantId, int matiereId, double valeur, String typeEvaluation, LocalDate dateEvaluation) {
        this.etudiantId = etudiantId;
        this.matiereId = matiereId;
        this.valeur = valeur;
        this.typeEvaluation = typeEvaluation;
        this.dateEvaluation = dateEvaluation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(int etudiantId) {
        this.etudiantId = etudiantId;
    }

    public int getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(int matiereId) {
        this.matiereId = matiereId;
    }

    public double getValeur() {
        return valeur;
    }

    public void setValeur(double valeur) {
        if (valeur < 0 || valeur > 20) {
            throw new IllegalArgumentException("La note doit etre comprise entre 0 et 20");
        }
        this.valeur = valeur;
    }

    public String getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(String typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public LocalDate getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDate dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
}
