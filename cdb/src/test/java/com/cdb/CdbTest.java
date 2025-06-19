package com.cdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CdbTest {
    private Connection _connection;

    @BeforeAll
    public static void BeforeAll() {
        Cdb.Register();
    }

    @BeforeEach
    private void BeforeEach() throws SQLException {
        _connection = DriverManager.getConnection("jdbc:cdb::memory:");
    }

    @AfterEach
    private void AfterEach() throws SQLException {
        _connection.close();
        _connection = null;
    }

    private void SetupFooTable() throws SQLException {
        _connection.prepareStatement(
            "CREATE TABLE foo (id INTEGER PRIMARY KEY, name TEXT)"
        ).execute();
    }

    private void InsertIntoFooTable(int id, String name) throws SQLException {
        var statement = _connection.prepareStatement(
            "INSERT INTO foo (id, name) VALUES (?, ?)"
        );
        statement.setInt(1, id);
        statement.setString(2, name);
        statement.execute();
    }

    @Test
    public void TestSequenceOfStatements() throws SQLException {
        var createStatement = _connection.prepareStatement(
            "CREATE TABLE foo (id INTEGER PRIMARY KEY, name TEXT)"
        );
        var createHasResult = createStatement.execute();
        assertFalse(createHasResult);
        createStatement.close();

        var insertStatement = _connection.prepareStatement(
            "INSERT INTO foo (id, name) VALUES (12, 'asdf');"
        );
        var insertHasResult = insertStatement.execute();
        assertFalse(insertHasResult);
        insertStatement.close();

        var selectStatement = _connection.prepareStatement("SELECT * FROM foo;");
        var rs = selectStatement.executeQuery();
        rs.next();
        var id = rs.getInt(1);
        var name = rs.getString(2);
        selectStatement.close();
        assertEquals(id, 12);
        assertEquals(name, "asdf");
    }

    @Test
    public void TestExecuteCreateNoResult() throws SQLException {
        var preparedStatement = _connection.prepareStatement(
            "CREATE TABLE foo (id, name) VALUES (12, 'asdf')"
        );
        var hasResult = preparedStatement.execute();
        assertFalse(hasResult);
        var updateCount = preparedStatement.getUpdateCount();
        assertEquals(updateCount, 1);
        var resultSet = preparedStatement.getResultSet();
        assertNull(resultSet);
    }

    @Test
    public void TestExecuteInsertNoResult() throws SQLException {
        SetupFooTable();
        var preparedStatement = _connection.prepareStatement(
            "INSERT INTO foo (name) VALUES ('Gud name')"
        );
        var hasResult = preparedStatement.execute();
        assertFalse(hasResult);
        var updateCount = preparedStatement.getUpdateCount();
        assertEquals(updateCount, 1);
        var resultSet = preparedStatement.getResultSet();
        assertNull(resultSet);
    }

    @Test
    public void TestExecuteSelect() throws SQLException {
        SetupFooTable();
        InsertIntoFooTable(1, "Gud name 1");
        var preparedStatement = _connection.prepareStatement("SELECT * FROM foo");
        var hasResult = preparedStatement.execute();
        assertTrue(hasResult);
        var updateCount = preparedStatement.getUpdateCount();
        assertEquals(updateCount, -1);
        var results = preparedStatement.getResultSet();
        var isValid = results.next();
        assertTrue(isValid);
        isValid = results.next();
        assertFalse(isValid);
    }

    @Test
    public void TestExecuteResultWithSQL() throws SQLException {
        SetupFooTable();
        InsertIntoFooTable(1, "Gud Name");
        var preparedStatement = _connection.createStatement();
        var hasResult = preparedStatement.execute("SELECT * FROM foo;");
        assertTrue(hasResult);
    }

    @Test
    public void TestExecuteEmptyResultWithSQL() throws SQLException {
        SetupFooTable();
        var preparedStatement = _connection.createStatement();
        var hasResult = preparedStatement.execute(
            "INSERT INTO foo (id, name) VALUES (1, 'asdf')"
        );
        assertFalse(hasResult);
    }

    @Test
    public void TestExecuteQuery() throws SQLException {
        SetupFooTable();
        InsertIntoFooTable(1, "Gud Name");
        var preparedStatement = _connection.createStatement();
        var result = preparedStatement.executeQuery("SELECT * FROM foo");
        assertTrue(result.next());
    }
}
