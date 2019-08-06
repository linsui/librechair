package ch.deletescape.lawnchair.feed;

import ch.deletescape.lawnchair.feed.RemoteCard;

interface IFeedProvider {
    List<RemoteCard> getCards();
}
