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
    protected Map<String, Object> fields = new HashMap<>();
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
        // Empty string
        if (fieldValue instanceof String && ((String) fieldValue).isEmpty()) {
            return;
        }
        
        fields.put(fieldName, fieldValue);
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
        
        List<Object> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder(sql);
        boolean firstInserted = false;
        
        for (Entry<String, Object> entry : fields.entrySet()) {
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
        
        PreparedStatementCreator psCreator =
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(
                        Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(
                                sb.toString());
                        for (int i = 0; i < values.size(); i++) {
                            // Boolean
                            if (values.get(i) instanceof Boolean) {
                                ps.setBoolean(i + 1, (boolean) values.get(i));
                            // Long
                            } else if (values.get(i) instanceof Long) {
                                ps.setLong(i + 1, (long) values.get(i));
                            // Integer
                            } else if (values.get(i) instanceof Integer) {
                                ps.setInt(i + 1, (int) values.get(i));
                            // Float
                            } else if (values.get(i) instanceof Float) {
                                ps.setFloat(i + 1, (float) values.get(i));
                            // String
                            } else {
                                ps.setString(i + 1, values.get(i).toString());
                            }
                        }
                        return ps;
                    }
                };
        
        return psCreator;
    }
}
