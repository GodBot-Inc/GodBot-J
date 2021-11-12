package utils.apis.spotify;

import utils.customExceptions.LinkInterpretation.InternalServerError;
import utils.customExceptions.LinkInterpretation.RateLimitException;
import utils.customExceptions.LinkInterpretation.RequestException;

public class SpotifyApi {

    /**
     * A method for checking the response code and throwing the corresponding error
     * @param reponseCode you get when a request is sent
     * @throws RequestException Is raised with the following response Codes: 400, 401, 403
     * @throws RateLimitException IS raised with the following response Code: 429
     * @throws InternalServerError Is raised with the following response Codes: 500, 502, 503
     */
    public static void spotCheckResposneCode(int reponseCode)
            throws RequestException,
            RateLimitException,
            InternalServerError {
        switch (reponseCode) {
            case 400, 401, 403 -> throw new RequestException("The sent request failed");
            case 429 -> throw new RateLimitException("You send too many requests");
            case 500, 502, 503 -> throw new InternalServerError("The ");
        }
    }

    /**
     * Returns only the title and the author of a song
     * @param url that the title and the author should be extracted from
     * @return the title and the author in a String array
     */
    public static String[] getTitleAndAuthor(String url) {
        // TODO FIRST WRITE FUNCTION
//        LinkInterpreter.sendRequest();
        return new String[]{};
    }
}
