package ch.deletescape.lawnchair.feed.images

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.Utilities
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class CurrentImageProvider : ContentProvider() {
    companion object {
        val AUTHORITY = "ch.deletescape.lawnchair.feed.FEED_BACKGROUND_IMAGE"
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val currentBitmap = File(context!!.cacheDir, "feed_background_share.png")
        currentBitmap.delete()
        var flag = false
        GlobalScope.launch {
            try {
                ImageProvider.inflate(Utilities.getLawnchairPrefs(context).feedBackground,
                                      context!!)?.getBitmap(context!!)!!
                        .compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(currentBitmap))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            flag = true
        }
        while (!flag);
        val c = MatrixCursor(
                arrayOf("_id", "_data", "orientation", "mime_type", "datetaken", "_display_name"))
        c.addRow(arrayOf<Any?>(0, currentBitmap, 0, "image/png", System.currentTimeMillis(),
                               "Librechair background image"))
        return c
    }

    override fun getType(uri: Uri): String? {
        return "image/png"
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        d("openFile: openFile called")
        return ParcelFileDescriptor.open(File(context!!.cacheDir, "feed_background_share.png"),
                                         ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun openFile(uri: Uri, mode: String,
                          signal: CancellationSignal?): ParcelFileDescriptor? {
        d("openFile: openFile called")
        return ParcelFileDescriptor.open(File(context!!.cacheDir, "feed_background_share.png"),
                                         ParcelFileDescriptor.MODE_READ_ONLY)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }
}
