package metier;

import java.util.Date;
import java.sql.Time;

public class Transaction {
    private int id;
    private float montant;
    private Date date;
    private Time heure;
    private String statut;
    private String telephoneSource;
    private String telephoneDestinataire;
    private String typeTransaction;  // Remplacement de nomoption par typeTransaction

    // Constructeur par défaut
    public Transaction() {}

    // Constructeur avec tous les champs
    public Transaction(int id, float montant, Date date, Time heure, String statut, 
                       String telephoneSource, String telephoneDestinataire, String typeTransaction) {
        this.id = id;
        this.montant = montant;
        this.date = date;
        this.heure = heure;
        this.statut = statut;
        this.telephoneSource = telephoneSource;
        this.telephoneDestinataire = telephoneDestinataire;
        this.typeTransaction = typeTransaction;  // Remplacement de nomoption par typeTransaction
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public float getMontant() { return montant; }
    public void setMontant(float montant) { this.montant = montant; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Time getHeure() { return heure; }
    public void setHeure(Time heure) { this.heure = heure; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getTelephoneSource() { return telephoneSource; }
    public void setTelephoneSource(String telephoneSource) { this.telephoneSource = telephoneSource; }

    public String getTelephoneDestinataire() { return telephoneDestinataire; }
    public void setTelephoneDestinataire(String telephoneDestinataire) { this.telephoneDestinataire = telephoneDestinataire; }

    public String getTypeTransaction() { return typeTransaction; }  // Getter pour typeTransaction
    public void setTypeTransaction(String typeTransaction) { this.typeTransaction = typeTransaction; }  // Setter pour typeTransaction
}
