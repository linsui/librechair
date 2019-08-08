package com.google.android.apps.nexuslauncher.smartspace;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.android.launcher3.Alarm;
import com.android.launcher3.LauncherModel;
import com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto.i;
import com.google.android.apps.nexuslauncher.utils.ActionIntentFilter;
import com.google.android.apps.nexuslauncher.utils.ProtoStore;
import java.io.PrintWriter;
import java.util.List;

public class SmartspaceController implements Handler.Callback {

    enum Store {
        WEATHER("smartspace_weather"),
        CURRENT("smartspace_current");

        final String filename;

        Store(final String filename) {
            this.filename = filename;
        }
    }

    private static SmartspaceController INSTANCE;
    private final SmartspaceDataContainer dataContainer;
    private final Alarm refreshAlarm;
    private ISmartspace mSmartspace;
    private final ProtoStore dT;
    private final Context mAppContext;
    private final Handler mUiHandler;
    private final Handler mWorker;

    public SmartspaceController(final Context mAppContext) {
        this.mWorker = new Handler(LauncherModel.getWorkerLooper(), this);
        this.mUiHandler = new Handler(Looper.getMainLooper(), this);
        this.mAppContext = mAppContext;
        this.dataContainer = new SmartspaceDataContainer();
        this.dT = new ProtoStore(mAppContext);
        (this.refreshAlarm = new Alarm()).setOnAlarmListener(alarm -> dc());
        this.updateGsa();
        mAppContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateGsa();
            }
        }, ActionIntentFilter.googleInstance(
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_CHANGED,
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_DATA_CLEARED));
    }

    private Intent getSmartspaceOptionsIntent() {
        return new Intent();
    }

    private void dc() {
        boolean weatherAvailable = this.dataContainer.isWeatherAvailable();
        boolean dataAvailable = this.dataContainer.isDataAvailable();
        this.dataContainer.clearAll();
        if (weatherAvailable && !this.dataContainer.isWeatherAvailable()) {
            this.updateSmartspaceStore(null, SmartspaceController.Store.WEATHER);
        }
        if (dataAvailable && !this.dataContainer.isDataAvailable()) {
            this.updateSmartspaceStore(null, SmartspaceController.Store.CURRENT);
        }
    }

    private void updateGsa() {
        if (this.mSmartspace != null) {
            this.mSmartspace.onGsaChanged();
        }
        this.onPostGsaUpdate();
    }

    private void onPostGsaUpdate() {
    }

    private void updateSmartspaceStore(final NewCardInfo a,
            final SmartspaceController.Store SmartspaceControllerStore) {
        Message.obtain(this.mWorker, 2, SmartspaceControllerStore.ordinal(), 0, a).sendToTarget();
    }

    public static SmartspaceController get(final Context context) {
        if (SmartspaceController.INSTANCE == null) {
            SmartspaceController.INSTANCE = new SmartspaceController(
                    context.getApplicationContext());
        }
        return SmartspaceController.INSTANCE;
    }

    private void update() {
        this.refreshAlarm.cancelAlarm();
        if (this.dataContainer.timeRemainingTillExpiry() > 0L) {
            this.refreshAlarm.setAlarm(this.dataContainer.timeRemainingTillExpiry());
        }
        if (this.mSmartspace != null) {
            this.mSmartspace.postUpdate(this.dataContainer);
        }
    }

    public void updateData(NewCardInfo cardInfo) {
        if (cardInfo != null && !cardInfo.forWeather) {
            this.updateSmartspaceStore(cardInfo, SmartspaceController.Store.WEATHER);
        } else {
            this.updateSmartspaceStore(cardInfo, SmartspaceController.Store.CURRENT);
        }
    }

    public void cW() {
        Message.obtain(this.mWorker, 1).sendToTarget();
    }

    public void dumpInfo(final String s, final PrintWriter printWriter) {
        printWriter.println();
        printWriter.println(s + "SmartspaceController");
        printWriter.println(s + "  weather " + this.dataContainer.weatherCard);
        printWriter.println(s + "  current " + this.dataContainer.dataCard);
    }

    public boolean cY() {
        boolean b = false;
        final List queryBroadcastReceivers = this.mAppContext.getPackageManager()
                .queryBroadcastReceivers(this.getSmartspaceOptionsIntent(), 0);
        if (queryBroadcastReceivers != null) {
            b = !queryBroadcastReceivers.isEmpty();
        }
        return b;
    }

    public void da(final ISmartspace ds) {
        this.mSmartspace = ds;
        if (this.mSmartspace != null && this.dataContainer != null) {
            this.mSmartspace.postUpdate(this.dataContainer);
            this.mSmartspace.onGsaChanged();
        }
    }

    public boolean handleMessage(final Message message) {
        SmartspaceCard dVar = null;
        switch (message.what) {
            case 1:
                i data = new i();
                SmartspaceCard weatherCard =
                        this.dT.dv(SmartspaceController.Store.WEATHER.filename, data) ?
                                SmartspaceCard.cD(this.mAppContext, data, true) :
                                null;

                data = new i();
                SmartspaceCard eventCard =
                        this.dT.dv(SmartspaceController.Store.CURRENT.filename, data) ?
                                SmartspaceCard.cD(this.mAppContext, data, false) :
                                null;

                Message.obtain(this.mUiHandler, 101, new SmartspaceCard[]{weatherCard, eventCard})
                        .sendToTarget();
                break;
            case 2:
                this.dT.dw(SmartspaceCard.cQ(this.mAppContext, (NewCardInfo) message.obj),
                        SmartspaceController.Store.values()[message.arg1].filename);
                Message.obtain(this.mUiHandler, 1).sendToTarget();
                break;
            case 101:
                SmartspaceCard[] dVarArr = (SmartspaceCard[]) message.obj;
                if (dVarArr != null) {
                    this.dataContainer.weatherCard = dVarArr.length > 0 ?
                            dVarArr[0] :
                            null;

                    SmartspaceDataContainer eVar = this.dataContainer;
                    if (dVarArr.length > 1) {
                        dVar = dVarArr[1];
                    }

                    eVar.dataCard = dVar;
                }
                this.dataContainer.clearAll();
                update();
                break;
        }
        return true;
    }
}
