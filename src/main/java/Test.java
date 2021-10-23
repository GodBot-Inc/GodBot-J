import org.json.JSONObject;
import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.RequestFailed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {
    public static void main(String[] args) throws IOException, RequestFailed, InvalidURL {
        JSONObject object = sendRequest("https://youtube.googleapis.com/youtube/v3/videos?part=snippet&part=contentDetails&part=player&part=statistics&id=XzUcSWVkjVA&videoCategoryId=10&key=AIzaSyDw076BIlPImvqay6IlGGzv_N_vFOFxd98");
    }

    /*
    GET https://youtube.googleapis.com/youtube/v3/videos?part=snippet&part=contentDetails&part=player&part=statistics&id=XzUcSWVkjVA&videoCategoryId=10&key=[YOUR_API_KEY]
     */

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
