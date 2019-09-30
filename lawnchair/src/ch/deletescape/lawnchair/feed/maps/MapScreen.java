/*
 * Copyright (c) 2019 oldosfan.
 * Copyright (c) 2019 the Lawnchair developers
 *
 *     This file is part of Librechair.
 *
 *     Librechair is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Librechair is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Librechair.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.feed.maps;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.launcher3.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.lang.reflect.InvocationTargetException;

import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.ProviderScreen;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.persistence.FeedPersistence;

public class MapScreen extends ProviderScreen {
    private final LauncherFeed feed;
    private final double lat;
    private final double lon;
    private final double zoom;
    private MapView mapView;
    private ViewGroup parent, layout;

    public MapScreen(Context base, LauncherFeed feed, double lat, double lon, double zoom) {
        super(base);
        this.feed = feed;
        this.lat = lat;
        this.lon = lon;
        this.zoom = zoom;
        addAction(new FeedProvider.Action(getDrawable(R.drawable.ic_open_in_browser_black_24dp),
                getString(
                        R.string.title_action_open_externally), () -> {
            try {
                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(
                                        "geo:" + mapView.getMapCenter().getLatitude() + "," + mapView.getMapCenter().getLongitude())).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    protected View getView(ViewGroup parent) {
        this.parent = parent;
        LinearLayout layout = new LinearLayout(parent.getContext()) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (feed != null) {
                    feed.getFeedController().setDisallowInterceptCurrentTouchEvent(true);
                }
                return super.onInterceptTouchEvent(ev);
            }
        };
        layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(mapView = getMapView());
        this.layout = layout;
        return layout;
    }

    private MapView getMapView() {
        MapView mapView = new MapView(parent.getContext());
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return mapView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bindView(View view) {
        try {
            //noinspection unchecked
            mapView.getTileProvider().setTileSource(MapProvider.inflate(
                    (Class<? extends MapProvider>) Class.forName(
                            FeedPersistence.Companion.getInstance(
                                    this).getMapProvider())).getTileSource());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        mapView.getController().animateTo(new GeoPoint(lat, lon));
        mapView.setClipToPadding(false);
        mapView.getController().zoomTo(zoom);
        mapView.getOverlayManager().add(new RotationGestureOverlay(mapView));
        mapView.getOverlayManager().add(new CopyrightOverlay(this));
    }

    @Override
    public void onPause() {
        layout.removeAllViews();
    }

    @Override
    public void onResume() {
        layout.addView(mapView = getMapView());
        bindView(null);
    }
}
