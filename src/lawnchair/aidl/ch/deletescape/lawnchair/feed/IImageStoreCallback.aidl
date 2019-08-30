package ch.deletescape.lawnchair.feed;

interface IImageStoreCallback {
    oneway void onImageRetrieved(String id);
}