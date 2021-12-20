package io.github.nickid2018.chemistrylab;

import io.github.nickid2018.chemistrylab.util.Version;
import io.github.nickid2018.chemistrylab.util.VersionType;

import java.util.Random;

public class Constants {

    public static final Version VERSION = new Version(1, 0, 0, VersionType.IN_DEVELOP);
    public static final String VERSION_IN_STRING = VERSION.toString();

    public static final String lineSeparator = System.getProperty("line.separator");

    public static final Random RANDOM = new Random();
}
