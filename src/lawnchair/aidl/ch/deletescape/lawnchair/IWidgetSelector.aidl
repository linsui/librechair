package ch.deletescape.lawnchair;

import ch.deletescape.lawnchair.WidgetSelectionCallback;

interface IWidgetSelector {
    oneway void pickWidget(WidgetSelectionCallback callback);
}
