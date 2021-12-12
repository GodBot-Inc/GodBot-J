package com.godbot.utils.linkProcessing;

import com.godbot.utils.customExceptions.requests.*;
import com.godbot.utils.customExceptions.LinkInterpretation.*;
import com.godbot.utils.customExceptions.ytApi.QuotaExpired;
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
        } else if (url.contains("https://www.youtube.com/") || url.contains("https://music.youtube.com/")) {
            return "youtube";
        } else {
            System.out.println("invalid platform");
            throw new PlatformNotFoundException(String.format("Platform for lin %s could not be determined", url));
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
        System.out.println("we got the platform!");
        if (platform.equals("youtube")) {
            System.out.println("platform is YT");
            return LinkInterpreter.ytGetTypeAndId(url).type.equals("video");
        }
        throw new PlatformNotFoundException("Platform for Link " + url + " not found");
    }

    /**
     * A method that is there for sending requests
     * @param url The destination that a request will be sent to
     * @return the response of the website as JSONObject
     * @throws IOException If the request failed
     * @throws RequestException If the request returned an invalid return code
     * @throws NotFoundException If the passed URL is invalid
     * @throws InternalError If the website has trouble processing the request
     */
    public static JSONObject sendRequest(String url)
            throws IOException, RequestException, NotFoundException, InternalError {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        switch (responseCode) {
            case 400, 401, 403 -> throw new RequestException("The request that was sent failed");
            case 404 -> throw new NotFoundException("The request returned a 404 error");
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
     * Method to check the return code for async api calls
     * @param response The response gotten from YouTube (first pass it here)
     * @return The response, so we can actually receive it where it's needed
     * @throws QuotaExpired If you can't send requests anymore for the day
     * @throws BadRequestException Your fault
     * @throws RateLimitException Too many requests at once
     * @throws InternalServerError YouTube fucked up
     * @throws EndpointMovedException They just moved an endpoint ;(
     * @throws NotFoundException What are you calling again?
     */
    public static HttpResponse<String> checkYTResponseCode(HttpResponse<String> response)
            throws QuotaExpired,
            BadRequestException,
            RateLimitException,
            InternalServerError,
            EndpointMovedException,
            NotFoundException {
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
            case 403 -> throw new QuotaExpired();
            case 404 -> throw new NotFoundException();
            case 429 -> throw new RateLimitException();
            case 500, 501, 503 -> throw new InternalServerError();
        }
        return response;
    }
}
