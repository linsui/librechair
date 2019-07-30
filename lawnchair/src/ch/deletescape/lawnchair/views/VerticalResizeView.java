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

package ch.deletescape.lawnchair.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.android.launcher3.R;
import kotlin.jvm.functions.Function1;

public class VerticalResizeView extends View {

    private Float coordY;
    private Function1<Float, Void> onResizeCallback;

    public VerticalResizeView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundResource(R.drawable.ic_arrow_down_blue);
        setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
    }

    public VerticalResizeView(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundResource(R.drawable.ic_arrow_down_blue);
        setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
    }

    public VerticalResizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundResource(R.drawable.ic_arrow_down_blue);
        setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
    }

    public VerticalResizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setBackgroundResource(R.drawable.ic_arrow_down_blue);
        setRotationY(90);
        setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            coordY = event.getY();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (coordY != null && onResizeCallback != null && (coordY - 100 < event.getY()
                    && coordY + 100 > event.getY())) {
                onResizeCallback.invoke(event.getY() - coordY);
                coordY = event.getY();
                return true;
            }
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            coordY = null;
            return true;
        } else {
            return false;
        }
    }

    public void setOnResizeCallback(
            Function1<Float, Void> onResizeCallback) {
        this.onResizeCallback = onResizeCallback;
    }
}
