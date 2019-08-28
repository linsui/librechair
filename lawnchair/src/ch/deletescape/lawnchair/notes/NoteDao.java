package ch.deletescape.lawnchair.notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import ch.deletescape.lawnchair.flights.Flight;
import java.util.List;

@Dao
public abstract class NoteDao {

    @Insert
    public abstract void insert(Note... notes);

    @Query("select * from note")
    public abstract List<Note> getAllNotes();

    @Query("select * from note where id like :id limit 1")
    public abstract Note findNoteById(int id);

    @Query("select * from note where note_title like :title limit 1")
    public abstract Note findNoteByTitle(String title);

    @Query("update note set note_selected = :selected where id like :id ")
    public abstract void setSelected(long id, boolean selected);

    @Query("update note set note_content = :content where id like :id")
    public abstract void setContent(long id, String content);

    @Query("update note set note_color = :color where id like :id")
    public abstract void setColor(long id, int color);

    @Query("update note set note_flight = :flight where id like :id")
    public abstract void setFlight(long id, Flight flight);

    @Delete
    public abstract void remove(Note note);
}
