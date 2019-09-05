package ch.deletescape.lawnchair.feed.cache;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
abstract class CacheDao {
    @Insert
    abstract void insert(CacheEntry entry);
    @Query("select * from cacheentry where cacheId like :cacheId limit 1")
    abstract CacheEntry findEntryById(int cacheId);
    @Delete
    abstract void delete(CacheEntry entry);
}
