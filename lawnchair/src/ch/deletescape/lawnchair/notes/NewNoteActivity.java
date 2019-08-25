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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import ch.deletescape.lawnchair.settings.ui.SettingsBaseActivity;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.R;

@SuppressLint("Registered")
public class NewNoteActivity extends SettingsBaseActivity {

    public static final String RETURN_NOTE = "created_note";

    private ExtendedEditText title;
    private ExtendedEditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        title = findViewById(R.id.title);
        message = findViewById(R.id.note_content);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (title.getText().toString().isEmpty() || message.getText().toString().isEmpty()) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else {
            setResult(RESULT_OK, new Intent().putExtra(RETURN_NOTE,
                    new Note(title.getText().toString(), message.getText().toString(),
                            Color.CYAN)));
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (title.getText().toString().isEmpty() || message.getText().toString().isEmpty()) {
            Snackbar.make(title, R.string.title_snackbar_empty_note, Snackbar.LENGTH_LONG).show();
        } else {
            setResult(RESULT_OK, new Intent().putExtra(RETURN_NOTE,
                    new Note(title.getText().toString(), message.getText().toString(),
                            getColor(R.color.colorAccent))));
            super.onBackPressed();
        }
    }
}
