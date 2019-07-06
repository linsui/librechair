/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package librechair.fonts.provider;

import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_FONT_NOT_FOUND;
import static android.support.v4.provider.FontsContractCompat.FontRequestCallback.FAIL_REASON_MALFORMED_QUERY;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat.FontRequestCallback;
import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class FontRequestHelper {

    public static final String GOOGLE_FONT_KEY = "AIzaSyD-tjZZSSDbq0ggoJMydogsGlpzcBTaxTw";

    public static void postFontRequest(Context context, FontRequest request,
            FontRequestCallback callback, Handler handler) {
        try {
            String[] split = request.getQuery().split("[&?]");
            HashMap<String, String> processedRequest = new HashMap<>();
            for (String iter : split) {
                processedRequest.put(iter.split("=")[0], iter.split("=")[1]);
            }
            postFontRequest(context, processedRequest, callback, handler, request.getQuery());
        } catch (IndexOutOfBoundsException e) {
            callback.onTypefaceRequestFailed(FAIL_REASON_MALFORMED_QUERY);
        }
    }

    public static void postFontRequest(Context context, Map<String, String> request,
            FontRequestCallback callback, Handler handler, String originalRequest) {

        Handler handler1 = new Handler();
        handler.post(() -> {
            try {
                if (request.get("name").trim().equals("Google Sans")) {
                    Log.d(FontRequestHelper.class.getName(), "postFontRequest: Requesting Google Sans! Deferred to the Google Sans font provider");
                    Typeface typeface = getGoogleSans(context, request, originalRequest);
                    handler1.post(() -> callback.onTypefaceRetrieved(typeface));
                    return;
                }
            } catch (NullPointerException | IOException e) {
                callback.onTypefaceRequestFailed(FAIL_REASON_MALFORMED_QUERY);
            }
            Log.d(FontRequestHelper.class.getName(), "postFontRequest: request executed: " + request.toString());
            String apiCall = String.format("https://www.googleapis.com/webfonts/v1/webfonts?key=%s",
                    GOOGLE_FONT_KEY);
            Log.d(FontRequestHelper.class.getName(), "postFontRequest: API call will be: " + apiCall);
            File cachedTypeface = new File(context.getCacheDir(),
                    "typeface_google_" + originalRequest.hashCode() + "_cached.ttf");
            Log.d(FontRequestHelper.class.getName(), "postFontRequest: cached path " + cachedTypeface.getAbsolutePath());
            if (cachedTypeface.exists()) {
                Log.d(FontRequestHelper.class.getName(), "postFontRequest: cached font exists! returning that");
                handler1.post(() -> callback.onTypefaceRetrieved(Typeface.createFromFile(cachedTypeface)));
                return;
            }
            try {
                Log.d(FontRequestHelper.class.getName(), "postFontRequest: cached font does not exist. we need to download it");
                String rawJson = IOUtils
                        .toString(new URL(apiCall).openConnection().getInputStream(),
                                Charset.defaultCharset());
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(rawJson).getAsJsonObject();
                JsonArray fontItems = jsonObject.getAsJsonArray("items");
                for (JsonElement object : fontItems) {
                    JsonArray variants = object.getAsJsonObject().getAsJsonArray("variants");
                    JsonObject files = object.getAsJsonObject().getAsJsonObject("files");
                    if (object.getAsJsonObject().getAsJsonPrimitive("family").getAsString()
                            .equalsIgnoreCase(request.get("name").trim()) && variants
                            .contains(new JsonPrimitive(request.get("weight").replaceAll("400", "regular") + (request.get("italic").equals(1) ? "italic" : "")))) {
                        IOUtils.copy(
                                new URL(files.getAsJsonPrimitive(request.get("weight").replaceAll("400", "regular") + (request.get("italic").equals(1) ? "italic" : "")).toString().replaceAll("\"", "").replaceAll("http", "https"))
                                        .openConnection().getInputStream(),
                                new FileOutputStream(cachedTypeface));
                        handler1.post(() -> callback.onTypefaceRetrieved(Typeface.createFromFile(cachedTypeface)));
                        return;
                    }
                }
                Log.d(FontRequestHelper.class.getName(), "postFontRequest: no such font found! signaling error");
                handler1.post(() -> callback.onTypefaceRequestFailed(FAIL_REASON_FONT_NOT_FOUND));

            } catch (IOException | NullPointerException | JsonParseException e) {
                e.printStackTrace();
                handler1.post(() -> callback.onTypefaceRequestFailed(FAIL_REASON_FONT_LOAD_ERROR));
            }
        });
    }

    private static Typeface getGoogleSans(Context context, Map<String, String> request, String originalRequest)
            throws NullPointerException, NumberFormatException, IOException {
        File cachedTypeface = new File(context.getCacheDir(),
                "typeface_google_" + originalRequest.hashCode() + "_cached.ttf");
        if (cachedTypeface.exists()) {
            Log.d(FontRequestHelper.class.getName(), "getGoogleSans: cached font exists! returning that");
            return Typeface.createFromFile(cachedTypeface);
        }
        String requestURL = String.format("https://fonts.googleapis.com/css?family=Google+Sans:" + request.get("weight") + (request.get("italic").equals("1") ? ":italic" : ""));
        Log.v(FontRequestHelper.class.getName(), "getGoogleSans: requesting Google Sans from " + requestURL);
        String response = IOUtils.toString(new URL(requestURL).openConnection().getInputStream(), Charset.defaultCharset());
        Pattern regexQuery = Pattern.compile("https://fonts.gstatic.com/s/googlesans/.+.ttf");
        Log.v(FontRequestHelper.class.getName(), "getGoogleSans: Google responded to request with" + response);
        Matcher matcher = regexQuery.matcher(response);
        if (matcher.find()) {
            URL url = new URL(response.substring(matcher.start(), matcher.end()));
            Log.v(FontRequestHelper.class.getName(), "getGoogleSans: found Google Sans URL at " + url.toString());
            FileOutputStream outputStream = new FileOutputStream(cachedTypeface);
            IOUtils.copy(url.openConnection().getInputStream(), outputStream);
            return Typeface.createFromFile(cachedTypeface);
        } else {
            Log.v(FontRequestHelper.class.getName(), "getGoogleSans: couldn't retrieve Google Sans! Falling back to system font (which is probably Roboto)");
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                return Typeface.create(Typeface.DEFAULT, Integer.valueOf(request.get("weight")),
                        request.get("italic").equals(1));
            } else {
                return Typeface.DEFAULT;
            }
        }
    }
}
