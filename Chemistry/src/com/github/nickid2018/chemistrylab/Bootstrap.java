package com.github.nickid2018.chemistrylab;

import com.github.nickid2018.chemistrylab.crash.CrashReport;
import com.github.nickid2018.chemistrylab.util.PrintStreamDelegate;

import java.io.PrintStream;

public class Bootstrap {

    public static PrintStream SYS_OUT;
    public static PrintStream SYS_ERR;

    public static void init(String side) {
        delegateSysPrinter();
        delegateExceptionCatcher(side);
    }

    public static void delegateSysPrinter() {
        System.setOut(new PrintStreamDelegate("System.out", SYS_OUT = System.out));
        System.setErr(new PrintStreamDelegate("System.err", SYS_ERR = System.err));
    }

    public static void delegateExceptionCatcher(String side) {
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> {
                    new CrashReport("Final exception catcher", e).writeToFile(side);
                    e.printStackTrace();
                });
    }
}
