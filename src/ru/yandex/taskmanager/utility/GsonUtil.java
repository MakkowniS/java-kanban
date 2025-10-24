package ru.yandex.taskmanager.utility;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class GsonUtil {

    private GsonUtil() {
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Адаптер для сериализации Duration
        gsonBuilder.registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
            @Override
            public JsonElement serialize(Duration duration, Type type, JsonSerializationContext jsonSerializationContext) {
                return new JsonPrimitive(duration.toString());
            }
        });
        gsonBuilder.registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
            @Override
            public Duration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return Duration.parse(jsonElement.getAsString());
            }
        });

        // Адаптер для сериализации LocalDateTime
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
                return new JsonPrimitive(localDateTime.toString());
            }
        });
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
                    throws JsonParseException {
                return LocalDateTime.parse(jsonElement.getAsString());
            }
        });

        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();

        return gsonBuilder.create();
    }
}
