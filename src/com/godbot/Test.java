import utils.audio.DurationCalc;
import utils.customExceptions.LinkInterpretation.InvalidURLException;
import utils.customExceptions.LinkInterpretation.RequestException;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, RequestException, InvalidURLException {
        System.out.println("Starting...");
        // 2 hours
        long hours = 7200000;
        // 54 minutes
        long minutes = 3240000;
        // 41 seconds
        long seconds = 41000;
        long time = hours + minutes + seconds;
        String str;
        long start = System.currentTimeMillis();
        str = DurationCalc.longToString(time);
        long end = System.currentTimeMillis();
        System.out.println("The program took " + (end - start) + " milliseconds to run");
        System.out.println(str);
    }
}
