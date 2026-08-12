package com.iua.academy.dao;

import java.util.Map;

public interface ParametreDAO {

    Map<String, String> listerTous();

    String obtenir(String cle, String valeurParDefaut);

    void enregistrer(String cle, String valeur);
}