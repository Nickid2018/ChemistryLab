package io.github.nickid2018.chemistrylab.network.handler;

import java.io.Serial;

public class SkippedPacketException extends Exception {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1145763172313456857L;

    public SkippedPacketException(Throwable e) {
        super(e);
    }
}
