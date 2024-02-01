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
            x.setRegex(re.getString("regex"));                 
            x.setOperation(re.getString("operation"));
            x.nMatcher= re.getInt("nmatcher");
            x.type = re.getString("type");
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
            int a = 0 ;
            for (int j = 0; j < nMatcher; j++) {
                for (int j2 = a; j2 < splitedHumanRequest.length; j2++) {
                    try{
                        x[j] = Integer.parseInt(copiedArray[j2]);
                        end = end.replace("INT"+j,x[j]+"" );
                        a++;
                        break;

                    }catch(Exception e){
                    }
                        
                }
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
