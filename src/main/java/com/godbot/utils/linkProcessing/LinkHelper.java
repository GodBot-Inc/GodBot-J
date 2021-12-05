package com.godbot.utils.linkProcessing;

import com.godbot.utils.customExceptions.LinkInterpretation.InvalidURLException;
import com.godbot.utils.customExceptions.LinkInterpretation.PlatformNotFoundException;
import com.godbot.utils.customExceptions.LinkInterpretation.RequestException;
import org.asynchttpclient.*;
import org.asynchttpclient.util.HttpConstants;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LinkHelper {

    /**
    * Just a helper function to get info
     * @param url that the platform should be determined of
     * @return the platform
     * @throws PlatformNotFoundException if no platform could be found
     */
    public static String getPlatform(String url) throws PlatformNotFoundException {
        if (url.contains("https://open.spotify.com/")) {
            return "spotify";
        } else if (url.contains("https://youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else {
            throw new PlatformNotFoundException(String.format("Platform for lin %s could not be determined", url));
        }
//        } else if (url.contains("https://soundcloud.com/")) {
//            return "soundcloud";
//        }
    }

    /**
     * A method that is there for sending requests
     * @param url The destination that a request will be sent to
     * @return the response of the website as JSONObject
     * @throws IOException If the request failed
     * @throws RequestException If the request returned an invalid return code
     * @throws InvalidURLException If the passed URL is invalid
     * @throws InternalError If the website has trouble processing the request
     */
    public static JSONObject sendRequest(String url)
            throws IOException, RequestException, InvalidURLException, InternalError {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        switch (responseCode) {
            case 400, 401, 403 -> throw new RequestException("The request that was sent failed");
            case 404 -> throw new InvalidURLException("The request returned a 404 error");
            case 500, 501, 502, 503, 504 -> throw new InternalError("The site has some issues resolving your request");
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(con.getInputStream())
        );
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        System.out.println(response);
        return new JSONObject(response.toString());
    }

    /**
     * A function to send asynchronous http requests
     * @param url to send the request to
     * @param handler is used when a message is received
     */
    public static void sendAsyncGetRequest(String url, AsyncCompletionHandler<Object> handler) {
        /*
         After this is called, the request was sent, but not received.
         So other things can be executed while we wait for an answer.
         */
        new DefaultAsyncHttpClient(
                new DefaultAsyncHttpClientConfig.Builder()
                        .setMaxRedirects(2)
                        .build()
        ).executeRequest(
                new RequestBuilder(HttpConstants.Methods.GET),
                handler
        );
    }
}
