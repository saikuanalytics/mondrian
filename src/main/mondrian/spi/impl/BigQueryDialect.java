/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2011-2011 Pentaho and others
// All Rights Reserved.
*/
package mondrian.spi.impl;

import mondrian.olap.Util;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Implementation of {@link mondrian.spi.Dialect} for Goolge Big Query.
 *
 * @author Paul Stoellberger
 * @since Sept 21, 2012
 */
public class BigQueryDialect extends JdbcDialectImpl {
    private static final int MAX_COLUMN_NAME_LENGTH = 128;

    public static final JdbcDialectFactory FACTORY =
        new JdbcDialectFactory(
            BigQueryDialect.class,
            DatabaseProduct.BIGQUERY)
        {
            protected boolean acceptsConnection(Connection connection) {
                return super.acceptsConnection(connection);
            }
        };

    /**
     * Creates a HiveDialect.
     *
     * @param connection Connection
     *
     * @throws SQLException on error
     */
    public BigQueryDialect(Connection connection) throws SQLException {
        super(connection);
    	System.out.println("Using BigQuery Dialect");
    }

    protected String deduceIdentifierQuoteString(
        DatabaseMetaData databaseMetaData)
    {
//        try {
        	return null;
//        	
//            final String quoteIdentifierString =
//                databaseMetaData.getIdentifierQuoteString();
//            return "".equals(quoteIdentifierString)
//                // quoting not supported
//                ? null
//                : quoteIdentifierString;
//        } catch (SQLException e) {
//            // Not supported by HiveDatabaseMetaData; do nothing if catch an
//            // Exception
//            return "`";
//        }
    }

    protected Set<List<Integer>> deduceSupportedResultSetStyles(
        DatabaseMetaData databaseMetaData)
    {
        // Hive don't support this, so just return an empty set.
        return Collections.emptySet();
    }

    protected boolean deduceReadOnly(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.isReadOnly();
        } catch (SQLException e) {
            // Hive is read only (as of release 0.7)
            return true;
        }
    }

    protected int deduceMaxColumnNameLength(DatabaseMetaData databaseMetaData) {
        try {
            return databaseMetaData.getMaxColumnNameLength();
        } catch (SQLException e) {
            return MAX_COLUMN_NAME_LENGTH;
        }
    }

    public boolean allowsCompoundCountDistinct() {
        return true;
    }

    public boolean requiresAliasForFromQuery() {
        return false;
    }

    public boolean requiresOrderByAlias() {
    	System.out.println("requires order by alias");
        return true;
    }
    
    public boolean requiresUnionOrderByOrdinal() {
    	System.out.println("requiresUnionOrderByOrdinal order by alias");
    	return true;
    };
    
    
    public boolean requiresGroupByAlias() {
    	System.out.println("requires group by alias");
    	return true;
    }

    public boolean requiresUnionOrderByExprToBeInSelectClause() {
    	System.out.println("requiresUnionOrderByExprToBeInSelectClause");
        return true;
    }

    public String generateInline(
        List<String> columnNames,
        List<String> columnTypes,
        List<String[]> valueList)
    {
        return "select * from ("
            + generateInlineGeneric(
                columnNames, columnTypes, valueList, " from dual", false)
            + ") x limit " + valueList.size();
    }

    protected void quoteDateLiteral(
        StringBuilder buf,
        String value,
        Date date)
    {
        // Hive doesn't support Date type; treat date as a string '2008-01-23'
        Util.singleQuoteString(value, buf);
    }

    @Override
    protected String generateOrderByNulls(
        String expr,
        boolean ascending,
        boolean collateNullsLast)
    {
            if (ascending) {
                return expr + " ASC";
            } else {
            	 return expr + " DESC";
            }

    }

    public boolean allowsAs() {
        return true;
    }

    @Override
    public boolean allowsJoinOn() {
        return false;
    }
    
    @Override
    public boolean allowsOrderByAlias() {
    	return true;
    }
}

// End BigQueryDialect.java
