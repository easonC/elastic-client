package easonc.elastic.client.annotation;

import easonc.elastic.client.enums.EsFieldType;
import easonc.elastic.client.enums.FieldIndexOption;
import easonc.elastic.client.enums.TermVectorOption;

import java.lang.annotation.*;

/**
 * Created by zhuqi on 2016/11/17.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ElasticProperty {
    EsFieldType fieldType();

    TermVectorOption TermVector() default TermVectorOption.No;

    FieldIndexOption index() default FieldIndexOption.No;

    double boost() default 1;

    String analyzer() default "";

    String indexAnalyzer() default "";

    String searchAnalyzer() default "";

    String sortAnalyzer() default "";

    String nullValue() default "";

    String Similarity() default "";

    boolean docValues() default false;

    boolean omitNorms() default false;

    boolean omitTermFrequencyAndPositions() default false;

    boolean includeInAll() default true;

    boolean includeInParent() default false;

    boolean store() default false;

    String dateFormat() default "date_time";
}
