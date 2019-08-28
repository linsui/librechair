/*
 *     Copyright (c) 2017-2019 the Lawnchair team
 *     Copyright (c)  2019 oldosfan (would)
 *     This file is part of Lawnchair Launcher.
 *
 *     Lawnchair Launcher is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Lawnchair Launcher is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Lawnchair Launcher.  If not, see <https://www.gnu.org/licenses/>.
 */

package ch.deletescape.lawnchair.notes;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import ch.deletescape.lawnchair.flights.Flight;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Note implements Parcelable {

    public Note() {
        this.id = UUID.randomUUID().hashCode();
    }

    public Note(String title, String content, int colour) {
        this();
        this.title = title;
        this.content = content;
        this.colour = colour;
    }

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "note_title")
    public String title;

    @ColumnInfo(name = "note_content")
    public String content;

    @ColumnInfo(name = "note_color")
    public int colour;

    @ColumnInfo(name = "note_selected")
    public boolean selected;

    @ColumnInfo(name = "note_flight")
    public String serializedFlight;

    public Flight getFlight() {
        return Flight.fromJson(serializedFlight);
    }

    public void setFlight(Flight flight) {
        serializedFlight = flight.toString();
    }

    protected Note(Parcel in) {
        id = in.readLong();
        title = in.readString();
        content = in.readString();
        colour = in.readInt();
        selected = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(colour);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Note)) {
            return false;
        }
        Note note = (Note) o;
        return id == note.id &&
                colour == note.colour &&
                Objects.equals(title, note.title) &&
                Objects.equals(content, note.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, colour);
    }
}
