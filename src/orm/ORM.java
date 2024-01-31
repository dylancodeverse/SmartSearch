package orm;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * ORM
 */
public class ORM<T> {

    @SuppressWarnings("unchecked")
    public T[] select(Connection connection , boolean isTransactional )throws Exception{
        try{
            String request = "SELECT * FROM "+getClass().getSimpleName();
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            Field [] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T)getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass().getDeclaredMethod("set"+fields[i].getName().substring(0, 1).toUpperCase()+fields[i].getName().substring(1),obj.getClass());
                            m.invoke(t,obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true); 
                            fields[i].set(t,obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:"+fields[i].getName()+" column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        }finally{
            if (!isTransactional) {
                connection.close();
            }
        }

    }


    public String selectColumnWithValues(Connection connection , boolean isTransactional , String values) throws Exception{
        try{
            String request  = "SELECT *, CASE " + getCaseWhen(values) + " END AS colomne_trouvee from "+getClass().getSimpleName()+getWhereWithFields(values) ;
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
//     SELECT
//     id,
//     nom,
//     description,
//     CASE
//         WHEN nom ILIKE '%qualité%' THEN 'Nom'
//         WHEN description ILIKE '%qualité%' THEN 'Description'
//         ELSE 'Non trouvé'
//     END AS colonne_trouvee
// FROM produits
// WHERE nom ILIKE '%qualité%' OR description ILIKE '%qualité%';


    private String getWhereWithFields(String values) {
        String [] listOfFields = getListOfFields();

        String val = " WHERE" ;
        for (int i = 0; i < listOfFields.length; i++) {
            try {
                Integer x = Integer.parseInt(values) ;
                val = val + " "+listOfFields[i]+" = "+x+" or ";
            } catch (Exception e) {
                val = val + " "+listOfFields[i]+" ILIKE '%"+values+" or ";
            }
        }
        val = val.substring(0,val.lastIndexOf(" or"));
        return val ;
    }


    private String getCaseWhen(String values) {
        String [] listOfFields = getListOfFields();
        String val = "" ;
        for (int i = 0; i < listOfFields.length; i++) {
            try {
                Integer x = Integer.parseInt(values) ;
                val = val + " WHEN "+listOfFields[i]+" = "+x+" then '"+listOfFields[i]+"' ";
            } catch (Exception e) {
                val = val + " WHEN "+listOfFields[i]+" ILIKE '%"+values+"'% then '"+listOfFields[i]+"' ";
            }
        }
        return val ;
    }


    private String[] getListOfFields() {
        Field[] fields = getClass().getDeclaredFields() ;
        String [] listStr = new String[fields.length] ;
        for (int i = 0; i < listStr.length; i++) {
            listStr[i] = fields[i].getName() ;
        }
        return listStr ;
    }


    @SuppressWarnings("unchecked")
    public T[] selectWhere(Connection connection , boolean isTransactional , String where)throws Exception{
        try{
            String request = "SELECT * FROM "+getClass().getSimpleName()+ " where "+where;
            System.out.println(request);
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery(request);
            Field [] fields = getClass().getDeclaredFields();
            ArrayList<T> tLists = new ArrayList<T>();
            while (res.next()) {
                T t = ((T)getClass().getConstructor().newInstance());
                for (int i = 0; i < fields.length; i++) {
                    try {
                        Object obj = res.getObject(fields[i].getName());
                        try {
                            Method m = getClass().getDeclaredMethod("set"+fields[i].getName().substring(0, 1).toUpperCase()+fields[i].getName().substring(1),obj.getClass());
                            m.invoke(t,obj);
                        } catch (NoSuchMethodException e) {
                            fields[i].setAccessible(true); 
                            fields[i].set(t,obj);
                        }
                    } catch (Exception e) {
                        // on s'en fout
                        System.out.println(e);
                        System.out.println("WARNING:"+fields[i].getName()+" column does not exist");
                    }
                }
                tLists.add(t);
            }

            T[] resultArray = (T[]) Array.newInstance(getClass(), tLists.size());
            return tLists.toArray(resultArray);
        }finally{
            if (!isTransactional) {
                connection.close();
            }
        }

    }

    public void insert(Connection connection , boolean isTransactional) throws Exception{
        try{
            connection.setAutoCommit(false);
            String request = "insert into "+getClass().getSimpleName(); 
            Field [] fields = getClass().getDeclaredFields();
            String values ="(";
            String columns = "(";
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); 
                if (fields[i].get(this)!=null) {
                    columns=  columns+fields[i].getName() +",";
                    if (!(fields[i].get(this) instanceof Number)) {
                        values = values+"'" +fields[i].get(this)+"'" +",";
                    }else{
                        values = values+ fields[i].get(this) +",";
                    }
                }
            }
            columns= columns.substring(0, columns.lastIndexOf(","))+")";
            values = values.substring(0, values.lastIndexOf(","))+")";

            request = request+ columns + "values"+values ;
            System.out.println(request);
            connection.createStatement().executeUpdate(request);

        }finally{
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }
        
    public Integer insertWithGeneratedKeys(Connection connection , boolean isTransactional) throws Exception{
        try{
            connection.setAutoCommit(false);
            String request = "insert into "+getClass().getSimpleName(); 
            Field [] fields = getClass().getDeclaredFields();
            String values ="(";
            String columns = "(";
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); 
                if (fields[i].get(this)!=null) {
                    columns=  columns+fields[i].getName() +",";
                    if (!(fields[i].get(this) instanceof Number)) {
                        values = values+"'" +fields[i].get(this)+"'" +",";
                    }else{
                        values = values+ fields[i].get(this) +",";
                    }
                }
            }
            columns= columns.substring(0, columns.lastIndexOf(","))+")";
            values = values.substring(0, values.lastIndexOf(","))+")";

            request = request+ columns + "values"+values ;

            Statement s = connection.createStatement();
             s.executeUpdate(request);
            ResultSet res = s.getGeneratedKeys();
            if (res.next()) {
                Integer idGenere = res.getInt(1);
                return idGenere ;
            }else{
                throw new Exception("No key generated");
            }
        }finally{
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }

