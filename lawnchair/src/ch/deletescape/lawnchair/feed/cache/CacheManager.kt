package ch.deletescape.lawnchair.feed.cache

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import ch.deletescape.lawnchair.util.SingletonHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class CacheManager(val context: Context) {
    private val database = Room.databaseBuilder(context, CacheDatabase::class.java,
            this::class.qualifiedName!!).enableMultiInstanceInvalidation().build()

    fun getCachedBytes(namespace: String, cacheEntry: String): Array<Byte> {
        return database.access().findEntryById(
                (namespace + cacheEntry).hashCode() xor 4)?.getCachedData(context)?.toTypedArray()
                ?: emptyArray()
    }

    suspend fun suspendCachedBytes(namespace: String,
                                   cacheEntry: String): Array<Byte> = GlobalScope.async {
        getCachedBytes(namespace, cacheEntry)
    }.await()

    fun writeCache(data: Array<Byte>,  namespace: String, cacheEntry: String) {
        var outputStream = database.access().findEntryById(
                (namespace + cacheEntry).hashCode() xor 4)?.getOutputStream(context)
        if (outputStream == null) {
            outputStream = CacheEntry(namespace, cacheEntry).also { database.access().insert(it) }.getOutputStream(context)
        }
        outputStream.write(data.toByteArray())
    }

    companion object : SingletonHolder<CacheManager, Context>(::CacheManager)
}