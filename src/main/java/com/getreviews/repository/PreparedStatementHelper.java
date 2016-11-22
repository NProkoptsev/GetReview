package com.getreviews.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.jdbc.core.PreparedStatementCreator;


public class PreparedStatementHelper {
    protected Map<String, String> fields = new HashMap<>();
    protected String sql;
    
    /**
     * Initialize with partial sql statement
     * @param sql
     */
    public PreparedStatementHelper(final String sql) {
        this.sql = sql;
    }
    
    
    /**
     * Put key - value pair
     * @param fieldName
     * @param fieldValue
     */
    public void put(
            final String fieldName, final Object fieldValue) {
        
        if (fieldName == null 
                || fieldName.isEmpty() || fieldValue == null) {
            return;
        }
        
        // String
        if (fieldValue instanceof String) {
            if (!((String) fieldValue).isEmpty()) {
                fields.put(fieldName, "'" + fieldValue + "'");
            }
            
        // Boolean
        } else if (fieldValue instanceof Boolean) {
            fields.put(
                    fieldName, fieldValue.toString().toUpperCase());
            
        // Any other type
        } else {
            fields.put(fieldName, fieldValue.toString());
        }
    }
    
    
    /**
     * Returns PreparedStatementCreator with the specified sql
     * and example.
     * If example has no valid fields, returns null.
     * @return
     */
    public PreparedStatementCreator statementCreator() {
        if (fields.size() == 0) {
            return null;
        }
        
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder(sql);
        boolean firstInserted = false;
        
        for (Entry<String, String> entry : fields.entrySet()) {
            sb.append(" ");
            if (firstInserted == true) {
                sb.append("AND ");
            } else {
                firstInserted = true;
            }
            sb.append(entry.getKey());
            sb.append(" = ?");
            values.add(entry.getValue());
        }
        
        System.out.println("QUERY: " + sb.toString());
        
        PreparedStatementCreator psCreator =
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(
                        Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(
                                sb.toString());
                        for (int i = 0; i < values.size(); i++) {
                            ps.setString(i + 1, values.get(i));
                            System.out.println("ps.setString(" + (i + 1) + ", " 
                                    + values.get(i)+ ");");
                        }
                        return ps;
                    }
                };
        
        return psCreator;
    }
}
