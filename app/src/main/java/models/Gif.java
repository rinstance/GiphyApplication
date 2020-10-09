package models;

public class Gif {
    private String id;
    private Images images;
    private transient String url;

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public Images getImages() {
        return images;
    }

    public String getUrl() {
        return url;
    }
}
