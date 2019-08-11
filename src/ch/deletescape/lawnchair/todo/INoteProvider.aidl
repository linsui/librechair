package ch.deletescape.lawnchair.todo;

import ch.deletescape.lawnchair.todo.Note;

interface INoteProvider {
    List<Note> getNotes();
}
