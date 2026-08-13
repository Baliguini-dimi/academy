package com.iua.academy.model;

import java.time.LocalDate;

/**
 * Represente une entree de l'activite recente affichee sur le dashboard,
 * construite a partir de donnees reelles (etudiants inscrits, notes saisies).
 */
public class ActiviteRecente {

    private String type;
    private String description;
    private LocalDate date;

    public ActiviteRecente(String type, String description, LocalDate date) {
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}