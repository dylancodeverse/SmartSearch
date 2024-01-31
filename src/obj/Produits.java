package obj;

import java.sql.Connection;
import java.sql.DriverManager;

import orm.ORM;

public class Produits  extends ORM<Produits>{

    String nom;
    Double prix;
    String categorie;
    Integer qualite;


    public static Produits[] select(String humanRequest, String context) throws Exception{
        Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/produit","postgres","post");
        return new Produits().selectWithRequest(c, false,humanRequest, context) ;
    }


    public static String getHTMLFromMarge(String humanRequest) throws Exception{
        Produits[] allPus = Produits.select(humanRequest ,"client");

        // Génération du tableau HTML
        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<div class=\"table-responsive\">");
        htmlTable.append("<table class=\"table table-hover\">");
        htmlTable.append("<thead>");
        htmlTable.append("<tr>");
        htmlTable.append("<th>Nom</th>");
        htmlTable.append("<th>Prix</th>");
        htmlTable.append("<th>Qualite</th>");
        htmlTable.append("<th>Categorie</th>");
        htmlTable.append("</tr>");
        htmlTable.append("</thead>");
        htmlTable.append("<tbody>");

        for (Produits allPu : allPus) {
            htmlTable.append("<tr>");
            htmlTable.append("<td>").append(allPu.getNom()).append("</td>");
            htmlTable.append("<td>").append(allPu.getPrix()).append("</td>");
            htmlTable.append("<td>").append(allPu.getQualite()).append("</td>");
            htmlTable.append("<td>").append(allPu.getCategorie()).append("</td>");
            htmlTable.append("</tr>");
        }

        htmlTable.append("</tbody>");
        htmlTable.append("</table>");
        htmlTable.append("</div>");

        // Affichage du tableau HTML généré
        return (htmlTable.toString());
    }

    


    
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
    public Integer getQualite() {
        return qualite;
    }
    public void setQualite(Integer qualite) {
        this.qualite = qualite;
    }

    
}
