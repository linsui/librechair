package ch.deletescape.lawnchair.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public abstract class NoteDao {

    @Insert
    public abstract void insert(Note... notes);

    @Query("SELECT * from note")
    public abstract List<Note> getAllNotes();

    @Query("SELECT * from note WHERE id LIKE :id LIMIT 1")
    public abstract Note findNoteById(int id);

    @Query("SELECT * from NOTE where note_title LIKE :title LIMIT 1")
    public abstract Note findNoteByTitle(String title);

    @Delete
    public abstract void remove(Note note);
}
