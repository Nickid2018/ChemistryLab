package io.github.nickid2018.chemistrylab;

import io.github.nickid2018.chemistrylab.crash.CrashReport;
import io.github.nickid2018.chemistrylab.crash.DetectedCrashException;
import io.github.nickid2018.chemistrylab.util.PrintStreamDelegate;

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

    public static void terminate() {

    }

    public static void delegateExceptionCatcher(String side) {
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> {
                    CrashReport report;
                    if (e instanceof DetectedCrashException detected)
                        (report = detected.getReport()).writeToFile(side);
                    else {
                        Throwable source = e;
                        DetectedCrashException detected = null;
                        while((e = e.getCause()) != null)
                            if(e instanceof DetectedCrashException d) {
                                detected = d;
                                break;
                            }
                        if(detected == null)
                            (report = new CrashReport("Final exception catcher", source)).writeToFile(side);
                        else
                            (report = detected.getReport()).writeToFile(side);
                    }
                    SYS_ERR.println(report.populateReport());
                    terminate();
                    System.exit(-1);
                });
    }
}
