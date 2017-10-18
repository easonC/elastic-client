package easonc.elastic.client.annotation;

import java.lang.annotation.*;

/**
 * Created by zhuqi on 2016/11/18.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ElasticType {

    String name() default "";

    String idProperty();

}
