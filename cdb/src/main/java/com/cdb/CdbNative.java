package com.cdb;

import java.lang.foreign.*;
import java.nio.file.Path;

@SuppressWarnings("preview")
public class CdbNative {

    private static Path GetCdbLib() {
        return Path.of("/workspaces/cdb-jdbc/cdb/src/main/resources/com/cdb/cdb.so");
    }

    public static void newDb(String filename) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_new_db").orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(":memory:");
        var result = (int) fh.invokeExact(nativeString);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
    }

    public static void closeDb(String filename) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_close_db").orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(":memory:");
        fh.invokeExact(nativeString);
    }

    public static int prepare(String filename, String sql) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_prepare").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeFilename = a.allocateUtf8String(filename);
        MemorySegment nativeSql = a.allocateUtf8String(sql);
        var prepareId = a.allocate(ValueLayout.JAVA_INT, 0);
        var result = (int) fh.invokeExact(prepareId, nativeFilename, nativeSql);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
        return prepareId.get(ValueLayout.JAVA_INT, 0);
    }

    public static void closeStatement(int prepareId) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_close_statement").orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        fh.invokeExact(prepareId);
    }

    public static void bindInt(int prepareId, int bound) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_bind_int").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        var result = (int) fh.invokeExact(prepareId, bound);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
    }

    public static void bindString(int prepareId, String bound) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_bind_string").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeBound = a.allocateUtf8String(bound);
        var result = (int) fh.invokeExact(prepareId, nativeBound);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
    }

    public static void execute(int prepareId) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_execute").orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        var result = (int) fh.invokeExact(prepareId);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
    }

    public static String resultErr(int prepareId) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_result_err").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasError = a.allocate(ValueLayout.JAVA_INT, 1);
        var errMessage = a.allocate(ValueLayout.ADDRESS);
        var result = (int) fh.invokeExact(prepareId, hasError, errMessage);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
        var v = hasError.get(ValueLayout.JAVA_INT, 0);
        if (v == 0) {
            return "";
        }
        return errMessage
                .get(ValueLayout.ADDRESS, 0)
                .reinterpret(Long.MAX_VALUE)
                .getUtf8String(0);
    }

    public static boolean resultRow(int prepareId) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_result_row").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasRow = a.allocate(ValueLayout.JAVA_INT, 0);
        var result = (int) fh.invokeExact(prepareId, hasRow);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
        var v = hasRow.get(ValueLayout.JAVA_INT, 0);
        return v != 0;
    }

    public static int resultColInt(int prepareId, int colIdx) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_result_col_int").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultCol = a.allocate(ValueLayout.JAVA_INT, 0);
        var result = (int) fh.invokeExact(prepareId, colIdx, resultCol);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
        return resultCol.get(ValueLayout.JAVA_INT, 0);
    }

    public static String resultColString(int prepareId, int colIdx) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find("cdb_result_col_string").orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultPtr = a.allocate(ValueLayout.ADDRESS);
        var errCode = (int) fh.invokeExact(prepareId, colIdx, resultPtr);
        if (errCode != 0) {
            throw new Exception("non zero status from cdb");
        }
        return resultPtr
                .get(ValueLayout.ADDRESS, 0)
                .reinterpret(Long.MAX_VALUE)
                .getUtf8String(0);
    }
}
