package com.cdb;

import java.lang.foreign.*;
import java.nio.file.Path;

@SuppressWarnings("preview")
public class CdbNative {

    private static Path GetCdbLib() {
        return Path.of("/workspaces/cdb-jdbc/cdb/src/main/resources/com/cdb/cdb.so");
    }

    private static void throwForCode(int code, String nativeName) throws Throwable {
        if (code != 0) {
            throw new Exception("non zero status from " + nativeName);
        }
    }

    public static void newDb(String filename) throws Throwable {
        final String nativeName = "cdb_new_db";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(filename);
        var result = (int) fh.invokeExact(nativeString);
        throwForCode(result, nativeName);
    }

    public static void closeDb(String filename) throws Throwable {
        final String nativeName = "cdb_close_db";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(filename);
        fh.invokeExact(nativeString);
    }

    public static int prepare(String filename, String sql) throws Throwable {
        final String nativeName = "cdb_prepare";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
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
        throwForCode(result, nativeName);
        return prepareId.get(ValueLayout.JAVA_INT, 0);
    }

    public static void closeStatement(int prepareId) throws Throwable {
        final String nativeName = "cdb_close_statement";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        fh.invokeExact(prepareId);
    }

    public static void bindInt(int prepareId, int bound) throws Throwable {
        final String nativeName = "cdb_bind_int";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        var result = (int) fh.invokeExact(prepareId, bound);
        throwForCode(result, nativeName);
    }

    public static void bindString(int prepareId, String bound) throws Throwable {
        final String nativeName = "cdb_bind_string";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeBound = a.allocateUtf8String(bound);
        var result = (int) fh.invokeExact(prepareId, nativeBound);
        throwForCode(result, nativeName);
    }

    public static void execute(int prepareId) throws Throwable {
        final String nativeName = "cdb_execute";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        var result = (int) fh.invokeExact(prepareId);
        throwForCode(result, nativeName);
    }

    public static String resultErr(int prepareId) throws Throwable {
        final String nativeName = "cdb_result_err";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasError = a.allocate(ValueLayout.JAVA_INT, 1);
        var errMessage = a.allocate(ValueLayout.ADDRESS);
        var result = (int) fh.invokeExact(prepareId, hasError, errMessage);
        throwForCode(result, nativeName);
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
        final String nativeName = "cdb_result_row";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasRow = a.allocate(ValueLayout.JAVA_INT, 0);
        var result = (int) fh.invokeExact(prepareId, hasRow);
        throwForCode(result, nativeName);
        var v = hasRow.get(ValueLayout.JAVA_INT, 0);
        return v != 0;
    }

    public static int resultColInt(int prepareId, int colIdx) throws Throwable {
        final String nativeName = "cdb_result_col_int";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultCol = a.allocate(ValueLayout.JAVA_INT, 0);
        var result = (int) fh.invokeExact(prepareId, colIdx, resultCol);
        throwForCode(result, nativeName);
        return resultCol.get(ValueLayout.JAVA_INT, 0);
    }

    public static String resultColString(int prepareId, int colIdx) throws Throwable {
        final String nativeName = "cdb_result_col_string";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(GetCdbLib(), a);
        var f = lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultPtr = a.allocate(ValueLayout.ADDRESS);
        var errCode = (int) fh.invokeExact(prepareId, colIdx, resultPtr);
        throwForCode(errCode, nativeName);
        return resultPtr
                .get(ValueLayout.ADDRESS, 0)
                .reinterpret(Long.MAX_VALUE)
                .getUtf8String(0);
    }
}
