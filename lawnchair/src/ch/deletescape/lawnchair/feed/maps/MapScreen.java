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
import android.location.Location;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.launcher3.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import ch.deletescape.lawnchair.feed.FeedProvider;
import ch.deletescape.lawnchair.feed.ProviderScreen;
import ch.deletescape.lawnchair.feed.impl.LauncherFeed;
import ch.deletescape.lawnchair.location.LocationManager;
import ch.deletescape.lawnchair.persistence.FeedPersistence;
import io.reactivex.rxjava3.core.Single;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MapScreen extends ProviderScreen {
    private final LauncherFeed feed;

    private GeoPoint toLocation;
    private GeoPoint fromLocation;
    private double lat;
    private double lon;
    private double zoom;
    private Double displayLat;
    private Double displayLon;
    private MapView mapView;
    private ViewGroup parent, layout;
    private IMyLocationProvider provider;
    private Polyline route;

    private final Object ROUTE_LOCK;

    public MapScreen(Context base, LauncherFeed feed, double lat, double lon, double zoom) {
        super(base);
        this.ROUTE_LOCK = new Object();
        this.feed = feed;
        this.lat = lat;
        this.lon = lon;
        this.zoom = zoom;
        this.provider = new IMyLocationProvider() {
            private IMyLocationConsumer myLocationConsumer;
            private Function2<Double, Double, Unit> changeListener = (lati, longi) -> {
                Location location = new Location("");
                location.setLatitude(lati);
                location.setLongitude(longi);
                myLocationConsumer.onLocationChanged(location, this);

                if (toLocation != null && fromLocation != null) {
                    Single.fromCallable(() -> {
                        OSRMRoadManager roadManager = new OSRMRoadManager(MapScreen.this);
                        return roadManager.getRoad((ArrayList<GeoPoint>) Arrays.asList(toLocation,
                                fromLocation));
                    }).subscribe(
                            (road, throwable) -> {
                                if (throwable != null) {
                                    if (route != null) {
                                        synchronized (ROUTE_LOCK) {
                                            mapView.getOverlayManager().remove(route);
                                            route = RoadManager.buildRoadOverlay(road);
                                            mapView.getOverlayManager().add(route);
                                        }
                                    }
                                }
                            });
                }

                return Unit.INSTANCE;
            };

            @Override
            public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
                this.myLocationConsumer = myLocationConsumer;
                LocationManager.INSTANCE.addCallback(changeListener);
                return true;
            }

            @Override
            public void stopLocationProvider() {
                LocationManager.INSTANCE.getChangeCallbacks().remove(myLocationConsumer);
            }

            @Override
            public Location getLastKnownLocation() {
                if (LocationManager.INSTANCE.getLocation() == null) {
                    return null;
                } else {
                    Pair<Double, Double> loc = LocationManager.INSTANCE.getLocation();
                    Location location = new Location("");
                    location.setLatitude(loc.getFirst());
                    location.setLongitude(loc.getSecond());
                    return location;
                }
            }

            @Override
            public void destroy() {

            }
        };
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

    public MapScreen(Context base, LauncherFeed feed, double lat, double lon, double zoom,
                     double displayLat, double displayLon) {
        this(base, feed, lat, lon, zoom);
        this.displayLat = displayLat;
        this.displayLon = displayLon;
    }

    public MapScreen(Context base, LauncherFeed feed, double lat, double lon, double zoom,
                     double displayLat, double displayLon, GeoPoint toLocation,
                     GeoPoint fromLocation) {
        this(base, feed, lat, lon, zoom, displayLat, displayLon);
        this.toLocation = toLocation;
        this.fromLocation = fromLocation;
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
        MyLocationNewOverlay overlay = new MyLocationNewOverlay(provider, mapView);
        overlay.enableMyLocation();
        synchronized (ROUTE_LOCK) {
            if (route != null &&
                    !mapView.getOverlayManager().contains(route)) {
                mapView.getOverlayManager().add(route);
            }
        }
        mapView.getOverlayManager().add(overlay);
        if (displayLat != null && displayLon != null) {
            SimpleFastPointOverlay pointOverlay = new SimpleFastPointOverlay(
                    new SimpleFastPointOverlay.PointAdapter() {
                        @Override
                        public int size() {
                            return 1;
                        }

                        @Override
                        public IGeoPoint get(int i) {
                            return new GeoPoint(displayLat, displayLon);
                        }

                        @Override
                        public boolean isLabelled() {
                            return false;
                        }

                        @Override
                        public boolean isStyled() {
                            return false;
                        }

                        @NonNull
                        @Override
                        public Iterator<IGeoPoint> iterator() {
                            return Collections.singletonList(
                                    (IGeoPoint) new GeoPoint(displayLat, displayLon)).iterator();
                        }
                    });
            mapView.getOverlayManager().add(pointOverlay);
        }
        mapView.getController().animateTo(new GeoPoint(lat, lon));
        mapView.setClipToPadding(false);
        mapView.getController().zoomTo(zoom);
        mapView.getOverlayManager().add(new RotationGestureOverlay(mapView));
        mapView.getOverlayManager().add(new CopyrightOverlay(this));
        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                lat = mapView.getMapCenter().getLatitude();
                lon = mapView.getMapCenter().getLongitude();
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                zoom = mapView.getZoomLevelDouble();
                return false;
            }
        });
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
