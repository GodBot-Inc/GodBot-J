import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import utils.apis.spotify.SpotApi;
import utils.apis.youtube.YoutubeApi;
import utils.customExceptions.LinkInterpretation.InvalidURLException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.interpretations.youtube.YoutubeInterpretation;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, RequestException, InvalidURLException {
        long start = System.currentTimeMillis();
        YoutubeInterpretation ytInterpretation = YoutubeApi.getVideoInformation("vSF3u5eaT7s");
        long end = System.currentTimeMillis();
        System.out.println(ytInterpretation);
        System.out.println(end - start);
    }
}
