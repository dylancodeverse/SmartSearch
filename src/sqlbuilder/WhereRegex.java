package sqlbuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;

public class WhereRegex {
    String regex ;
    String operation;
    Integer nMatcher ;
    String type;

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

    public String getFirstWord(){
        return regex.split(" ")[0];
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

    public String getCompleteOperation(int i, String[] splitedHumanRequest , String column) {
        String end  =operation ;
        String[] copiedArray = Arrays.copyOfRange(splitedHumanRequest, i, splitedHumanRequest.length);

        if (type.equals("INT")) {
            Integer[] x = new Integer[nMatcher]; 
            for (int j = 0; j < nMatcher; j++) {
                x[j] = Integer.parseInt(copiedArray[j]);
                end = end.replace("INT",x[j]+"" );
            }
            end = end.replaceAll("column", column);

            return end ;

        }else{
            for (int j = 0; j < nMatcher; j++) {
                end = end.replace("STR",copiedArray[j] );
            }
            end = end.replaceAll("column", column);

            return end ;
        }
    }
}
