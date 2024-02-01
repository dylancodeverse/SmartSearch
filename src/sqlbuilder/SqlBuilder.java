package sqlbuilder;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SqlBuilder {
    private String request ;
    Boolean isAggregationPresent ;
    FonctionAggregation [] fonctionAggregation ;
    Field[] objFields ;
    String[] objFieldsName ;
    String [] splitedHumanRequest ;
    WhereRegex[] whereRegexs ;
    ArrayList<String> colomneForGroupBy ;
    ArrayList<String> columnKnown ;
    Sort[] sorts ;

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
        // pour where
        setWhere(connection ,humanRequest);
        // pour group by
        setGroupBy();
        // pour order by
        setOrderBy();


    }



    private void setOrderBy() {
        // jerena hoe anakiray ve sa maromaro ny mots cles ana order by
        Integer n = 0;
        for (int i = 0; i < splitedHumanRequest.length; i++) {
            for (int j = 0; j < sorts.length; j++) {
                if (sorts[j].getMots().contains(splitedHumanRequest[i])) {
                    n++;
                }
            }
            if (n>1) {
                break;
            }
        }
        if (n>1) {
            // mijery an'izy rehetra 
            
        }else{
            // alaina alony ilay mots cles hiasa:
            Sort s = new Sort();
            for (int i = 0; i < splitedHumanRequest.length; i++) {
                for (int j = 0; j < sorts.length; j++) {
                    if (sorts[j].getMots().equals(splitedHumanRequest[i])) s = sorts[j];
                }
            }
            for (int i = 0; i < columnKnown.size(); i++) {
                request = request+" "+s.formSQL(columnKnown);
            }
        }

        // raha anakiray de milamina be satria ampanarahana an'iny daolo ftsn

        // raha maromaro de eo vao mijery ny a droite foana satria zay no manana signification logique
    }



    private void setGroupBy() {
        String g = "group by " ;

        if (colomneForGroupBy!=null) {
            for (int i = 0; i < colomneForGroupBy.size(); i++) {
                g = g+colomneForGroupBy.get(i)+" ,";
            }            
            g = g.substring(0, g.lastIndexOf(" ,"));
            request = request +" "+ g ;
        }

    }



    private void setWhere(Connection  connection ,String humanRequest) throws Exception {
        String where = " WHERE " ;
        // mitady hoe aiza no misy mots cles ana where :
        for (int i = 0; i < splitedHumanRequest.length; i++) {
            String refCol = null ;
            WhereRegex whereRegexsTo =null ;
            for (int j = 0; j < whereRegexs.length; j++) {
                if (splitedHumanRequest[i].equals(whereRegexs[j].getFirstWord())) {
                    whereRegexsTo = whereRegexs[j];
                    // mila alaina ny colomne alohan'iny :
                    try {
                        for (int j2 = i-1; j2 >= 0; j2--) {
                            if (objFieldsName[j].equals(splitedHumanRequest[j2])) {
                                refCol = objFieldsName[j] ;
                                break ;
                            }                            
                        }
                        
                    } catch (Exception e) {
                        throw new Exception("misy tsy milamina");
                    }
                    break ;
                }                
            }
            if (refCol!=null) {
                where = where + whereRegexsTo.getCompleteOperation(i+1 ,splitedHumanRequest,refCol)+" and" ;
            }

        }        
        
        // mijery hoe misy valeur ana colomne ve ?
        
        ArrayList<String[]> ss = getWheres(connection ,humanRequest);
        for (int i = 0; i < ss.size(); i++) {
            where = where +" "+ ss.get(i)[0]+ "="+ss.get(i)[1]+" and"; 
        }
        
        where = where.substring(0, where.lastIndexOf(" and")) ;


        request = request +where ;

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
        sorts = Sort.getAll(connection);

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





    private ArrayList<String[]> getWheres(Connection connection , String humanRequest) throws Exception {
        String [] splited = humanRequest.split(" ") ;
        ArrayList<String[]> ls= new ArrayList<>();
        for (int i = 0; i < splited.length; i++) {
            String column = selectColumnWithValues(connection ,true,splited[i]);
            try {
                Field f = getClass().getDeclaredField(column) ;           
                f.setAccessible(true);
                if (column!=null) {
                    String [] s = new String[2];
                    s[0] = column ;
                    if(f.getClass().isInstance(4))
                        s[1] = splited[i];
                    else s[1] ="'"+splited[i]+"'";
                    ls.add(s);
                }                
            } catch (Exception e) {
                // manaraka ftsn
            }

        }
        return ls;
    }

    private String selectColumnWithValues(Connection connection , boolean isTransactional , String values) throws Exception{
        try{
            String caseWhen = getCaseWhen(values);
            if(caseWhen.isEmpty()) return "" ;
            String request  = "SELECT *, CASE " + caseWhen + " END AS colomne_trouvee from "+getClass().getSimpleName()+getWhereWithFields(values) ;
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            if (res.next()) {
                return res.getString("colomne_trouvee") ;
            }
            return null;
        }
        finally{
            if (!isTransactional) {
                    connection.close();
            }
        }
    }




    private String getWhereWithFields(String values) {
        Field[] fields = objFields ;
        String [] listOfFields = objFieldsName;

        String val = " WHERE" ;
        for (int i = 0; i < listOfFields.length; i++) {
            
            if(fields[i].getType().isInstance(values)) {
                try {
                    Integer x = Integer.parseInt(values) ;
                    val = val + " "+listOfFields[i]+" = "+x+" or ";
                    
                } catch (Exception e) {
                    val = val + " "+listOfFields[i]+" ILIKE '%"+values+"%' or ";
                }
            } 
        }
        val = val.substring(0,val.lastIndexOf(" or"));
        return val ;
    }


    private String getCaseWhen(String values) {
        if (values !=null && !values.isEmpty()) {
            String [] listOfFields = objFieldsName;
            Field[] fields = objFields ;
            String val = "" ;
            for (int i = 0; i < listOfFields.length; i++) {
                if(fields[i].getType().isInstance(values)) {
                    try {
                        Integer x = Integer.parseInt(values) ;
                        val = val + " WHEN "+listOfFields[i]+" = "+x+" then '"+listOfFields[i]+"' ";
                        
                    } catch (Exception e) {
                        val = val + " WHEN "+listOfFields[i]+" ILIKE '%"+values+"%' then '"+listOfFields[i]+"' ";
                    }
                }                 
             }
            return val ;            
        }
        else{
            return "";
        }

    }

}
