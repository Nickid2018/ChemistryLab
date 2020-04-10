package com.github.nickid2018.chemistrylab.mod;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mod {

	String modid();

	int priority() default 0;

	String name() default "";

	String[] author() default {};

	String credit() default "";

	String notice() default "";

	String license() default "";

	String version() default "1.0.0";

	String describe() default "";

	String updataURL() default "";

	String acceptVersion() default "[0.0.0,99.99.9999]";

	String officialWebsite() default "";
}
