package ch.deletescape.lawnchair.feed;

import ch.deletescape.lawnchair.feed.SortingAlgorithmArgumentWrapper;
import ch.deletescape.lawnchair.feed.RemoteCard;

interface ISortingAlgorithm {
    List<RemoteCard> sort(in SortingAlgorithmArgumentWrapper cards);
}
