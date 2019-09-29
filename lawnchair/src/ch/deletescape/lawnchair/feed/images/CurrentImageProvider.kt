package ch.deletescape.lawnchair.feed.images

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.ParcelFileDescriptor
import ch.deletescape.lawnchair.feed.FeedScope
import ch.deletescape.lawnchair.feed.images.providers.ImageProvider
import ch.deletescape.lawnchair.util.extensions.d
import com.android.launcher3.Utilities
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class CurrentImageProvider : ContentProvider() {
    companion object {
        val AUTHORITY = "ch.deletescape.lawnchair.feed.FEED_BACKGROUND_IMAGE"
        val SHARE_QUERIES = mutableListOf<String>()
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? = synchronized(this) {
        d("query: query called. lastPathSegment: ${uri.lastPathSegment} shareQueries: $SHARE_QUERIES")
        val c = MatrixCursor(
                arrayOf("_id", "_data", "orientation", "mime_type", "datetaken", "_display_name"))
        c.addRow(arrayOf<Any?>(0, uri.lastPathSegment, 0, "image/png", System.currentTimeMillis(), ""))
        return c
    }

    override fun getType(uri: Uri): String? {
        return "image/png"
    }

    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? = synchronized(this) {
        if (!SHARE_QUERIES.contains(uri.lastPathSegment!!)) {
            return null
        }
        d("openFile: openFile called. lastPathSegment: ${uri.lastPathSegment} shareQueries: $SHARE_QUERIES")
        val currentBitmap = File(context!!.cacheDir, uri.lastPathSegment)
        currentBitmap.delete()
        var flag = false
        FeedScope.launch {
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
        val descriptor = ParcelFileDescriptor.open(
                File(context!!.cacheDir, uri.lastPathSegment),
                ParcelFileDescriptor.MODE_READ_ONLY)
        d("openFile: descriptor ${uri.lastPathSegment} is $descriptor")
        return descriptor
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
