package io.github.nickid2018.chemistrylab.network;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Documented
@Retention(CLASS)
@Target({TYPE, METHOD, FIELD})
public @interface SideOnly {

    NetworkSide value();
}
