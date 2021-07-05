package com.github.nickid2018.chemistrylab.network;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Retention(CLASS)
@Target({ TYPE, METHOD, FIELD })
public @interface SideOnly {

	public NetworkSide value();
}
