package ch.deletescape.lawnchair.feed.wikipedia.news;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class News {
    private static final Executor fetchExecutor = Executors.newSingleThreadExecutor();
    private static final List<Consumer<NewsItem>> onChangeListeners = new ArrayList<>();
    private static NewsItem currentItem;
    private static final String API_URL = "https://en.wikipedia.org/api/rest_v1/feed/featured/2496/04/01";

    static {
        requestRefresh();
    }

    public static synchronized void requestRefresh() {
        fetchExecutor.execute(() -> {
            try {
                URLConnection connection = new URL(API_URL).openConnection();
                JsonObject object = new JsonParser().parse(
                        IOUtils.toString(connection.getInputStream(),
                                Charset.defaultCharset())).getAsJsonObject().getAsJsonObject();
                JsonObject news = object.getAsJsonObject("news");
                JsonObject links = news.getAsJsonObject("links");
                NewsItem item = new NewsItem();
                item.contentUrl = links.getAsJsonObject("content_urls").getAsJsonObject(
                        "desktop").getAsJsonPrimitive("page").getAsString();
                item.thumbnail = links.getAsJsonObject("thumbnail").getAsJsonPrimitive(
                        "source").getAsString();
                item.lang = links.getAsJsonPrimitive("lang").getAsString();
                item.story = news.getAsJsonPrimitive("story").getAsString();
                currentItem = item;
                onChangeListeners.forEach(consumer -> consumer.accept(currentItem));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static synchronized void addListener(Consumer<NewsItem> consumer) {
        if (currentItem != null) {
            consumer.accept(currentItem);
        }
        onChangeListeners.add(consumer);
    }

    private News() {
        throw new RuntimeException("This class cannot be instantiated");
    }
}
