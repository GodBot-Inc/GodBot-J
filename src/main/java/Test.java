import utils.customExceptions.LinkInterpretation.InvalidURL;
import utils.customExceptions.LinkInterpretation.RequestFailed;
import utils.apis.youtube.youtubeApi;
import utils.linkProcessing.interpretations.youtube.YoutubePlaylistInterpretation;
import utils.linkProcessing.interpretations.youtube.YoutubeVideoInterpretation;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, RequestFailed, InvalidURL {
        System.out.println("Starting...");
        long start = System.currentTimeMillis();
        YoutubePlaylistInterpretation ytInterpretation = youtubeApi.getPlaylistInformation("PLKfD8K0QKDy8Zs2H65SB43pK56DhUAF4g");
        long end = System.currentTimeMillis();
        System.out.println("The program took " + (end - start) + " milliseconds to run");
        System.out.println(ytInterpretation);
    }
}
