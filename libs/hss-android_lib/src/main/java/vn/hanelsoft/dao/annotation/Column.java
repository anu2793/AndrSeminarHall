package vn.hanelsoft.dao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    boolean unique() default false;
    boolean notNull() default false;
}