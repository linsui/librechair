package amirz.aidlbridge

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import ch.deletescape.lawnchair.feed.FeedAdapter
import ch.deletescape.lawnchair.feed.getFeedController
import ch.deletescape.lawnchair.theme.ThemeManager
import com.android.launcher3.R
import com.google.android.libraries.launcherclient.ILauncherOverlay
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback

class LauncherFeed(contex2t: Context) : ILauncherOverlay.Stub() {

    private val dark: Boolean = ThemeManager.getInstance(contex2t.applicationContext).isDark
    private val context = ContextThemeWrapper(contex2t,
                                              if (dark) R.style.SettingsTheme_V2_Dark else R.style.SettingsTheme_V2)
    private val adapter by lazy {
        FeedAdapter(getFeedController(context).getProviders(), ThemeManager.getInstance(context))
    }
    private val handler = Handler(Looper.getMainLooper())
    private val windowService = context.getSystemService(WindowManager::class.java)
    private val feedController = (LayoutInflater.from(context).inflate(R.layout.overlay_feed, null,
                                                                       false) as FeedController)
            .also {
                it.setLauncherFeed(this)
                if (dark) {
                    it.setBackgroundColor(context.getColor(R.color.qsb_background_dark))
                }
            }
    private val recyclerView =  feedController.findViewById(R.id.feed_recycler) as RecyclerView

    private var callback: ILauncherOverlayCallback? = null
    private lateinit var layoutParams: WindowManager.LayoutParams

    private var feedAttached = false
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    recyclerView.adapter = this.adapter
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter!!.notifyDataSetChanged()
                    windowService.addView(feedController, layoutParams)
                } else {
                    recyclerView.adapter = null
                    recyclerView.layoutManager = null
                    windowService.removeView(feedController)
                }
            }
        }

    override fun startScroll() {
        recyclerView.adapter = null
        recyclerView.layoutManager = null
        handler.post {
            feedAttached = true
            feedController.startScroll()
        }
    }

    override fun onScroll(progress: Float) {
        handler.post { feedController.onScroll(progress) }
    }

    override fun endScroll() {
        handler.post { feedController.endScroll() }
    }

    override fun windowAttached(lp: WindowManager.LayoutParams, cb: ILauncherOverlayCallback,
                                flags: Int) {
        callback = cb
        cb.overlayStatusChanged(1)
        layoutParams = lp
        //        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        //        layoutParams.flags = layoutParams.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    }

    override fun windowAttached2(bundle: Bundle, cb: ILauncherOverlayCallback) {
        windowAttached(bundle.getParcelable("layout_params")!!, cb, 0 /* TODO: figure this out */)
    }

    override fun windowDetached(isChangingConfigurations: Boolean) {
        handler.post { windowService.removeView(feedController) }
    }

    override fun closeOverlay(flags: Int) {
        handler.post { feedController.closeOverlay((flags and 1) != 0, flags shr 2) }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
    }

    override fun openOverlay(flags: Int) {
        Log.d(TAG, "openOverlay($flags)")
    }

    override fun requestVoiceDetection(start: Boolean) {
        Log.d(TAG, "requestVoiceDetection")
    }

    override fun getVoiceSearchLanguage(): String {
        Log.d(TAG, "getVoiceSearchLanguage")
        return "en"
    }

    override fun isVoiceDetectionRunning(): Boolean {
        Log.d(TAG, "isVoiceDetectionRunning")
        return false
    }

    override fun hasOverlayContent(): Boolean {
        Log.d(TAG, "hasOverlayContent")
        return true
    }

    override fun unusedMethod() {
        Log.d(TAG, "unusedMethod")
    }

    override fun setActivityState(flags: Int) {
        Log.d(TAG, "setActivityState($flags)")
    }

    override fun startSearch(data: ByteArray?, bundle: Bundle?): Boolean {
        Log.d(TAG, "startSearch")
        return false
    }

    fun onProgress(progress: Float, isDragging: Boolean) {
        callback?.overlayScrollChanged(progress)
        val touchable = Math.signum(progress).compareTo(Math.signum(0f)) != 0
        if (!touchable && !isDragging) {
            feedAttached = false
        }
    }

    companion object {

        private const val TAG = "LauncherFeed"
    }
}
