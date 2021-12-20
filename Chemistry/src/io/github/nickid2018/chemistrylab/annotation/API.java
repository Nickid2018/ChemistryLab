package io.github.nickid2018.chemistrylab.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface API {

    String since() default "0.0.1";
}
