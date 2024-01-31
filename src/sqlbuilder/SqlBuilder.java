package sqlbuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlBuilder {
    private String request ;
    Boolean isAggregationPresent ;
    FonctionAggregation [] fonctionAggregation ;
    Field[] objFields ;
    String[] objFieldsName ;
    String [] splitedHumanRequest ;
    WhereRegex[] whereRegexs ;
    ArrayList<String> colomneForGroupBy ;

    /**
     * 1 ere etape : regarder si colomne : * 
     * , ou bien des colomnes specifiques avec des fonctions d'aggregation
     * ---------------
     * 
     * @param connection
     * @param humanRequest
     * @throws Exception 
     */
    public SqlBuilder(Connection connection , String humanRequest , Object obj) throws Exception{
        // manadio anle phrase:
        humanRequest = format(humanRequest).toLowerCase();

        init(connection,obj,humanRequest);
        // mamantatra hoe inona avy no colomne ilaina
        setColumns(humanRequest);        
        // ampiana anle nom de table
        setTable(obj) ;
        // 
        setWhere(humanRequest);




    }



    private void setWhere(String humanRequest) {
        String where = "WHERE " ;
        // mijery hoe misy mots cles ana where ve:
        for (int i = 0; i < whereRegexs.length; i++) {

            Pattern pattern = Pattern.compile(whereRegexs[i].getRegex());
            Matcher matcher = pattern.matcher(humanRequest);
            if (matcher.find()) {
                
            }


        }
        // mijery hoe misy valeur ana colomne ve

        

    }






    private void init(Connection connection, Object obj, String humanRequest) throws Exception  {
        fonctionAggregation = FonctionAggregation.getAll(connection);
        objFields = obj.getClass().getDeclaredFields() ;
        objFieldsName = new String[objFields.length];
        for (int i = 0; i < objFieldsName.length; i++) {
            objFieldsName[i] = objFields[i].getName().toLowerCase() ;
        }

        splitedHumanRequest = humanRequest.split(" ");

        whereRegexs = WhereRegex.getAll(connection) ;


    }



    private void setTable(Object obj) {
        request = request+" FROM "+obj.getClass().getSimpleName() ;
    }



    private void setColumns(String humanRequest) throws Exception {
        // mi set anle boolean temporaire
        isAggregationPresent = isFonctionAggregationPresent(humanRequest) ;
        if (isAggregationPresent) {
            // CAS ANAKIROA : SOIT TSY MAINTSY MIATOMBOKA @ FONCTION D'AGGREGATION DIA MIAFARA @ NOM DE COLOMNE

            // SOIT MIATOMBOKA @ NOM DIA MIAFARA @ FONCTION D'AGGREGATION

            // -> TETEZINA ILAY HUMANREQUEST : ANGONONA INDRAY MIARAKA DAHOLO NA AGGREGATION IO NA NOM DE COLOMNE

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < splitedHumanRequest.length; i++) {
                // jerena hoe nom de colomne ve sa fonction d'aggregation:
                for (int j = 0; j < objFieldsName.length; j++) {
                    if (objFieldsName[j].equals(splitedHumanRequest[i])) {
                        str.append(splitedHumanRequest[i]+" ");
                        break ;
                    }                    
                }
                for (int j = 0; j < fonctionAggregation.length; j++) {
                    if (fonctionAggregation[j].getMots().equals(splitedHumanRequest[i])) {
                        str.append(splitedHumanRequest[i]+" ");
                        break ;
                    }
                }
                if (str.length()!=0) {
                    if (str.charAt(str.length()-1)!='%' ) {
                        str.append("%"); // misy separateur manelanelana anzareo 
                                        // manova ny signification an ilay phrase vao misy separateur
                    }
                    // tsy mi compte ny surplus de separateur
                }
            }
            // tokony mi former an'ilay request
            String [] splitedStr = str.toString().split("%");
            request = "SELECT ";
            for (int i = 0; i < splitedStr.length; i++) {
                request = request + formColomneAvecAggregation(splitedStr[i])+",";
            }
            request = request.substring(0,request.lastIndexOf(","));            
        }
        else{
            request = "SELECT * ";
        }
 

    }

    private String formColomneAvecAggregation(String toreplace) {
        String[] splited = toreplace.split(" ");        
        if(splited.length==1){
            setColumnsGroupBy(toreplace);
            return toreplace ;
        }else{
            String aggregation =  getAggregation(splited[0]);
            if (aggregation==null) {
                aggregation = getAggregation(splited[1]);           
                return aggregation.replace("%", splited[0]);

            }else{
                return aggregation.replace("%",splited[1]);
            }
        }
    }






    private String getAggregation(String string) {
        for (int i = 0; i < fonctionAggregation.length; i++) {
            if (fonctionAggregation[i].getMots().equals(string)) {
                return fonctionAggregation[i].getOperation() ;
            }            
        }
        return null ;
    }



    private boolean isFonctionAggregationPresent( String humanRequest) throws Exception {
        for (int i = 0; i < fonctionAggregation.length; i++) {
            if (humanRequest.contains(fonctionAggregation[i].getMots()) ) {
                return true ;
            }   
        }
        return false ;
    }


    /**
     * Manala ny espace mihoatra
     * @param humanRequest
     * @return
     */
    private String format(String humanRequest) {
        humanRequest = humanRequest.trim();
        humanRequest = humanRequest.stripTrailing();
        return humanRequest ;
    }
    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setColumnsGroupBy(String str){
        try {
            colomneForGroupBy.add(str);
        } catch (Exception e) {
            colomneForGroupBy = new ArrayList<>();
            colomneForGroupBy.add(str);
        }
    }

}
