package com.github.nickid2018.chemistrylab.network.handler;

public class SkippedPacketException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -1145763172313456857L;

    public SkippedPacketException(Throwable e) {
        super(e);
    }
}
