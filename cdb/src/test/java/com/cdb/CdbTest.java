package com.cdb;

import static org.junit.Assert.assertEquals;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

public class CdbTest {
    @Test
    public void Foo() throws SQLException, ClassNotFoundException {
        var d = new Cdb();
        DriverManager.registerDriver(d);
        var connection = DriverManager.getConnection("jdbc:cdb:memory:");
        var sql = """
            CREATE TALBE IF NOT EXISTS foo (
                ID INTEGER PRIMARY KEY,
                name TEXT
            );
        """;
        // var s = connection.prepareStatement(sql);
        // s.execute();
        connection.close();
        assertEquals(2, 2);
    }
}
