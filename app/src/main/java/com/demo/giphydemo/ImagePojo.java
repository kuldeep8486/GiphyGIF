package com.demo.giphydemo;

/**
 * Created by Kuldeep Sakhiya on 24-Jul-2017.
 */

public class ImagePojo
{
    private String imageId = "";
    private String thumbPath = "";
    private String imagePath = "";
    private String detailThumb = "";

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDetailThumb() {
        return detailThumb;
    }

    public void setDetailThumb(String detailThumb) {
        this.detailThumb = detailThumb;
    }
}
