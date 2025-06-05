package com.cdb;

import java.lang.foreign.*;
import java.nio.file.Path;

@SuppressWarnings("preview")
public class CdbNative {

    public static void newDb(String filename) throws Throwable {
        var linker = Linker.nativeLinker();
        var a = Arena.global();
        var lib = SymbolLookup.libraryLookup(Path.of("/usr", "lib", "libcdb.so"), a);
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
        var lib = SymbolLookup.libraryLookup(Path.of("/usr", "lib", "libcdb.so"), a);
        var f = lib.find("cdb_close_db").orElseThrow();
        var fd = FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
        var fh = linker.downcallHandle(f, fd);
        MemorySegment nativeString = a.allocateUtf8String(":memory:");
        var result = (int) fh.invokeExact(nativeString);
        if (result != 0) {
            throw new Exception("non zero status from cdb");
        }
    }
}
