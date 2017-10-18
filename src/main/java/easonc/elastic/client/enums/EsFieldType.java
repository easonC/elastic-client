package easonc.elastic.client.enums;

/**
 * Created by zhuqi on 2016/11/17.
 */
public enum EsFieldType {

    None("none"),
    GeoPoint("geo_point"),
    GeoShape("geo_shape"),
    Attachment("attachment"),
    Ip("ip"),
    Binary("binary"),
    String("string"),
    Integer("integer"),
    Long("long"),
    Short("short"),
    Byte("byte"),
    Float("float"),
    Double("double"),
    Date("date"),
    Boolean("boolean"),
    Completion("completion"),
    Nested("nested"),
    Object("object"),
    Murmur3Hash("murmur3");

    private final String fieldString;

    private EsFieldType(final String fieldString) {
        this.fieldString = fieldString;
    }

    @Override
    public String toString() {
        return fieldString;
    }
}
