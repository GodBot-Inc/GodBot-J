import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import utils.apis.spotify.SpotApi;
import utils.apis.youtube.YoutubeApi;
import utils.customExceptions.LinkInterpretation.InvalidURLException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.interpretations.youtube.YoutubeInterpretation;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, RequestException, InvalidURLException {
        long time = 0;
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            YoutubeInterpretation ytInterpretation =
                    YoutubeApi.getPlaylistInformation("PLKfD8K0QKDy-knOHaPYcL0o9cmuG7ldY_");
            time += System.currentTimeMillis() - start;
        }
        System.out.println(time/10);
//        long start = System.currentTimeMillis();
//        YoutubeInterpretation ytInterpretation =
//                YoutubeApi.getPlaylistInformation("PLKfD8K0QKDy-knOHaPYcL0o9cmuG7ldY_");
//        long end = System.currentTimeMillis();
//        System.out.println(ytInterpretation);
//        System.out.println(end - start);
        // 2305 average of 10x calls
    }
}
