package com.example.chars.photocollection.network.json;

import java.util.List;

public class PhotoSizes {

    /**
     * sizes : {"canblog":0,"canprint":0,"candownload":1,"size":[{"label":"Square","width":75,"height":75,"source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_s.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/sq/","media":"photo"},{"label":"Large Square","width":"150","height":"150","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_q.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/q/","media":"photo"},{"label":"Thumbnail","width":100,"height":56,"source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_t.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/t/","media":"photo"},{"label":"Small","width":"240","height":"135","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_m.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/s/","media":"photo"},{"label":"Small 320","width":"320","height":"180","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_n.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/n/","media":"photo"},{"label":"Medium","width":"500","height":"281","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/m/","media":"photo"},{"label":"Medium 640","width":"640","height":"360","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_z.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/z/","media":"photo"},{"label":"Medium 800","width":"800","height":"450","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_c.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/c/","media":"photo"},{"label":"Large","width":"1024","height":"576","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_b.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/l/","media":"photo"},{"label":"Large 1600","width":"1600","height":"900","source":"https://live.staticflickr.com/4844/31624992207_fb9a687eeb_h.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/h/","media":"photo"},{"label":"Large 2048","width":"2048","height":"1152","source":"https://live.staticflickr.com/4844/31624992207_33945a2ca4_k.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/k/","media":"photo"},{"label":"Original","width":"2208","height":"1242","source":"https://live.staticflickr.com/4844/31624992207_b4cfec027e_o.png","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/o/","media":"photo"}]}
     * stat : ok
     */

    private SizesBean sizes;
    private String stat;

    public SizesBean getSizes() {
        return sizes;
    }

    public void setSizes(SizesBean sizes) {
        this.sizes = sizes;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public static class SizesBean {
        /**
         * canblog : 0
         * canprint : 0
         * candownload : 1
         * size : [{"label":"Square","width":75,"height":75,"source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_s.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/sq/","media":"photo"},{"label":"Large Square","width":"150","height":"150","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_q.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/q/","media":"photo"},{"label":"Thumbnail","width":100,"height":56,"source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_t.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/t/","media":"photo"},{"label":"Small","width":"240","height":"135","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_m.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/s/","media":"photo"},{"label":"Small 320","width":"320","height":"180","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_n.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/n/","media":"photo"},{"label":"Medium","width":"500","height":"281","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/m/","media":"photo"},{"label":"Medium 640","width":"640","height":"360","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_z.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/z/","media":"photo"},{"label":"Medium 800","width":"800","height":"450","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_c.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/c/","media":"photo"},{"label":"Large","width":"1024","height":"576","source":"https://live.staticflickr.com/4844/31624992207_a3196f29b6_b.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/l/","media":"photo"},{"label":"Large 1600","width":"1600","height":"900","source":"https://live.staticflickr.com/4844/31624992207_fb9a687eeb_h.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/h/","media":"photo"},{"label":"Large 2048","width":"2048","height":"1152","source":"https://live.staticflickr.com/4844/31624992207_33945a2ca4_k.jpg","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/k/","media":"photo"},{"label":"Original","width":"2208","height":"1242","source":"https://live.staticflickr.com/4844/31624992207_b4cfec027e_o.png","url":"https://www.flickr.com/photos/163032290@N04/31624992207/sizes/o/","media":"photo"}]
         */

        private int canblog;
        private int canprint;
        private int candownload;
        private List<SizeBean> size;

        public int getCanblog() {
            return canblog;
        }

        public void setCanblog(int canblog) {
            this.canblog = canblog;
        }

        public int getCanprint() {
            return canprint;
        }

        public void setCanprint(int canprint) {
            this.canprint = canprint;
        }

        public int getCandownload() {
            return candownload;
        }

        public void setCandownload(int candownload) {
            this.candownload = candownload;
        }

        public List<SizeBean> getSize() {
            return size;
        }

        public void setSize(List<SizeBean> size) {
            this.size = size;
        }

        public static class SizeBean {
            /**
             * label : Square
             * width : 75
             * height : 75
             * source : https://live.staticflickr.com/4844/31624992207_a3196f29b6_s.jpg
             * url : https://www.flickr.com/photos/163032290@N04/31624992207/sizes/sq/
             * media : photo
             */

            private String label;
            private int width;
            private int height;
            private String source;
            private String url;
            private String media;

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getMedia() {
                return media;
            }

            public void setMedia(String media) {
                this.media = media;
            }
        }
    }
}
