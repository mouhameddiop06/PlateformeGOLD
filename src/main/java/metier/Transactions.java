package metier;

import java.time.LocalDate;
import java.time.LocalTime;

public class Transactions {
    private int id;
    private double montant;
    private LocalDate date;
    private String statut;
    private LocalTime heure;
    private String type;
    private String telephoneSource;
    private String telephoneDestinataire;
    private int compteSource; // ID du compte source
    private int compteDestinataire; // ID du compte destinataire

    // Constructeur
    public Transactions(int id, double montant, LocalDate date, String statut, LocalTime heure, String type, String telephoneSource, String telephoneDestinataire) {
        this.id = id;
        this.montant = montant;
        this.date = date;
        this.statut = statut;
        this.heure = heure;
        this.type = type;
        this.telephoneSource = telephoneSource;
        this.telephoneDestinataire = telephoneDestinataire;
        this.telephoneSource = telephoneSource;
        this.telephoneDestinataire = telephoneDestinataire;
        
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTelephoneSource() {
        return telephoneSource;
    }

    public void setTelephoneSource(String telephoneSource) {
        this.telephoneSource = telephoneSource;
    }

    public String getTelephoneDestinataire() {
        return telephoneDestinataire;
    }

    public void setTelephoneDestinataire(String telephoneDestinataire) {
        this.telephoneDestinataire = telephoneDestinataire;
    }
    public int getCompteSource() {
        return compteSource;
    }

    public void setCompteSource(int compteSource) {
        this.compteSource = compteSource;
    }

    public int getCompteDestinataire() {
        return compteDestinataire;
    }

    public void setCompteDestinataire(int compteDestinataire) {
        this.compteDestinataire = compteDestinataire;
    }
    // Méthode toString() pour l'affichage facile des objets Transactions
    @Override
    public String toString() {
        return "Transactions{" +
                "id=" + id +
                ", montant=" + montant +
                ", date=" + date +
                ", statut='" + statut + '\'' +
                ", heure=" + heure +
                ", type='" + type + '\'' +
                ", telephoneSource='" + telephoneSource + '\'' +
                ", telephoneDestinataire='" + telephoneDestinataire + '\'' +
                 ", compteSource=" + compteSource +
                ", compteDestinataire=" + compteDestinataire +
                '}';
    }
}