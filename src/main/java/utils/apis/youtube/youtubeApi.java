package utils.apis.youtube;

import org.json.JSONObject;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.RequestFailed;
import utils.linkProcessing.interpretations.youtube.YoutubeInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubePlaylistInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class youtubeApi {
    public static final String url = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet&id=hd1D61037c8&videoCategoryId=10&key=[YOUR_API_KEY]";

//    public static YoutubeVideoInterpretation getVideoInformation(String id) {
//        String getVideoInformationUrl = "";
//        return new YoutubeVideoInterpretation()
//    }

//    public static YoutubePlaylistInterpretation getPlaylistInformation(String id) {
//        String getPlaylistInformationUrl = "";
//    }

    private static void checkResponseCode(int code) throws RequestFailed, InvalidURL {
        switch (code) {
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
                System.out.println("Request was successful");
                break;
            case 400:
            case 401:
            case 403:
                throw new RequestFailed("The request that was sent failed");
            case 404:
                throw new InvalidURL("The request returned a 404 error");
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                throw new InternalError("Youtube has some issues resolving this request");
        }
    }

    private static JSONObject sendRequest(String url) throws IOException, RequestFailed, InvalidURL {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        checkResponseCode(responseCode);
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
}
