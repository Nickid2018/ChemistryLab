// --- Import JS ---
/**
 * API Base Class
 */
BASE_CLASS = Java.type("io.github.nickid2018.chemistrylab.mod.javascript.JavaScriptModBase");

/**
 * Import a system JavaScript file API
 * @param name system API name
 * @returns {*} true if imported successfully
 */
function importSystemPackage(name) {
    return BASE_CLASS.importSystemJS(name);
}

/**
 * Import an outside JavaScript file API
 * @param path file path
 * @returns {*} true if imported successfully
 */
function importFilePackage(path) {
    return BASE_CLASS.importOutsideJS(path);
}

/**
 * Get packages loaded
 * @returns {*} a Set
 */
function getLoadedPackages() {
    return BASE_CLASS.instance.loadedPackages;
}

/**
 * Check whether a package has been loaded
 * @param name package name
 */
function isPackageLoaded(name) {
    return getLoadedPackages().contains(name);
}

// --- Cast ---
/**
 * Cast to a type
 * @param object the object to cast
 * @param clazz the class to cast
 */
function cast(object, clazz) {
    return BASE_CLASS.cast(object, clazz);
}

// --- Base Logger API Class ---
/**
 * Logger of JS Engine
 */
BASE_LOGGER = BASE_CLASS.JS_LOGGER;

/**
 * print debug message
 * @param message
 * @param error
 */
function logDebug(message, error) {
    BASE_LOGGER.debug(message);
    if(error != null)
        print(error);
}

/**
 * print information
 * @param message
 * @param error
 */
function logInfo(message, error) {
    BASE_LOGGER.info(message);
    if(error != null)
        print(error);
}

/**
 * print warn message
 * @param message
 * @param error
 */
function logWarn(message, error) {
    BASE_LOGGER.warn(message);
    if(error != null)
        print(error);
}

/**
 * print error message
 * @param message
 * @param error
 */
function logError(message, error) {
    BASE_LOGGER.error(message);
    if(error != null)
        print(error);
}

/**
 * print fatal message
 * @param message
 * @param error
 */
function logFatal(message, error) {
    BASE_LOGGER.fatal(message);
    if(error != null)
        print(error);
}

// --- Crash API Class ---
/**
 * Class CrashReport
 */
CRASH_REPORT_CLASS = Java.type("io.github.nickid2018.chemistrylab.crash.CrashReport");
/**
 * Class CrashReportSession
 */
CRASH_SESSION_CLASS = Java.type("io.github.nickid2018.chemistrylab.crash.CrashReportSession");
/**
 * Class DetectedCrashException
 */
DetectedCrashException = Java.type("io.github.nickid2018.chemistrylab.crash.DetectedCrashException");
/**
 * Class ScriptException
 */
ScriptException = Java.type("javax.script.ScriptException");

/**
 * Throw a Java Exception
 * @param exception an exception
 */
function throwJavaException(exception) {
    BASE_CLASS.exceptionFromJS(exception);
}

/**
 * Create a CrashReport
 * @param name Description of the error
 * @param exceptionDesc Error Details
 * @returns {*} a CrashReport
 */
function newCrashReport(name, exceptionDesc) {
    return new CRASH_REPORT_CLASS(name, new ScriptException(exceptionDesc));
}

/**
 * Create a CrashReport from a Java Exception
 * @param name Description of the error
 * @param exception a Java Exception
 * @returns {*} a CrashReport
 */
function newCrashReportFromJavaException(name, exception) {
    return new CRASH_REPORT_CLASS(name, exception);
}

/**
 * Create a CrashReportSession
 * @param name the name of the session
 * @returns {*} a CrashReportSession
 */
function newCrashSession(name) {
    return new CRASH_SESSION_CLASS(name);
}

/**
 * Throw a Java Crash of the CrashReport
 * @param crashReport a CrashReport
 */
function throwCrash(crashReport) {
    BASE_CLASS.crashFromJS(new DetectedCrashException(crashReport));
}

System = Java.type("java.lang.System");

function systemExit(value) {
    System.exit(value);
}

// --- API Invocation ---
/* ****** Initialization ****** */
function __system__init__() {
    logDebug("builtin package 'system' loaded");
}