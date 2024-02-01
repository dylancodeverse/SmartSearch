package obj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sqlbuilder.SqlBuilder;

public class NewProduit {
    public static String getHTML(String phrase) throws Exception{
        Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/recherche","postgres","post");
        String sqlQuery = (new SqlBuilder(c, phrase , new Produits()).getRequest());

        // Exécuter la requête SQL
        try (PreparedStatement statement = c.prepareStatement(sqlQuery)) {
            ResultSet resultSet = statement.executeQuery();

            // Créer la page HTML à partir du résultat de la requête
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<div class=\"table-responsive\">");
            htmlTable.append("<table class=\"table table-hover\">");
            htmlTable.append("<thead>");
            htmlTable.append("<tr>");

            // Obtenez les noms de colonnes à partir des métadonnées du résultat
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                htmlTable.append("<th>").append(resultSet.getMetaData().getColumnName(i)).append("</th>");
            }

            htmlTable.append("</tr>");
            htmlTable.append("</thead>");
            htmlTable.append("<tbody>");

            // Parcourir les lignes du résultat
            while (resultSet.next()) {
                htmlTable.append("<tr>");

                // Ajouter les valeurs de chaque colonne à la ligne HTML
                for (int i = 1; i <= columnCount; i++) {
                    htmlTable.append("<td>").append(resultSet.getString(i)).append("</td>");
                }

                htmlTable.append("</tr>");
            }

            htmlTable.append("</tbody>");
            htmlTable.append("</table>");
            htmlTable.append("</div>");

            // Maintenant, htmlTable contient le code HTML de la table résultante

            return htmlTable.toString();


        } catch (Exception e) {
            // e.printStackTrace();
            return "Pas de resultat";
        }
    }    
}
