package com.example.chars.photocollection.data;

import java.util.List;

public class ResultPhotos {
    /**
     * "page":1,
     * 	"pages":2354,
     * 	"perpage":100,
     * 	"total":"235337",
     */
    public int page;
    public int pages;
    public int perpage;
    public int total;
    public List<photo> photos;

    /**
     * "id":"32691810168",
     * 	"owner":"167192543@N04",
     * 	"secret":"22f540ed71",
     * 	"server":"4868",
     * 	"farm":5,
     * 	"title":"Scottie in the Flowers",
     * 	"ispublic":1,
     * 	"isfriend":0,
     * 	"isfamily":0,
     * 	"url_s":"https:\/\/farm5.staticflickr.com\/4868\/32691810168_22f540ed71_m.jpg",
     * 	"height_s":"160",
     * 	"width_s":"240"
     */
    public class photo {
        public String id;
        public String owner;
        public String secret;
        public int server;
        public int farm;
        public String title;
        public boolean ispublic;
        public boolean isfriend;
        public boolean isfamily;
        public String url_s;
        public int height_s;
        public int width_s;
    }

    /**
     * "stat": "ok"
     */
    public String stat;
}
