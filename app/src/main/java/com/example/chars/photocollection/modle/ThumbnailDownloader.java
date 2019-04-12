package com.example.chars.photocollection.modle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.example.chars.photocollection.modle.FlickrFetchr;
import com.example.chars.photocollection.util.BitmapUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    private Boolean hasQuit = false;
    private Handler requestHandler;
    private Handler responseHandler;
    private ThumbnailDownloadListener<T> thumbnailDownloadListener;
    private ConcurrentMap<T, String> requestMap = new ConcurrentHashMap<>();
    private LruCache<String, Bitmap> lruCache;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        thumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        lruCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        requestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_DOWNLOAD:
                        T target = (T) msg.obj;
                        Log.i(TAG, "Got a request for URL: " + requestMap.get(target));
                        handleRequest(target);
                        break;
                    case MESSAGE_PRELOAD:
                        String url = (String) msg.obj;
                        downloadImage(url);
                        break;
                }
            }
        };
    }

    private void handleRequest(final T target) {
            final String url = requestMap.get(target);
            if (url == null)
                return;

            final Bitmap bitmap = downloadImage(url);

            Log.i(TAG, "Bitmap created.");
            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(target) != url || hasQuit)
                        return;
                    requestMap.remove(target);
                    thumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
    }

    private Bitmap downloadImage(String url) {
        Bitmap bitmap;
        if (url == null)
            return null;

        bitmap = BitmapUtils.getBitmapFromLocal(url);
        if (bitmap != null)
            return bitmap;

        bitmap = lruCache.get(url);
        if (bitmap != null)
            return bitmap;

        try {
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            lruCache.put(url, bitmap);
            BitmapUtils.setBitmapToLocal(bitmap, url);
            Log.i(TAG, "Downloaded & cached image: " + url);
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image.", e);
            e.printStackTrace();
            return null;
        }
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);
        if (url == null) {
            requestMap.remove(target);
        } else {
            requestMap.put(target, url);
            requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void preloadImage(String url) {
        requestHandler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
    }

    public Bitmap getCachedImage(String url) {
        return lruCache.get(url);
    }

    public void clearQueue() {
        requestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    public void clearPreloadQueue(){
        requestHandler.removeMessages(MESSAGE_PRELOAD);
    }

    public void clearCache() {
        lruCache.evictAll();
    }

    @Override
    public boolean quit() {
        hasQuit = true;
        return super.quit();
    }
}
