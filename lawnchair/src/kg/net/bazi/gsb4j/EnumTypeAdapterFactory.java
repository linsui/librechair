/*
 * Copyright 2018 Azilet B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kg.net.bazi.gsb4j;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Type adapter factory that creates type adapters for enums. Type adapters of this factory serialize and deserialize
 * enum values as is, i.e. there is no case change.
 *
 * @author azilet
 */
class EnumTypeAdapterFactory implements TypeAdapterFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnumTypeAdapterFactory.class);

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (!rawType.isEnum()) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value != null) {
                    out.value(value.toString());
                } else {
                    out.nullValue();
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                String str = in.nextString();
                for (T item : rawType.getEnumConstants()) {
                    if (item.toString().equals(str)) {
                        return item;
                    }
                }
                LOGGER.warn("Unknown enum name for {}: {}", rawType.getSimpleName(), str);
                return null;
            }
        };
    }

}
