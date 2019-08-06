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
import ch.deletescape.lawnchair.feed.IFeedProvider.Stub;
import ch.deletescape.lawnchair.feed.RemoteCard.Types;
import com.android.launcher3.R;
import java.util.Collections;
import java.util.List;

public class RemoteDemoService extends Service {

    private IBinder binder;

    public RemoteDemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder != null ? binder : (binder = new Stub() {
            @Override
            public List<RemoteCard> getCards() throws RemoteException {
                return Collections.singletonList(new RemoteCard(null, "feed provider demo",
                        new RemoteInflateHelper.Stub() {
                            @Override
                            public RemoteViews inflate(boolean darkText) throws RemoteException {
                                return new RemoteViews(getApplicationContext().getPackageName(),
                                        R.layout.appwidget_error);
                            }
                        }, Types.INSTANCE.getRAISE(), "nosort,top", "remoteFeedDemo".hashCode()));
            }
        });
    }
}
