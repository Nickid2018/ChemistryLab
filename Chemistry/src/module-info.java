module com.github.chemistrylab {

    requires java.scripting;
    requires java.management;
    requires org.openjdk.nashorn;

    requires jsr305;

    requires io.netty.all;

    requires com.google.gson;
    requires com.google.common;

    requires org.apache.commons.io;
    requires org.apache.logging.log4j;
    requires commons.lang;

    requires com.github.oshi;

    requires org.lwjgl;
    requires org.lwjgl.stb;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.lwjgl.openal;

    requires it.unimi.dsi.fastutil;
    requires jdk.dynalink;

    exports com.github.nickid2018.chemistrylab.crash;
    exports com.github.nickid2018.chemistrylab.mod.javascript;
    exports com.github.nickid2018.chemistrylab.server;
}