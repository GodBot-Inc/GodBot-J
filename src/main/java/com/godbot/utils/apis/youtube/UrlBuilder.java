package com.godbot.utils.apis.youtube;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Link Builder is used to construct a link using . notation
 */
public class UrlBuilder {

    private final boolean hasPage;

    private final String url;
    private String pageToken;
    private String id;

    public UrlBuilder(String url, boolean hasPage) {
        this.url = url;
        this.hasPage = hasPage;
    }

    public UrlBuilder setPageToken(String pageToken) {
        if (hasPage) {
            this.pageToken = pageToken;
        }
        return this;
    }

    public UrlBuilder setId(String id) {
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
                    getApiKey()
            );
        } else {
            return String.format(
                    url,
                    id,
                    getApiKey()
            );
        }
    }
}
