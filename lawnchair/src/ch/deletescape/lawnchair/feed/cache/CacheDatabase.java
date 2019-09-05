package ch.deletescape.lawnchair.feed.cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CacheEntry.class}, version = 1)
public abstract class CacheDatabase extends RoomDatabase {
    abstract CacheDao access();
}
