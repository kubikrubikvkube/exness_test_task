package endpoints;

public enum Endpoints {
    PING("http://127.0.0.1:5000/ping/"),
    AUTHORIZE("http://127.0.0.1:5000/authorize/"),
    SAVE_DATA("http://127.0.0.1:5000/api/save_data/");

    public final String url;

    Endpoints(String url) {
        this.url = url;
    }
}
