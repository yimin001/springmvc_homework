package com.theodore.anno;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TheodoreRequestMapping {
    String value() default "";
}
