package com.example.chars.photocollection.main;

import com.example.chars.photocollection.common.data.PhotoItem;
import com.example.chars.photocollection.network.json.PhotoRecent;

import java.util.ArrayList;
import java.util.List;

public class RequestCallback implements PhotoCollectionFragment.RequestPhotoCallback {
    private static final String TAG = "RequestCallback";

    @Override
    public void requestSuccess(PhotoCollectionFragment fragment , List<PhotoRecent.PhotosBean
            .PhotoBean> list) {
        List<PhotoItem> items = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            PhotoItem item = new PhotoItem();
            item.setCaption(list.get(i).title);
            item.setOwner(list.get(i).owner);
            item.setUrl(list.get(i).url_s);
            item.setId(list.get(i).id);
            items.add(item);
        }
        fragment.init(items);
    }

    @Override
    public void requestFailed() {

    }
}
