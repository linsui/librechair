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

package ch.deletescape.lawnchair.feed;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.deletescape.lawnchair.LawnchairUtilsKt;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import io.github.cdimascio.essence.Essence;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import org.apache.commons.io.IOUtils;

public class ArticleViewerScreen extends ProviderScreen {

    private final String title;
    private final String categories;
    private final String url;
    private final String desc;

    public ArticleViewerScreen(Context base, String title, String categories, String url, String desc) {
        super(base);
        this.title = title;
        this.categories = categories;
        this.url = url;
        this.desc = desc;
    }

    @Override
    protected View getView(ViewGroup parent) {
        return LawnchairUtilsKt.inflate(parent, R.layout.overlay_article);
    }

    @Override
    protected void bindView(View articleView) {
        TextView titleView = articleView.findViewById(R.id.title);
        TextView contentView = articleView.findViewById(R.id.content);
        articleView.findViewById(R.id.open_externally)
                .setOnClickListener(v3 -> Utilities
                        .openURLinBrowser(this, url));
        titleView.setText(title);
        TextView categoriesView = articleView
                .findViewById(R.id.article_categories);
        categoriesView.setText(categories);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                URLConnection urlConnection = new URL(
                        url.replace("http://", "https://"))
                        .openConnection();
                if (urlConnection instanceof HttpURLConnection) {
                    ((HttpURLConnection) urlConnection)
                            .setInstanceFollowRedirects(true);
                }
                CharSequence text = Essence
                        .extract(IOUtils.toString(urlConnection
                                .getInputStream(), Charset
                                .defaultCharset())).getText();
                if (text.toString().trim().isEmpty()) {
                    text = Html
                            .fromHtml(desc, 0);
                }
                CharSequence finalText = text;
                contentView.post(() -> contentView.setText(finalText));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
