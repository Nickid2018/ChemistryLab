// --- Collection API ---
var Arrays = Java.type("java.util.Arrays");
var Lists = Java.type("com.google.common.collect.Lists");
var Sets = Java.type("com.google.common.collect.Sets");

// --- File API ---
var File = Java.type("java.io.File");
var InputStream = Java.type("java.io.InputStream");
var FileInputStream = Java.type("java.io.FileInputStream");
var Reader = Java.type("java.io.Reader");
var FileReader = Java.type("java.io.FileReader");
var OutputStream = Java.type("java.io.OutputStream");
var FileOutputStream = Java.type("java.io.FileOutputStream");
var Writer = Java.type("java.io.Writer");
var FileWriter = Java.type("java.io.FileWriter");
var IOUtils = Java.type("org.apache.commons.io.IOUtils");
var IOException = Java.type("java.io.IOException");

// --- Charset ---
var Charset = Java.type("java.nio.charset.Charset");
var StandardCharsets = Java.type("java.nio.charset.StandardCharsets");

// --- Array ---
function binarySearch(array, key) {
    return Arrays.binarySearch(array, key);
}

function binarySearchWithComparator(array, key, comparator) {
    return Arrays.binarySearch(array, key, comparator);
}

// --- API Invocation ---
/* ****** Initialization ****** */
function __utils__init__() {
    logDebug("builtin package 'utils' loaded");
}