package com.github.nickid2018.chemistrylab.util;

public enum VersionType {

    IN_DEVELOP("indev"), ALPHA("alpha"), BETA("beta"), TRIAL("trial"), STABLE("stable");

    private final String name;

    VersionType(String name) {
        this.name = name;
    }

    public static VersionType fromString(String type) {
        switch (type.toLowerCase()) {
            case "indev":
                return IN_DEVELOP;
            case "alpha":
                return ALPHA;
            case "beta":
                return BETA;
            case "trial":
                return TRIAL;
        }
        return STABLE;
    }

    public String getName() {
        return name;
    }
}
