package easonc.elastic.client;

import easonc.elastic.client.exceptions.EntityReflectionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuqi on 2016/11/17.
 */
public final class EntityReflectManager<T> {
    private Class<T> clazz = null;
    private List<Field> fields = null;

    public EntityReflectManager(Class<T> clazz) throws EntityReflectionException {
        this.clazz = clazz;
        getFields();
    }

    private void getFields() throws EntityReflectionException {
        Field[] allFields = clazz.getDeclaredFields();
        if (null == allFields || allFields.length == 0)
            throw new EntityReflectionException("The class [" + clazz.getName() + "] has no fields.");
        this.fields = new ArrayList<>(allFields.length);
        for (Field f : allFields) {
            this.fields.add(f);
            f.setAccessible(true);
        }
    }

    public List<Field> getField() {
        return this.fields;
    }

    public Map<String,Field> getFieldMap() {

        Map<String, Field> fieldsMap = new HashMap<String, Field>();
        for (Field field: fields) {
            String columnName = field.getName();
            if(!fieldsMap.containsKey(columnName))
                fieldsMap.put(columnName, field);
        }
        return fieldsMap;
    }

    public <TAnno extends Annotation> Map<String, TAnno> getAnnotations(Class<TAnno> clazz) {
        Map<String, TAnno> map = new HashMap<>();
        TAnno anno;
        for (Field f : this.fields) {
            anno = f.getAnnotation(clazz);
            if (anno == null) {
                continue;
            }
            map.put(f.getName(), anno);
        }
        return map;
    }

    public <TAnno extends Annotation> TAnno getClassAnnotations(Class<TAnno> clazz) {
        TAnno anno;
        anno = this.clazz.getAnnotation(clazz);
        return anno;
    }
}
