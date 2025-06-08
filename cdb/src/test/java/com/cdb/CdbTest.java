package com.cdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

public class CdbTest {
    @Test
    public void TestCreate() throws SQLException, ClassNotFoundException {
        var d = new Cdb();
        DriverManager.registerDriver(d);
        var connection = DriverManager.getConnection("jdbc:cdb::memory:");

        var createStatement = connection.prepareStatement(
            "CREATE TABLE foo (id INTEGER PRIMARY KEY, name TEXT)"
        );
        createStatement.execute();
        createStatement.close();

        var insertStatement = connection.prepareStatement(
            "INSERT INTO foo (id, name) VALUES (12, 'asdf');"
        );
        insertStatement.execute();
        insertStatement.close();

        var selectStatement = connection.prepareStatement("SELECT * FROM foo;");
        var rs = selectStatement.executeQuery();
        rs.next();
        var id = rs.getInt(0);
        var name = rs.getString(1);
        selectStatement.close();

        connection.close();

        assertEquals(id, 12);
        assertEquals(name, "asdf");
    }
}
