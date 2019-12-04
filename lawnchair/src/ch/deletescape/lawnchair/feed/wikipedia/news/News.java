package ch.deletescape.lawnchair.feed.wikipedia.news;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class News {
    private static final Executor fetchExecutor = Executors.newSingleThreadExecutor();
    private static final List<Consumer<List<NewsItem>>> onChangeListeners = new ArrayList<>();
    private static volatile List<NewsItem> currentItem;
    private static final String API_URL = "https://en.wikipedia.org/api/rest_v1/feed/featured/2496/04/01";

    static {
        requestRefresh();
    }

    private static synchronized void requestRefresh() {
        fetchExecutor.execute(() -> {
            try {
                URLConnection connection = new URL(API_URL).openConnection();
                JsonObject object = new JsonParser().parse(
                        IOUtils.toString(connection.getInputStream(),
                                Charset.defaultCharset())).getAsJsonObject().getAsJsonObject();
                JsonArray news = object.getAsJsonArray("news");
                ArrayList<NewsItem> list = new ArrayList<>();
                news.iterator().forEachRemaining(jsonElement -> {
                    JsonObject newsItem = jsonElement.getAsJsonObject();
                    JsonObject links = newsItem.getAsJsonArray("links").get(0).getAsJsonObject();
                    NewsItem item = new NewsItem();
                    item.title = links.getAsJsonPrimitive("displaytitle").getAsString();
                    item.contentUrl = links.getAsJsonObject("content_urls").getAsJsonObject(
                            "desktop").getAsJsonPrimitive("page").getAsString();
                    try {
                        item.thumbnail = links.getAsJsonObject("thumbnail").getAsJsonPrimitive(
                                "source").getAsString();
                    } catch (NullPointerException e) {
                        item.thumbnail = null;
                    }
                    try {
                        item.dt = DateFormat.getDateInstance().parse(
                                links.getAsJsonPrimitive("timestamp").getAsString());
                    } catch (ParseException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    item.lang = links.getAsJsonPrimitive("lang").getAsString();
                    item.story = newsItem.getAsJsonPrimitive("story").getAsString();
                    list.add(item);
                });
                currentItem = list;
                onChangeListeners.forEach(consumer -> consumer.accept(currentItem));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public static synchronized List<NewsItem> requireEntries() {
        if (currentItem != null) {
            return currentItem;
        } else {
            requestRefresh();
            //noinspection StatementWithEmptyBody
            while (currentItem == null);
            return currentItem;
        }
    }

    public static synchronized void addListener(Consumer<List<NewsItem>> consumer) {
        if (currentItem != null) {
            consumer.accept(currentItem);
        }
        onChangeListeners.add(consumer);
    }

    private News() {
        throw new RuntimeException("This class cannot be instantiated");
    }
}
