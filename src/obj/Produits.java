package obj;

import orm.ORM;

public class Produits  extends ORM<Produits>{

    String nom;
    Double prix;
    String categorie;
    Double qualite;


    
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public Double getPrix() {
        return prix;
    }
    public void setPrix(Double prix) {
        this.prix = prix;
    }
    public String getCategorie() {
        return categorie;
    }
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    public Double getQualite() {
        return qualite;
    }
    public void setQualite(Double qualite) {
        this.qualite = qualite;
    }

    
}
