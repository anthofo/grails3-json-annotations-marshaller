package org.grails.json.annotations

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention( RetentionPolicy.RUNTIME )
public @interface JsonApi {
	String[] value() default []
}
