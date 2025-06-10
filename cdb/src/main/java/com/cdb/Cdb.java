package com.cdb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class Cdb implements Driver {
    static final Logger _logger = Logger.getLogger(Cdb.class.getName());

    static void Register() {};

    static {
        var d = new Cdb();
        try {
            DriverManager.registerDriver(d);
        } catch (SQLException e) {
            _logger.warning("cdb driver failed to register " + e.getStackTrace());
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        var filename = url.replaceFirst("jdbc:cdb:", "");
        CdbNative.newDb(filename);
        return new CdbConnection(filename);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:cdb:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return _logger;
    }
}
