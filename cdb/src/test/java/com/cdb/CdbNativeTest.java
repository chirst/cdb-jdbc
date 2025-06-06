package com.cdb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CdbNativeTest {

    @Test
    public void TestNative() {
        assertDoesNotThrow(() -> {
            CdbNative.newDb(":memory:");

            var createSql = """
            CREATE TABLE IF NOT EXISTS foo (id INTEGER PRIMARY KEY, name TEXT);
            """;
            var createPrepareId = CdbNative.prepare(":memory:", createSql);
            assertNotEquals(createPrepareId, 0);
            CdbNative.execute(createPrepareId);
            var err = CdbNative.resultErr(createPrepareId);
            assertEquals(err, "");
            CdbNative.closeStatement(createPrepareId);

            var insertSql = """
            INSERT INTO foo (id, name) VALUES (?, ?);
            """;
            var insertPrepareId = CdbNative.prepare(":memory:", insertSql);
            assertNotEquals(insertPrepareId, 0);
            CdbNative.bindInt(insertPrepareId, 12);
            CdbNative.bindString(insertPrepareId, "bar");
            CdbNative.execute(insertPrepareId);
            err = CdbNative.resultErr(insertPrepareId);
            assertEquals(err, "");
            CdbNative.closeStatement(insertPrepareId);

            var selectSql = """
            SELECT * FROM foo;
            """;
            var selectPrepareId = CdbNative.prepare(":memory:", selectSql);
            assertNotEquals(selectPrepareId, 0);
            CdbNative.execute(selectPrepareId);
            var hasRow = CdbNative.resultRow(selectPrepareId);
            assertTrue(hasRow);
            var rowId = CdbNative.resultColInt(selectPrepareId, 0);
            var name = CdbNative.resultColString(selectPrepareId, 1);
            assertEquals(rowId, 12);
            assertEquals(name, "bar");
            hasRow = CdbNative.resultRow(selectPrepareId);
            assertFalse(hasRow);
            CdbNative.closeStatement(selectPrepareId);

            CdbNative.closeDb(":memory:");
        });
    }
}
