package com.osmi.dev.duplicatefinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DuplicateMatch {
    private String contactIdOrigen;
    private String contactIdCoincidencia;
    private String precision;
    private int score;

    // Getters and Setters

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getContactIdCoincidencia() {
        return contactIdCoincidencia;
    }

    public void setContactIdCoincidencia(String contactIdCoincidencia) {
        this.contactIdCoincidencia = contactIdCoincidencia;
    }

    public String getContactIdOrigen() {
        return contactIdOrigen;
    }

    public void setContactIdOrigen(String contactIdOrigen) {
        this.contactIdOrigen = contactIdOrigen;
    }


}
