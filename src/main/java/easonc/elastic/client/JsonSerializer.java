package easonc.elastic.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonSerializer {

    private static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);





    public static <T> String toJson(T obj) {
        String string = null;
        try {
            string = mapper.writeValueAsString(obj);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static <T> T toObject(String json, Class<T> cls) {

        if (Strings.isWhitespaceOrNull(json))
            return null;

        try {
            T obj = mapper.readValue(json, cls);

            return obj;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T toObject(String json, TypeReference valueTypeRef) {

        if (Strings.isWhitespaceOrNull(json))
            return null;

        try {
            T obj = mapper.readValue(json, valueTypeRef);

            return obj;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }





    //TypeReference valueTypeRef
}
