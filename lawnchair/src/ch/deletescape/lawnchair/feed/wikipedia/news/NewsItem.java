package ch.deletescape.lawnchair.feed.wikipedia.news;

import java.util.Date;
import java.util.Objects;

public class NewsItem {
    public String title;
    public String thumbnail;
    public String lang;
    public String contentUrl;
    public String story;
    public Date dt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewsItem)) return false;
        NewsItem newsItem = (NewsItem) o;
        return Objects.equals(title, newsItem.title) &&
                Objects.equals(thumbnail, newsItem.thumbnail) &&
                Objects.equals(lang, newsItem.lang) &&
                Objects.equals(contentUrl, newsItem.contentUrl) &&
                Objects.equals(story, newsItem.story) &&
                Objects.equals(dt, newsItem.dt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, thumbnail, lang, contentUrl, story, dt);
    }
}
