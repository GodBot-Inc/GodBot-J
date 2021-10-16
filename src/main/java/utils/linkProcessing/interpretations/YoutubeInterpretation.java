package utils.linkProcessing.interpretations;

public class YoutubeInterpretation implements Interpretation {

    public final String searchTerm;
    public final long duration;
    public final String author;
    public final String title;
    public final String uri;
    public final String type;
    public final long likes;
    public final long dislikes;
    public final long views;
    public final long comments;

    public YoutubeInterpretation(String searchTerm, long duration, String author, String title, String uri, String type, long likes, long dislikes, long views, long comments) {
        this.searchTerm = searchTerm;
        this.duration = duration;
        this.author = author;
        this.title = title;
        this.uri = uri;
        this.type = type;
        this.likes = likes;
        this.dislikes = dislikes;
        this.views = views;
        this.comments = comments;
    }
}
