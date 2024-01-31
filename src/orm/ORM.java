package orm;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ConnectException;
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
// meilleur qualite 

// meilleur qualite prix

// meilleur rapport qualite prix

// meilleur rapport  qualite 

    private String getWhereWithFields(String values) {
        Field[] fields = getClass().getDeclaredFields() ;
        String [] listOfFields = getListOfFields();

        String val = " WHERE" ;
        for (int i = 0; i < listOfFields.length; i++) {
            
            if(fields[i].getType().isInstance(values)) {
                try {
                    Integer x = Integer.parseInt(values) ;
                    val = val + " "+listOfFields[i]+" = "+x+" or ";
                    
                } catch (Exception e) {
                    // TODO: handle exception
                    val = val + " "+listOfFields[i]+" ILIKE '%"+values+"%' or ";
                }
            } 
        }
        val = val.substring(0,val.lastIndexOf(" or"));
        return val ;
    }


    private String getCaseWhen(String values) {
        if (values !=null && !values.isEmpty()) {
            String [] listOfFields = getListOfFields();
            Field[] fields = getClass().getDeclaredFields() ;
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
            
            ResultSet res = statement.executeQuery("select operation from operation where mots ilike '%"+humanRequestSplited+"%' and contexte ilike '%"+contexte+"%'");
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
            String op = getOperation1(connection, true, splited[i], contexte) ;
            if(op!=null) lst.add(op) ;
        }
        return lst.toArray(new String [lst.size()]);
    }

    // public String [] getAllOperation(Connection connection , boolean isTransactional, String contexte) throws Exception{

    //     ResultSet res = connection.createStatement().executeQuery("select operation from operation where contexte='"+contexte+"'");   
    //     ArrayList<String> lst = new ArrayList<>();
    //     while (res.next()) {
    //         lst.add(res.getString("operation")) ;
    //     }
    //     return lst .toArray(new String[lst.size()]) ;
    // }

    // public String findPerfectMatch(Connection connection , boolean isTransactional , String humanRequest , String contexte) throws Exception{
    //     String [] allOperation = getAllOperation(connection, false, contexte) ;
    //     ArrayList<String> match = 
    // }

    public String formSQL (Connection connection , boolean isTransactional , String humanRequest , String context) throws Exception{
        humanRequest = humanRequest.replaceAll("  "," ");
        try{
            String [] operations = getOperation0(connection, true, humanRequest, context) ;
            if (operations.length == 0) 
                throw new Exception("Contexte a revoir") ;
    
            else if (operations.length ==1) 
                return formSQLWithOneOperation(connection, true ,humanRequest ,context ,operations[0]) ;
    
            else return formSQLWithMultipleOperation(connection ,true ,humanRequest, context ,operations);
        }
        finally{
            if (!isTransactional) {
                connection.close();
            }
        }
        
    }

    private String formSQLWithOneOperation(Connection connection, boolean isTransactional, String humanRequest,
            String context, String operation) throws Exception {
        String original = humanRequest ;

        String [] variableOperation = getVariableOperation(humanRequest) ;

        // esorina izay variable d'operation @ ilay requete

        for (int i = 0; i < variableOperation.length; i++) {
            humanRequest = humanRequest.replace(variableOperation[i],"");
        }
        // maka ny where rehetra
        ArrayList<String[]> wheres= getWheres(connection,humanRequest); 

        // former requete : select * from produits where dsf=dsfnjds order by variableOperation asc

        
        String where = formWhere(wheres) ;
        
        String regroupement = checkRegroupement(connection, variableOperation ,humanRequest);
        String orderBy ="" ;
        if (regroupement== null) {
            orderBy= getOrderBy(operation, variableOperation);
            
        }
        else{
            orderBy =getOrderBy(operation, regroupement)  ;
        }


        return "SELECT * FROM "+getClass().getSimpleName()+" "+where+" "+orderBy ;

    }


    @SuppressWarnings("unchecked")
    public T[] selectWithRequest(Connection connection , boolean isTransactional ,String humanRequest ,String context) throws Exception{

        try{
            String request = formSQL(connection, true, humanRequest,context);
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



    private String getOrderBy(String operation, String regroupement) {
        return operation.replace("%", regroupement);
    }


    private String checkRegroupement(Connection connection , String[] variableOperation , String humanRequest) throws Exception {
        String[] splited = humanRequest.split(" ");
        String operation =null ;
        for (int i = 0; i < splited.length; i++) {
            String re = "select operation from regrouprement where mots ilike '%"+splited[i]+"%'";            
            ResultSet r = connection.createStatement().executeQuery(re);
            if (r.next()) {
                operation = r.getString("operation");                
                break ;
            }
        }
        if (operation==null) {
            return null ;
        }
        String end = "" ;

        for (int i = 0; i < variableOperation.length; i++) {
            end=  end + variableOperation[i]+ "/";
        }
        end = end.substring(0,end.lastIndexOf("/"));

        return end ;

    }


    private String formWhere(ArrayList<String[]> wheres) {
        if (wheres.size()!=0) {
            String x = " where " ;
            for (int i = 0; i < wheres.size(); i++) {
                x = x +wheres.get(i)[0]+"="+wheres.get(i)[1]+" and ";
            }
            x= x.substring(0, x.lastIndexOf("and ")) ;
            return x; 
                
        }
        return " " ;
    }


    private String getOrderBy(String operation, String[] variableOperation) {
        String x = "" ;
        for (int i = 0; i < variableOperation.length; i++) {
            x =x+ variableOperation[i]+",";
        }
        x = x.substring(0,x.lastIndexOf(","));
        operation = operation.replace("%",x);
        return operation ;

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


    private String[] getVariableOperation(String humanRequest) {
        String [] ss = humanRequest.split(" ");
        Field[] fields = this.getClass().getDeclaredFields() ;
        ArrayList<String> list = new ArrayList<>() ;
        for (int j = 0; j < ss.length; j++) {
            for (int i = 0; i < fields.length; i++) {
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