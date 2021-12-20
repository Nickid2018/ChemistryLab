package io.github.nickid2018.chemistrylab.mod.javascript;

import io.github.nickid2018.chemistrylab.annotation.API;
import io.github.nickid2018.chemistrylab.annotation.ScriptAPI;
import io.github.nickid2018.chemistrylab.crash.CrashReport;
import io.github.nickid2018.chemistrylab.crash.CrashReportSession;
import io.github.nickid2018.chemistrylab.crash.DetectedCrashException;
import io.github.nickid2018.chemistrylab.mod.ModBase;
import jdk.dynalink.beans.StaticClass;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class JavaScriptModBase implements ModBase {

    public static final Logger JS_LOGGER = LogManager.getLogger("Java Script Mod");

    public static JavaScriptModBase instance;

    public Set<String> loadedPackages = new HashSet<>();
    private ScriptEngine scriptEngine;
    private boolean valid = false;

    public JavaScriptModBase(){
        instance = this;
    }

    @Override
    public void init() {
        engineInitialize();
        if(valid) {
            if(!importSystemJS("system")){
                valid = false;
                JS_LOGGER.warn("(JS Engine) system.js cannot be loaded! JS Engine will be disabled.");
            }
        }
    }

    public void evalString(String eval) throws ScriptException {
        if(valid) {
            scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, null, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(eval);
        } else
            JS_LOGGER.warn("(JS Engine) Code attempts to run but engine has been disabled.");
    }

    private void engineInitialize() {
        ScriptEngineManager manager = new ScriptEngineManager();
        scriptEngine = manager.getEngineByName("javascript");
        valid = scriptEngine != null;
        if(!valid)
            JS_LOGGER.warn("(JS Engine) Cannot initialize! JS Engine will be disabled.");
    }

    public void evalJS(InputStream source, String name) throws IOException, ScriptException {
        if(valid) {
            String info = IOUtils.toString(source, StandardCharsets.UTF_8);
            scriptEngine.getContext().setAttribute(ScriptEngine.FILENAME, name, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(info);
        } else
            JS_LOGGER.warn("(JS Engine) Code attempts to run but engine has been disabled.");
    }

    @Override
    public void terminate() {
        if(valid) {

        }
    }

    // --- JS APIs ---
    @API
    @ScriptAPI("system:throwJavaException")
    public static void exceptionFromJS(Throwable exception) throws Throwable {
        throw exception;
    }

    @API
    @ScriptAPI("system:throwCrash")
    public static void crashFromJS(DetectedCrashException exception) {
        CrashReport report = exception.getReport();
        report.getCause().fillInStackTrace();
        CrashReportSession session = new CrashReportSession("JavaScript");
        report.addSession(session);
        session.addDetailObject("Engine", instance.scriptEngine);
        ScriptEngineFactory factory = instance.scriptEngine.getFactory();
        session.addDetailObject("Engine Name", factory.getEngineName());
        session.addDetailObject("Engine Version" , factory.getEngineVersion());
        session.addDetailObject("Language Name", factory.getLanguageName());
        session.addDetailObject("Language Version", factory.getLanguageVersion());
        throw exception;
    }

    @API
    @ScriptAPI("system:importSystemPackage")
    public static boolean importSystemJS(String name) {
        if(instance.loadedPackages.contains(name))
            return true;
        try {
            instance.evalJS(JavaScriptModBase.class.getResourceAsStream(String.format("/scripts/%s.js", name)),
                    String.format("[builtin]%s.js", name));
            ((Invocable)instance.scriptEngine).invokeFunction(String.format("__%s__init__", name));
            instance.loadedPackages.add(name);
        } catch (Exception e) {
            JS_LOGGER.warn("([builtin]{}.js) Cannot be evaluated!", name, e);
            return false;
        }
        return true;
    }

    @API
    @ScriptAPI("system:importFilePackage")
    public static boolean importOutsideJS(String path) {
        if(instance.loadedPackages.contains(path))
            return true;
        try {
            instance.evalJS(new FileInputStream(path), path);
            instance.loadedPackages.add(path);
        } catch (Exception e) {
            JS_LOGGER.warn("({}) Cannot be evaluated!", path, e);
            return false;
        }
        return true;
    }

    /**
     * system.js - cast
     * @param object a JavaScript-Mirrored object
     * @param castClass the class target to cast
     * @param <T> class target
     * @return a Java object
     */
    @SuppressWarnings("unchecked")
    @API
    @ScriptAPI("system:cast")
    public static <T> T cast(Object object, StaticClass castClass) {
        if(object instanceof ScriptObjectMirror mirror)
            return (T) mirror.to(castClass.getRepresentedClass());
        else
            return (T) castClass.getRepresentedClass().cast(object);
    }
}
