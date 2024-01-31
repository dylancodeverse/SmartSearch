package sqlbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class FonctionAggregation {
    String mots ;
    String operation ;

    public static FonctionAggregation [] getAll(Connection c ) throws Exception{
        ResultSet re= c.createStatement().executeQuery("select * from aggregation") ;
        ArrayList<FonctionAggregation> list = new ArrayList<>();
        while (re.next()) {
            FonctionAggregation f = new FonctionAggregation() ;
            f.setMots(re.getString("mots"));
            f.setOperation(re.getString("operation"));            
            list.add(f);
        }
        return list.toArray(new FonctionAggregation[list.size()]);
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
}
