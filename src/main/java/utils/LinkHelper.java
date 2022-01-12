package utils;

import ktUtils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;

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
        } else if (
                url.contains("https://www.youtube.com/") ||
                url.contains("https://music.youtube.com/") ||
                url.contains("https://youtube.com/") ||
                url.contains("https://youtu.be/") ||
                url.contains("https://www.youtu.be/")
        ) {
            return "youtube";
        } else {
            throw new PlatformNotFoundException();
        }
    }

    /**
     * A method that checks if the given link is a video link
     * @param url The link that should be checked
     * @return true if the link is a video link, false if not
     * @throws PlatformNotFoundException if the platform could not be determined
     * @throws InvalidURLException if the link is invalid
     */
    public static boolean isVideo(String url)
            throws PlatformNotFoundException, InvalidURLException {
        String platform = getPlatform(url);
        if (platform.equals("youtube")) {
            return DataGatherer.ytGetTypeAndId(url).type.equals("video");
        }
        throw new PlatformNotFoundException();
    }

    /**
     * A method that is there for sending requests
     * @param url The destination that a request will be sent to
     * @return the response of the website as JSONObject
     * @throws IOException If the request failed
     * @throws RequestException If the request returned an invalid return code
     * @throws EndpointNotFoundException If the passed URL is invalid
     * @throws InternalError If the website has trouble processing the request
     */
    public static JSONObject sendRequest(String url)
            throws IOException, RequestException, EndpointNotFoundException, InternalServerException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        switch (responseCode) {
            case 400, 401, 403 -> throw new RequestException();
            case 404 -> throw new EndpointNotFoundException();
            case 500, 501, 502, 503, 504 -> throw new InternalServerException();
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
        return new JSONObject(response.toString());
    }

    /**
     * Method to check the return code for async api calls
     * @param response The response gotten from YouTube (first pass it here)
     * @return The response, so we can actually receive it where it's needed
     * @throws QuotaExpiredException If you can't send requests anymore for the day
     * @throws BadRequestException Your fault
     * @throws RateLimitException Too many requests at once
     * @throws InternalServerException YouTube fucked up
     * @throws EndpointMovedException They just moved an endpoint ;(
     * @throws EndpointNotFoundException What are you calling again?
     */
    public static HttpResponse<String> checkYTResponseCode(HttpResponse<String> response)
            throws QuotaExpiredException,
            BadRequestException,
            RateLimitException,
            InternalServerException,
            EndpointMovedException,
            EndpointNotFoundException {
        JSONObject jsonObject;
        int code;
        try {
            jsonObject = new JSONObject(response.body());
            code = jsonObject
                    .getJSONObject("error")
                    .getJSONArray("errors")
                    .getJSONObject(0)
                    .getInt("code");
        } catch (JSONException e) {
            throw new BadRequestException();
        }
        switch (code) {
            case 301, 303, 304, 307, 410 -> throw new EndpointMovedException();
            case 400, 402, 405, 409, 412, 413, 416, 417, 428 -> throw new BadRequestException();
            case 403 -> throw new QuotaExpiredException();
            case 404 -> throw new EndpointNotFoundException();
            case 429 -> throw new RateLimitException();
            case 500, 501, 503 -> throw new InternalServerException();
        }
        return response;
    }
}
