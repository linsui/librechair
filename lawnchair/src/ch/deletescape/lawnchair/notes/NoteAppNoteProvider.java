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

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import androidx.annotation.Nullable;
import ch.deletescape.lawnchair.todo.INoteProvider;
import ch.deletescape.lawnchair.todo.Note;
import ch.deletescape.lawnchair.todo.Note.Types;
import java.util.List;
import java.util.stream.Collectors;

public class NoteAppNoteProvider extends INoteProvider.Stub {

    private Context context;

    public NoteAppNoteProvider(Context context) {

        this.context = context;
    }


    @Override
    public List<Note> getNotes() throws RemoteException {
        return DatabaseStore.getAccessObject(context).getAllNotes().stream()
                .map(it -> new Note(it.title, it.content, it.colour, Types.NOTE)).collect(
                        Collectors.toList());
    }

    public static class Service extends android.app.Service {

        public NoteAppNoteProvider provider;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return provider != null ? provider : (provider = new NoteAppNoteProvider(this));
        }
    }
}
