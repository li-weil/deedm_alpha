package com.deedm.validation;

import com.deedm.config.InputValidationProperties;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class RequestPayloadValidator {

    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
        String.class,
        Boolean.class,
        Byte.class,
        Short.class,
        Integer.class,
        Long.class,
        Float.class,
        Double.class,
        Character.class
    );

    private final InputValidationProperties properties;

    public RequestPayloadValidator(InputValidationProperties properties) {
        this.properties = properties;
    }

    public void validate(Object payload) {
        if (!properties.isEnabled() || payload == null) {
            return;
        }
        IdentityHashMap<Object, Boolean> visited = new IdentityHashMap<>();
        validateValue(payload, "requestBody", 0, visited);
    }

    private void validateValue(Object value, String path, int depth, IdentityHashMap<Object, Boolean> visited) {
        if (value == null) {
            return;
        }
        if (depth > properties.getMaxObjectDepth()) {
            throw new RequestPayloadValidationException(path + " exceeds maximum nesting depth");
        }

        Class<?> type = value.getClass();
        if (isSimpleType(type)) {
            if (value instanceof String stringValue) {
                validateString(path, stringValue);
            }
            return;
        }

        if (type.isEnum()) {
            return;
        }

        if (visited.containsKey(value)) {
            return;
        }
        visited.put(value, Boolean.TRUE);

        if (value instanceof Map<?, ?> mapValue) {
            validateMap(path, mapValue, depth, visited);
            return;
        }

        if (value instanceof Collection<?> collectionValue) {
            validateCollection(path, collectionValue, depth, visited);
            return;
        }

        if (type.isArray()) {
            validateArray(path, value, depth, visited);
            return;
        }

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(value);
                validateValue(fieldValue, path + "." + field.getName(), depth + 1, visited);
            } catch (IllegalAccessException e) {
                throw new RequestPayloadValidationException("Cannot inspect field " + path + "." + field.getName());
            }
        }
    }

    private void validateMap(String path, Map<?, ?> mapValue, int depth, IdentityHashMap<Object, Boolean> visited) {
        if (mapValue.size() > properties.getMaxMapEntries()) {
            throw new RequestPayloadValidationException(path + " contains too many entries");
        }
        for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
            if (entry.getKey() instanceof String key) {
                validateString(path + ".key", key);
            }
            validateValue(entry.getValue(), path + "[" + String.valueOf(entry.getKey()) + "]", depth + 1, visited);
        }
    }

    private void validateCollection(String path, Collection<?> collectionValue, int depth,
                                    IdentityHashMap<Object, Boolean> visited) {
        if (collectionValue.size() > properties.getMaxCollectionSize()) {
            throw new RequestPayloadValidationException(path + " contains too many elements");
        }
        int index = 0;
        for (Object element : collectionValue) {
            validateValue(element, path + "[" + index + "]", depth + 1, visited);
            index++;
        }
    }

    private void validateArray(String path, Object arrayValue, int depth, IdentityHashMap<Object, Boolean> visited) {
        int length = Array.getLength(arrayValue);
        if (length > properties.getMaxCollectionSize()) {
            throw new RequestPayloadValidationException(path + " contains too many elements");
        }
        for (int i = 0; i < length; i++) {
            validateValue(Array.get(arrayValue, i), path + "[" + i + "]", depth + 1, visited);
        }
    }

    private void validateString(String path, String value) {
        int maxLength = resolveMaxLength(path);
        if (value.length() > maxLength) {
            throw new RequestPayloadValidationException(path + " length exceeds limit " + maxLength);
        }
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (isForbiddenControlCharacter(current)) {
                throw new RequestPayloadValidationException(path + " contains illegal control characters");
            }
        }
    }

    private int resolveMaxLength(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.contains("filename")) {
            return properties.getFilenameMaxLength();
        }
        if (lowerPath.contains("formula")
            || lowerPath.contains("expression")
            || lowerPath.contains("relation")
            || lowerPath.contains("function")
            || lowerPath.contains("edges")
            || lowerPath.contains("nodes")
            || lowerPath.contains("premises")
            || lowerPath.contains("consequent")
            || lowerPath.contains("set")
            || lowerPath.contains("leaf")) {
            return properties.getExtendedMaxStringLength();
        }
        return properties.getDefaultMaxStringLength();
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
            || SIMPLE_TYPES.contains(type)
            || Number.class.isAssignableFrom(type)
            || Temporal.class.isAssignableFrom(type);
    }

    private boolean isForbiddenControlCharacter(char current) {
        return (current < 32 && current != '\n' && current != '\r' && current != '\t') || current == 127;
    }
}
