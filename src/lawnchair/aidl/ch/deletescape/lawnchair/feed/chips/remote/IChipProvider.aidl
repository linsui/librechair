package ch.deletescape.lawnchair.feed.chips.remote;
import ch.deletescape.lawnchair.feed.chips.remote.RemoteItem;

interface IChipProvider {
    List<RemoteItem> getChips(int launcherOverrideAccent);
}
