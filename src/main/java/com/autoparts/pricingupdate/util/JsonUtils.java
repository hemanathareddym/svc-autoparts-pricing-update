package com.autoparts.pricingupdate.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static ObjectNode mergeObjectNodes(ObjectNode node1, ObjectNode node2) {
        node2.fieldNames().forEachRemaining((fieldName) -> {
            node1.replace(fieldName, node2.get(fieldName));
        });
        return node1;
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ObjectNode valueToTree(Object fromValue) {
        return (ObjectNode)mapper.valueToTree(fromValue);
    }

    public static String writeValueAsString(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    public static Map parse(String input) throws Exception {
        JsonNode n = mapper.readTree(input);
        return (Map)mapper.convertValue(n, Map.class);
    }

    public static Map convert(JsonNode node) throws Exception {
        return (Map)mapper.convertValue(node, Map.class);
    }

    public static Collection<Integer> getIntegerValues(Map mappedObject, String expression) throws Exception {
        Object obj = resolve(mappedObject, expression);
        ArrayList res = new ArrayList();
        if(obj != null) {
            if(obj instanceof Integer) {
                res.add((Integer)obj);
            } else if(obj instanceof String) {
                res.add(Integer.valueOf(Integer.parseInt((String)obj)));
            } else if(obj instanceof List) {
                Iterator var4 = ((List)obj).iterator();

                while(var4.hasNext()) {
                    Object v = var4.next();
                    if(v instanceof Integer) {
                        res.add((Integer)v);
                    }

                    if(v instanceof String) {
                        res.add(Integer.valueOf(Integer.parseInt((String)v)));
                    }
                }
            }
        }

        return res;
    }

    public static Collection<String> getStringValues(Map mappedObject, String expression) throws Exception {
        Object obj = resolve(mappedObject, expression);
        ArrayList res = new ArrayList();
        if(obj != null) {
            if(obj instanceof List) {
                Iterator var4 = ((List)obj).iterator();

                while(var4.hasNext()) {
                    Object v = var4.next();
                    res.add(v.toString());
                }
            } else {
                res.add(obj.toString());
            }
        }

        return res;
    }

    public static String getString(Map mappedObject, String expression) {
        try {
            Object obj = resolve(mappedObject, expression);
            if(obj != null) {
                if(!(obj instanceof List)) {
                    return obj.toString();
                }

                Iterator it = ((List)obj).iterator();
                if(it.hasNext()) {
                    return it.next().toString();
                }
            }

            return null;
        } catch (Exception var4) {
            return null;
        }
    }

    private static Object resolve(Map mappedObject, String expression) throws Exception {
        try {
            String e = adopt(expression);
            return JsonPath.read(mappedObject, e, new Predicate[0]);
        } catch (PathNotFoundException var3) {
            return null;
        }
    }

    private static String adopt(String val) {
        return val != null && !val.startsWith("$")?"$.." + val:val;
    }
}
