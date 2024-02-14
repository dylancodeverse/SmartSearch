package sqlbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.security.auth.Subject;

public class Sort {
    String mots ;
    String operation ;




    public static Sort [] getAll(Connection c ) throws Exception{
        ResultSet re= c.createStatement().executeQuery("select * from sort") ;
        ArrayList<Sort> list = new ArrayList<>();
        while (re.next()) {
            Sort f = new Sort() ;
            f.setMots(re.getString("mots"));
            f.setOperation(re.getString("operation"));            
            list.add(f);
        }
        return list.toArray(new Sort[list.size()]);
    }


    public String getMots() {
        return mots;
    }
    public void setMots(String mots) {
        this.mots = mots.toLowerCase();
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }


    public String formSQL(ArrayList<String> columnKnown ,Connection connection , String definition) throws Exception {
        String end ="order by";
        Ordre [] ordres = Ordre.getAll(connection);        

        for (int i = 0; i < columnKnown.size(); i++) {
            String pref = operation.replace("order by", "");
            // System.out.println(columnKnown.get(i));
            end  = end +" "+ pref.replace("%", columnKnown.get(i))+",";
            boolean trouvee = false;
            String newOrdrer = "";
            for (int j = 0; j < ordres.length; j++) {
                trouvee= ordres[j].getNomColomne().equalsIgnoreCase(columnKnown.get(i)) && ordres[j].getDefinition().equalsIgnoreCase(definition);

                if (trouvee) {
                    newOrdrer = ordres[j].getOrdre() ;
                    break ;
                }                
            }
            if (trouvee) {
                if (end.contains("asc")) {
                    end = end.replace("asc", newOrdrer);
                }else{
                    end = end.replace("desc", newOrdrer);
                }
            }
                    
        }
        end=  end .substring(0, end.lastIndexOf(","))  ;

        return end ;

    }


    public String formSQL(String substring, Connection connection, String definition) throws Exception {
        String [] x = substring.split(",");
        ArrayList<String> lst = new ArrayList<>();
        for (int i = 0; i < x.length; i++) {
            lst.add(x[i]);
        }
        return formSQL(lst, connection ,definition);

        // String end ="order by";

        // String pref = operation.replace("order by", "");

        // end  = end +" "+ pref.replace("%", substring);

        // return end; 
    }    
}
