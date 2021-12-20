package io.github.nickid2018.chemistrylab.util;

public enum VersionType {

    IN_DEVELOP("indev"), ALPHA("alpha"), BETA("beta"), TRIAL("trial"), STABLE("stable");

    private final String name;

    VersionType(String name) {
        this.name = name;
    }

    public static VersionType fromString(String type) {
        return switch (type.toLowerCase()) {
            case "indev" -> IN_DEVELOP;
            case "alpha" -> ALPHA;
            case "beta" -> BETA;
            case "trial" -> TRIAL;
            default -> STABLE;
        };
    }

    public String getName() {
        return name;
    }
}
