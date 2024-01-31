package main;

import java.sql.Connection;
import java.sql.DriverManager;

import obj.Produits;

public class App {
    public static void main(String[] args) throws Exception {
        Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/produit","postgres","post");

        
        System.out.println(new Produits().formSQL(c, false,"meilleur prix boisson ", "client"));
    }
}
