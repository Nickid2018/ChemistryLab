// --- Collection API ---
Arrays = Java.type("java.util.Arrays");
Lists = Java.type("com.google.common.collect.Lists");
Sets = Java.type("com.google.common.collect.Sets");

// --- File API ---
File = Java.type("java.io.File");
InputStream = Java.type("java.io.InputStream");
FileInputStream = Java.type("java.io.FileInputStream");
Reader = Java.type("java.io.Reader");
FileReader = Java.type("java.io.FileReader");
OutputStream = Java.type("java.io.OutputStream");
FileOutputStream = Java.type("java.io.FileOutputStream");
Writer = Java.type("java.io.Writer");
FileWriter = Java.type("java.io.FileWriter");
IOUtils = Java.type("org.apache.commons.io.IOUtils");
IOException = Java.type("java.io.IOException");

// --- Charset ---
Charset = Java.type("java.nio.charset.Charset");
StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

// --- Array ---
function binarySearch(array, key) {
    return Arrays.binarySearch(array, key);
}

function binarySearchWithComparator(array, key, comparator) {
    return Arrays.binarySearch(array, key, comparator);
}

function sort(array) {
    Arrays.sort(array);
}

function sortWithComparator(array, comparator) {
    Arrays.sort(array, comparator);
}

// --- API Invocation ---
/* ****** Initialization ****** */
function __utils__init__() {
    logDebug("builtin package 'utils' loaded");
}