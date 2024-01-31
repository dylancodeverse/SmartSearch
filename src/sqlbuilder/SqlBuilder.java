package sqlbuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class SqlBuilder {
    private String request ;
    Boolean isAggregationPresent ;
    FonctionAggregation [] fonctionAggregation ;
    Field[] objFields ;
    String[] objFieldsName ;
    String [] splitedHumanRequest ;


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
        setColumns(connection ,humanRequest);        
        // ampiana anle nom de table
        setTable(obj) ;
        // 


    }



    private void init(Connection connection, Object obj, String humanRequest) throws Exception  {
        fonctionAggregation = FonctionAggregation.getAll(connection);
        objFields = obj.getClass().getDeclaredFields() ;
        objFieldsName = new String[objFields.length];
        for (int i = 0; i < objFieldsName.length; i++) {
            objFieldsName[i] = objFields[i].getName().toLowerCase() ;
        }
        splitedHumanRequest = humanRequest.split(" ");

    }



    private void setTable(Object obj) {
        request = request+"FROM "+obj.getClass().getSimpleName() ;
    }



    private void setColumns(Connection connection, String humanRequest) throws Exception {
        // mi set anle boolean temporaire
        isAggregationPresent = isFonctionAggregationPresent(connection,humanRequest) ;
        if (isAggregationPresent) {
            // TODO : manamboatra anlay nom de colomnes

            // CAS ANAKIROA : SOIT TSY MAINTSY MIATOMBOKA @ FONCTION D'AGGREGATION DIA MIAFARA @ NOM DE COLOMNE

            // SOIT MIATOMBOKA @ NOM DIA MIAFARA @ FONCTION D'AGGREGATION

            // -> TETEZINA ILAY HUMANREQUEST : ANGONONA INDRAY MIARAKA DAHOLO NA AGGREGATION IO NA NOM DE COLOMNE

            ArrayList<String> str = new ArrayList<>() ;
            for (int i = 0; i < splitedHumanRequest.length; i++) {
                // jerena hoe nom de colomne ve sa fonction d'aggregation:
                for (int j = 0; j < objFieldsName.length; j++) {
                    if (objFieldsName[j].equals(splitedHumanRequest[i])) {
                        str.add(splitedHumanRequest[i]);
                        break ;
                    }                    
                }
                for (int j = 0; j < fonctionAggregation.length; j++) {
                    if (fonctionAggregation[j].getMots().equals(splitedHumanRequest[i])) {
                        str.add(splitedHumanRequest[i]);
                        break ;
                    }
                }
            }

        }
        else{
            request = "SELECT * ";
        }
 

    }

    private boolean isFonctionAggregationPresent(Connection connection, String humanRequest) throws Exception {
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

}
