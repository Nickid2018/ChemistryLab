package com.github.nickid2018.chemistrylab.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.OutputStream;
import java.io.PrintStream;

public class PrintStreamDelegate extends PrintStream {

    private final Logger logger;

    private final String name;

    public PrintStreamDelegate(String name, OutputStream out) {
        super(out);
        this.name = name;
        logger = LogManager.getLogger(name);
    }

    public void println(@Nullable String string) {
        log(string);
    }

    public void println(Object object) {
        log(String.valueOf(object));
    }

    protected void log(@Nullable String string) {
        logger.info(string);
    }
}
