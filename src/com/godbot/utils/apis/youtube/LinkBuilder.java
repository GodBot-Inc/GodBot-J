package utils.apis.youtube;

import io.github.cdimascio.dotenv.Dotenv;
import utils.customExceptions.LinkInterpretation.youtubeApi.ApiKeyNotRetreivedException;

/**
 * Link Builder is used to construct a link using . notation
 */
public class LinkBuilder {

    private final boolean hasPage;

    private final String url;
    private String pageToken;
    private String id;
    private String apiKey;

    public LinkBuilder(String url, boolean hasPage) {
        this.url = url;
        this.hasPage = hasPage;
    }

    public LinkBuilder setPageToken(String pageToken) {
        if (hasPage) {
            this.pageToken = pageToken;
        }
        return this;
    }

    public LinkBuilder setId(String id) {
        this.id = id;
        return this;
    }

    private static String getApiKey() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("YT_API_KEY");
    }

    public String build() {
        if (hasPage) {
            return String.format(
                    url,
                    pageToken,
                    id,
                    apiKey
            );
        } else {
            return String.format(
                    url,
                    id,
                    apiKey
            );
        }
    }
}
