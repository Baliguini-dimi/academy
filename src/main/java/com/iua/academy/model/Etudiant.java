package com.iua.academy.model;

import java.time.LocalDate;

/**
 * Represente un etudiant inscrit dans l'application Academy.
 */
public class Etudiant {

    private int id;
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String classe;
    private String email;

    public Etudiant() {
    }

    public Etudiant(String matricule, String nom, String prenom, LocalDate dateNaissance, String classe, String email) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.classe = classe;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    // Champ calcule, non persiste en base : rempli a l'affichage a partir des notes
    private Double moyenne;

    public Double getMoyenne() {
        return moyenne;
    }

    public void setMoyenne(Double moyenne) {
        this.moyenne = moyenne;
    }

    @Override
    public String toString() {
        return getNomComplet();
    }
}
