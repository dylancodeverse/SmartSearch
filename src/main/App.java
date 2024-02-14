package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import obj.NewProduit;
import obj.Produits;
import sqlbuilder.SqlBuilder;

public class App {
    public static void main(String[] args) throws Exception {
        Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/recherche","postgres","post");
        // System.out.println( Produits.getHTMLFromMarge("meilleur rapport qualite prix  coca "));

        // "fwdf".split(" ");
        // System.out.println( new SqlBuilder(c, " prix qualite meilleur boisson",new Produits()).getRequest());

        // System.out.println( new SqlBuilder(c, " prix qualite meilleur boisson",new Produits()).getRequest());

        // System.out.println(new SqlBuilder(c, " somme prix par categorie ,qualite ",new Produits()).getRequest());

        // System.out.println(new SqlBuilder(c, "meilleur somme prix par categorie",new Produits()).getRequest());
        // System.out.println(new SqlBuilder(c, " somme prix par categorie meilleur",new Produits()).getRequest());

        // System.out.println(new SqlBuilder(c, " prix entre 45 et 6784 meilleur",new Produits()).getRequest());

        // System.out.println(new SqlBuilder(c, "prix inferieur 5", new Produits()).getRequest());

        NewProduit.getHTML("meilleur prix");


    }
}
