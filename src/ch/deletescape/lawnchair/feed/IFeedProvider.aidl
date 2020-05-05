package ch.deletescape.lawnchair.feed;

import ch.deletescape.lawnchair.feed.RemoteCard;
import ch.deletescape.lawnchair.feed.RemoteAction;

interface IFeedProvider {
    List<RemoteCard> getCards();
    List<RemoteAction> getActions(boolean exclusive);
    boolean isVolatile();
}
