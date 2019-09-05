package ch.deletescape.lawnchair.feed.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.deletescape.lawnchair.util.SingletonHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit

class CacheManager(val context: Context) {
    private val database = Room.databaseBuilder(context, CacheDatabase::class.java,
            this::class.qualifiedName!!).enableMultiInstanceInvalidation().build()

    fun getCachedBytes(namespace: String, cacheEntry: String): ByteArray {
        var data = database.access().findEntryById(
                (namespace + cacheEntry).hashCode() xor 4)?.getCachedData(context)
                ?: byteArrayOf()
        if (database.access().findEntryById(
                        (namespace + cacheEntry).hashCode() xor 4)?.expired() != false) {
            data = byteArrayOf()
        }
        return data
    }

    suspend fun suspendCachedBytes(namespace: String,
                                   cacheEntry: String): ByteArray = GlobalScope.async {
        getCachedBytes(namespace, cacheEntry)
    }.await()

    fun writeCache(data: ByteArray, namespace: String, cacheEntry: String, expiry: Long = TimeUnit.HOURS.toMillis(1)) {
        var outputStream = database.access().findEntryById(
                (namespace + cacheEntry).hashCode() xor 4)?.getOutputStream(context)
        if (outputStream == null) {
            outputStream = CacheEntry(namespace, cacheEntry, expiry).also {
                database.access().insert(it)
            }.getOutputStream(context)
        }
        outputStream.write(data)
    }

    companion object : SingletonHolder<CacheManager, Context>(::CacheManager)
}