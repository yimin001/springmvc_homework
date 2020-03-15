package com.theodore.anno;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TheodoreAutowire {
    String value() default "";
}
