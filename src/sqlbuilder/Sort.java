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


    public String formSQL(ArrayList<String> columnKnown) {
        String end ="order by";
        
        for (int i = 0; i < columnKnown.size(); i++) {
            String pref = operation.replace("order by", "");

            end  = end +" "+ pref.replace("%", columnKnown.get(i))+",";
                    
        }
        end=  end .substring(0, end.lastIndexOf(","))  ;

        return end ;

    }


    public String formSQL(String substring) {
        String end ="order by";

        String pref = operation.replace("order by", "");

        end  = end +" "+ pref.replace("%", substring);

        return end; 
    }    
}
