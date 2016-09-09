/*
 * ApplicationDeserializer.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.internal.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class ApplicationDeserializer extends GsonDeserializer<List<Connection>> {

    @Override
    public List<Connection> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        assertJsonObject(json);

        final JsonObject object = json.getAsJsonObject();

        requiredValue("id", String.class, object, context);
        requiredValue("tenant", String.class, object, context);
        requiredValue("authorize", String.class, object, context);
        requiredValue("callback", String.class, object, context);

        Type strategyType = new TypeToken<List<Strategy>>() {}.getType();
        requiredValue("strategies", strategyType, object, context);
        List<Strategy> strategies = context.deserialize(object.remove("strategies"), strategyType);
        List<Connection> connections = new ArrayList<>();
        for (Strategy data : strategies) {
            connections.addAll(data.getConnections());
        }

        return connections;
    }
}
