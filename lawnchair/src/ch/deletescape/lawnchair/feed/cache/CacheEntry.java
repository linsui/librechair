package ch.deletescape.lawnchair.feed.cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.launcher3.util.IOUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Entity
class CacheEntry {
    CacheEntry() {
    }

    @SuppressLint("DefaultLocale")
    CacheEntry(String namespace, String id, long expiry) {
        this.cacheId = (namespace + id).hashCode() ^ namespace.length();
        this.filePath = String.format("cacheManager/%s/%s/%d", namespace, id, cacheId);
        this.expiry = System.currentTimeMillis() + expiry;
    }

    @PrimaryKey
    protected int cacheId;

    @ColumnInfo(name = "file_path")
    protected String filePath;

    protected long expiry;

    public boolean expired() {
        return expiry < System.currentTimeMillis();
    }

    @Nullable
    public byte[] getCachedData(Context context) {
        try {
            return IOUtils.toByteArray(new File(context.getCacheDir(), filePath));
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    public FileInputStream getInputStream(Context context) {
        try {
            return new FileInputStream(new File(context.getCacheDir(), filePath));
        } catch (IOException e) {
            return null;
        }
    }

    @NotNull
    public OutputStream getOutputStream(Context context) {
        try {
            File file = new File(context.getCacheDir(), filePath);
            file.getParentFile().mkdirs();
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.d(getClass().getName(), "getOutputStream: could not retrieve output stream", e);
            return new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    return;
                }
            };
        }
    }
}
