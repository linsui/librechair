package ch.deletescape.lawnchair;

import ch.deletescape.lawnchair.feed.IImageStoreCallback;

interface IImageSelector {
    oneway void selectImage(IImageStoreCallback callback);
}