    public void deleteWhere(Connection connection , boolean isTransactional , String condition) throws Exception{
        try{
            connection.setAutoCommit(false);

            String request = "delete from "+getClass().getSimpleName()+" where "+condition;
            connection.createStatement().executeUpdate(request) ;
        }
        finally{
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }
        }
    }

    public void update(Connection connection , boolean isTransactional , String condition) throws Exception{
        try{
            connection.setAutoCommit(false);

            String request = "update "+getClass().getSimpleName()+" set ";
            Field [] fields = getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true); 
                if (fields[i].get(this)!=null) {
                    if ((fields[i].get(this) instanceof Number)) {
                        request= request + fields[i].getName() +" = "+ fields[i].get(this)+" , ";
                    }
                    else{
                        request= request + fields[i].getName() +" = "+"'"+ fields[i].get(this)+"' , ";                        
                    }
                }
            }
            request=  request.substring(0, request.lastIndexOf(",")) +" where "+condition;

            System.out.println(request);
            connection.createStatement().executeUpdate(request) ;

        }
        finally{
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }            
        }
    }


    public String getOperation1(Connection connection , boolean isTransactional , String humanRequestSplited , String contexte) throws Exception{
        String x =null;
        try {
            Statement statement = connection.createStatement() ;
            
            ResultSet res = statement.executeQuery("select operation from operation where operation like '%"+humanRequestSplited+"%' and contexte like like '%"+contexte+"%'");
            if (res.next()) {
                x = res.getString("operation");
            }
            if (res.next()) {
                return null ;
            }
        } 
        finally{
            if (!isTransactional) {
                connection.commit();
                connection.close();
            }            
        }
        return x;
    }

    public String[] getOperation0(Connection connection , boolean isTransactional , String humanRequest ,String contexte) throws Exception{
        String [] splited = humanRequest.split(" ");
        ArrayList<String> lst = new ArrayList<>() ;
        for (int i = 0; i < splited.length; i++) {
            String op = getOperation1(connection, isTransactional, humanRequest, contexte) ;
            if(op!=null) lst.add(op) ;
        }
        return lst.toArray(new String [lst.size()]);
    }

    public String formSQL (Connection connection , boolean isTransactional , String humanRequest , String context) throws Exception{
        String [] operations = getOperation0(connection, isTransactional, humanRequest, context) ;
        if (operations.length == 0) 
            throw new Exception("Contexte a revoir") ;

        else if (operations.length ==1) 
            return formSQLWithOneOperation(connection, isTransactional ,humanRequest ,context ,operations[0]) ;

        else return formSQLWithMultipleOperation(connection ,isTransactional ,humanRequest, context ,operations)
        
    }

    private String formSQLWithOneOperation(Connection connection, boolean isTransactional, String humanRequest,
            String context, String operation) {

        humanRequest = humanRequest.replace(operation,"") ;

        String [] variableOperation = getVariableOperation(humanRequest) ;

        // esorina izay variable d'operation @ ilay requete

        for (int i = 0; i < variableOperation.length; i++) {
            humanRequest = humanRequest.replace(variableOperation[i],"");
        }

        ArrayList<String[]> wheres= getWheres(humanRequest); 

    }



   private ArrayList<String[]> getWheres(String humanRequest) {
        String [] splited = humanRequest.split(humanRequest) ;
        ArrayList<String[]> ls= new ArrayList<>();
        for (int i = 0; i < splited.length; i++) {

        }
        throw new UnsupportedOperationException("Unimplemented method 'getWheres'");
    }


 private String[] getVariableOperation(String humanRequest) {
        String [] ss = humanRequest.split(" ");
        Field[] fields = this.getClass().getDeclaredFields() ;
        ArrayList<String> list = new ArrayList<>() ;
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < ss.length; j++) {
                if (fields[i].getName().equalsIgnoreCase(ss[j]))list.add(ss[j]);         
            }
        }
        return list.toArray(new String[list.size()]);

    }


    private String formSQLWithMultipleOperation(Connection connection, boolean isTransactional, String humanRequest,
            String context, String[] operations) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'formSQLWithMultipleOperation'");
    }










    
}