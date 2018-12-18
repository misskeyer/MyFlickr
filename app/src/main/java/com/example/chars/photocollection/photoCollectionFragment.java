package com.example.chars.photocollection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chars.photocollection.data.PhotoItem;

import java.util.ArrayList;
import java.util.List;

public class photoCollectionFragment extends Fragment {
    private static final String TAG = "PhotoCollectionFragment";
    private RecyclerView photoRecyclerView;
    private List<PhotoItem> items = new ArrayList<>();

    private class FetchItemTask extends AsyncTask<Void, Void, List<PhotoItem>> {

        @Override
        protected List<PhotoItem> doInBackground(Void... voids) {
//            try {
//                String result = new FlickrFetchr().getUrlString("https://www.baidu.com");
//                Log.i(TAG,"Fetched contents of URL: " + result);
//            } catch (IOException e) {
//                Log.e(TAG,"Failed to fecth URL.");
//            }
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<PhotoItem> photoItems) {
            super.onPostExecute(photoItems);
            items = photoItems;
            setupAdapter();
        }
    }

    public static photoCollectionFragment newInstance() {
        return new photoCollectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_container,container,false);
        photoRecyclerView = view.findViewById(R.id.fragment_photo_collection);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        if (isAdded())
            photoRecyclerView.setAdapter(new PhotoAdapter(items));
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {
        private TextView titleTV;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = (TextView) itemView;
        }

        public void bindPhotoItem(PhotoItem item) {
            titleTV.setText(item.toString());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<PhotoItem> photoItems;

        public PhotoAdapter(List<PhotoItem> items) {
            photoItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            PhotoItem item = photoItems.get(i);
            photoHolder.bindPhotoItem(item);
        }

        @Override
        public int getItemCount() {
            return photoItems.size();
        }
    }
}






