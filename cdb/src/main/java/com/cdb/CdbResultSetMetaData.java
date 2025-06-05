package com.cdb;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class CdbResultSetMetaData implements ResultSetMetaData {
    int _prepareId;
    
    public CdbResultSetMetaData(int prepareId) {
        _prepareId = prepareId;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return CdbNative.resultColCount(_prepareId);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        var r = CdbNative.resultColType(_prepareId, column - 1);
        if (r == 1) return Types.INTEGER;
        if (r == 3) return Types.VARCHAR;
        throw new SQLException("unexpected type id " + r);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        var r = CdbNative.resultColType(_prepareId, column - 1);
        if (r == 1) return "INTEGER";
        if (r == 3) return "TEXT";
        throw new SQLException("name unexpected type id " + r);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return CdbNative.resultColName(_prepareId, column - 1);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return CdbNative.resultColName(_prepareId, column - 1);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isWrapperFor'");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unwrap'");
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnClassName'");
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getColumnDisplaySize'");
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPrecision'");
    }

    @Override
    public int getScale(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScale'");
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSchemaName'");
    }

    @Override
    public String getTableName(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTableName'");
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isAutoIncrement'");
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCaseSensitive'");
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCurrency'");
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isDefinitelyWritable'");
    }

    @Override
    public int isNullable(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isNullable'");
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isReadOnly'");
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isSearchable'");
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isSigned'");
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isWritable'");
    }
}