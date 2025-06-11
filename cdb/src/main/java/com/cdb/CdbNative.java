package com.cdb;

import java.io.IOException;
import java.lang.foreign.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

@SuppressWarnings("preview")
public class CdbNative {

    private static Arena _globalArena = Arena.global();
    private static SymbolLookup _lib;

    static {
        Path p;
        try {
            p = GetCdbLib();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Error("failed to init CdbNative");
        }
        _lib = SymbolLookup.libraryLookup(p, _globalArena);
    }

    private static Path GetCdbLib() throws SQLException {
        var dir = GetOsDir();
        var resourceName = String.format("com/cdb/%s/cdb.so", dir);
        var loader = CdbNative.class.getClassLoader();
        var r = loader.getResourceAsStream(resourceName);
        Path tempFile;
        try {
            tempFile = Files.createTempFile("cdbtemp", ".so");
        } catch (IOException e) {
            throw new SQLException("failed to create temp native lib");
        }
        try {
            Files.copy(r, tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new SQLException("failed to copy native lib into temp");
        }
        System.load(tempFile.toString());
        tempFile.toFile().deleteOnExit();
        return tempFile;
    }

    private static String GetOsDir() {
        var osName = System.getProperty("os.name").toLowerCase();
        var osArchName = System.getProperty("os.arch").toLowerCase();
        if (osName.contains("mac")) {
            if (osArchName.contains("aarch64")) return "mac/arm64";
            if (osArchName.contains("x86")) return "mac/x86_64";
        }
        if (osName.contains("linux")) {
            if (osArchName.contains("aarch64")) return "linux/aarch64";
            if (osArchName.contains("amd64")) return "linux/amd64";
        }
        throw new Error("no lib for platform " + osName + "/" + osArchName);
    }

    private static void throwForCode(int code, String nativeName) throws SQLException {
        if (code != 0) {
            throw new SQLException("non zero status from " + nativeName);
        }
    }

    public static void newDb(String filename) throws SQLException {
        final String nativeName = "cdb_new_db";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(filename);
        int result;
        try {
            result = (int) fh.invokeExact(nativeString);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
    }

    public static void closeDb(String filename) throws SQLException {
        final String nativeName = "cdb_close_db";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(filename);
        try {
            fh.invokeExact(nativeString);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
    }

    public static int prepare(String filename, String sql) throws SQLException {
        final String nativeName = "cdb_prepare";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeFilename = a.allocateUtf8String(filename);
        MemorySegment nativeSql = a.allocateUtf8String(sql);
        var prepareId = a.allocate(ValueLayout.JAVA_INT, 0);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, nativeFilename, nativeSql);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
        return prepareId.get(ValueLayout.JAVA_INT, 0);
    }

    public static void closeStatement(int prepareId) throws SQLException {
        final String nativeName = "cdb_close_statement";
        var linker = Linker.nativeLinker();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        try {
            fh.invokeExact(prepareId);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
    }

    public static void bindInt(int prepareId, int bound) throws SQLException {
        final String nativeName = "cdb_bind_int";
        var linker = Linker.nativeLinker();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, bound);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
    }

    public static void bindString(int prepareId, String bound) throws SQLException {
        final String nativeName = "cdb_bind_string";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeBound = a.allocateUtf8String(bound);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, nativeBound);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
    }

    public static void execute(int prepareId) throws SQLException {
        final String nativeName = "cdb_execute";
        var linker = Linker.nativeLinker();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
        var fh = linker.downcallHandle(f, fd);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
    }

    public static String resultErr(int prepareId) throws SQLException {
        final String nativeName = "cdb_result_err";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasError = a.allocate(ValueLayout.JAVA_INT, 1);
        var errMessage = a.allocate(ValueLayout.ADDRESS);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, hasError, errMessage);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
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

    public static boolean resultRow(int prepareId) throws SQLException {
        final String nativeName = "cdb_result_row";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var hasRow = a.allocate(ValueLayout.JAVA_INT, 0);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, hasRow);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
        var v = hasRow.get(ValueLayout.JAVA_INT, 0);
        return v != 0;
    }

    public static int resultColInt(int prepareId, int colIdx) throws SQLException {
        final String nativeName = "cdb_result_col_int";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultCol = a.allocate(ValueLayout.ADDRESS);
        int result;
        try {
            result = (int) fh.invokeExact(prepareId, colIdx, resultCol);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(result, nativeName);
        return resultCol.get(ValueLayout.JAVA_INT, 0);
    }

    public static String resultColString(int prepareId, int colIdx) throws SQLException {
        final String nativeName = "cdb_result_col_string";
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var f = _lib.find(nativeName).orElseThrow();
        var fd = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        var resultPtr = a.allocate(ValueLayout.ADDRESS);
        int errCode;
        try {
            errCode = (int) fh.invokeExact(prepareId, colIdx, resultPtr);
        } catch (Throwable e) {
            throw new SQLException("failed to invoke " + nativeName, e);
        }
        throwForCode(errCode, nativeName);
        return resultPtr
                .get(ValueLayout.ADDRESS, 0)
                .reinterpret(Long.MAX_VALUE)
                .getUtf8String(0);
    }
}
