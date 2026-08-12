package com.iua.academy.model;

/**
 * Represente un parametre applicatif sous forme cle/valeur
 * (nom de l'etablissement, annee scolaire, seuil de validation, etc.).
 */
public class Parametre {

    private String cle;
    private String valeur;

    public Parametre() {
    }

    public Parametre(String cle, String valeur) {
        this.cle = cle;
        this.valeur = valeur;
    }

    public String getCle() {
        return cle;
    }

    public void setCle(String cle) {
        this.cle = cle;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}