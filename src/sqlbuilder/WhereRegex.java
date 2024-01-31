package sqlbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class WhereRegex {
    String regex ;
    String operation;

    public static WhereRegex[] getAll(Connection connection ) throws Exception{
        ResultSet re= connection.createStatement().executeQuery("select * from whereregex") ;
        ArrayList<WhereRegex> wjRegexs = new ArrayList<>();
        while (re.next()) {
            WhereRegex x= new WhereRegex() ;
            x.setRegex(re.getString("mots"));                 
            x.setOperation(re.getString("operation"));
            wjRegexs.add(x);
        }
        return wjRegexs.toArray(new WhereRegex[wjRegexs.size()]);
    }

    public String getRegex() {
        return regex;
    }
    public void setRegex(String regex) {
        this.regex = regex;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
