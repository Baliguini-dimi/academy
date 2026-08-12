package com.iua.academy.model;

/**
 * Represente une matiere enseignee, avec son coefficient.
 */
public class Matiere {

    private int id;
    private String nom;
    private double coefficient;
    private String enseignant;

    public Matiere() {
    }

    public Matiere(String nom, double coefficient, String enseignant) {
        this.nom = nom;
        this.coefficient = coefficient;
        this.enseignant = enseignant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(String enseignant) {
        this.enseignant = enseignant;
    }

    @Override
    public String toString() {
        return nom;
    }
}
