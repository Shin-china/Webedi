package customer.comm.tool;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.setSerializationInclusion(Include.NON_NULL);
    }

    private JsonUtils() {
    }

    // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串。
     * <p>
     * Title: pojoToJson
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param data
     * @return
     */
    public static String objectToJson(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        }
        catch (JsonProcessingException e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * 将json字符串转换成json对象。
     * <p>
     * Title: pojoToJson
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param jsonStr
     * @return JsonNode
     */
    public static JsonNode strToJson(String jsonStr) {
        try {
            return mapper.readTree(jsonStr);
        }
        catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * 将json结果集转化为对象
     *
     * @param jsonData json数据
     * @param clazz    对象中的object类型
     * @return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        }
        catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * 将json数据转换成pojo对象list
     * <p>
     * Title: jsonToList
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        }
        catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    /**
     * json字符串转换为类
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        try {
            T bean = (T) mapper.readValue(json, clazz);
            return bean;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> toBean(String json) {
        return toBean(json, HashMap.class);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> toBeanStr(String json) {
        return toBean(json, HashMap.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toBean(String json, TypeReference<T> tr) {
        try {
            T bean = (T) mapper.readValue(json, tr);
            return bean;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> toList(String json) {
        return toBean(json, ArrayList.class);
    }

    /**
     * 对象转换为Map
     */
    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        }
        catch (Exception e) {
            System.out.println("transBean2Map Error " + e);
        }
        return map;
    }

    /**
     * 对象转换为json字符串
     */
    public static String toJson(Object bean) throws IOException {
        String json = null;
        JsonGenerator gen = null;
        StringWriter sw = new StringWriter();
        try {
            gen = new JsonFactory().createGenerator(sw);
            mapper.writeValue(gen, bean);
            json = sw.toString();
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            try {
                if (gen != null) {
                    gen.close();
                }
                if (sw != null) {
                    sw.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /**
     * json字符串转换为List
     */
    public static <T> List<T> json2ListBean(String json, Class<T> cls) {

        List<T> list = JSONArray.parseArray(json, cls);
        return list;
    }

}
