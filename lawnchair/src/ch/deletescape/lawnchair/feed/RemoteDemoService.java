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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.launcher3.R;

import java.util.Collections;
import java.util.List;

import ch.deletescape.lawnchair.LawnchairUtilsKt;
import ch.deletescape.lawnchair.feed.IFeedProvider.Stub;
import ch.deletescape.lawnchair.feed.RemoteCard.Types;
import ch.deletescape.lawnchair.util.IRunnable;

public class RemoteDemoService extends Service {

    private IBinder binder;

    public RemoteDemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder != null ? binder : (binder = new Stub() {
            @Override
            public List<RemoteCard> getCards() throws RemoteException {
                RemoteCard demoCard = new RemoteCard(null, "feed provider demo",
                        new RemoteInflateHelper.Stub() {
                            @Override
                            public RemoteViews inflate(boolean darkText) throws RemoteException {
                                RemoteViews views = new RemoteViews(
                                        getApplicationContext().getPackageName(),
                                        R.layout.appwidget_error);
                                views.setTextViewText(R.id.appwidget_error,
                                        "This is a demonstration feed provider that you should not pay attention to. It serves to demonstrate the remote feed provider API.");
                                return views;
                            }
                        }, Types.INSTANCE.getRAISE(), "nosort,top", "remoteFeedDemo".hashCode());
                demoCard.setActionOnCardActionSelectedListener(
                        new RemoteOnCardActionSelectedListener.Stub() {
                            @Override
                            public void onAction() throws RemoteException {
                                Toast.makeText(getApplicationContext(),
                                        "demonstration action clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                demoCard.setActionName("Action demo");
                demoCard.setCanHide(true);
                return Collections.singletonList(demoCard);
            }

            @Override
            public List<RemoteAction> getActions(boolean exclusive) throws RemoteException {
                return Collections.singletonList(new RemoteAction("Demo", LawnchairUtilsKt.drawableToBitmap(
                        getDrawable(R.drawable.ic_smartspace_preferences)),
                        new IRunnable.Stub() {
                            @Override
                            public void run() throws RemoteException {
                                Toast.makeText(RemoteDemoService.this, "works for me", Toast.LENGTH_SHORT).show();
                            }
                        }));
            }
        });
    }
}
