package com.github.nickid2018.chemistrylab.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ScriptAPI {

    String value();
}
