package sqlbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Ordre {
    String nomColomne ;
    String ordre ;
    String definition;
        public static Ordre [] getAll(Connection c ) throws Exception{
        ResultSet re= c.createStatement().executeQuery("select * from Ordre") ;
        ArrayList<Ordre> list = new ArrayList<>();
        while (re.next()) {
            Ordre f = new Ordre() ;
            f.setNomColomne(re.getString("nomcolomne"));
            f.setOrdre(re.getString("ordre"));            
            f.setDefinition(re.getString("defintion"));
            list.add(f);
        }
        return list.toArray(new Ordre[list.size()]);
    }


    public String getNomColomne() {
        return nomColomne;
    }
    public void setNomColomne(String nomColomne) {
        this.nomColomne = nomColomne;
    }
    public String getOrdre() {
        return ordre;
    }
    public void setOrdre(String ordre) {
        this.ordre = ordre;
    }


    public String getDefinition() {
        return definition;
    }


    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
