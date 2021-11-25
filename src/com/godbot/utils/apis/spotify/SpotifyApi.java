package utils.apis.spotify;

import org.json.JSONException;
import org.json.JSONObject;
import utils.customExceptions.LinkInterpretation.InternalServerError;
import utils.customExceptions.LinkInterpretation.RateLimitException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.linkProcessing.TypeAndId;

public class SpotifyApi {

    private static final String baseUrl = "https://api.spotify.com/v1";

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

    public static void checkJSONError(JSONObject json)
            throws JSONException {
        if (json.getJSONObject("error") != null) {
            throw new JSONException("Error is given");
        }
    }

    /**
     * Returns only the title and the author of a song
     * @param typeAndId of the song that should be gathered info about
     * @return the title and the author in a String array
     */
    public static String getTitleAndAuthor(TypeAndId typeAndId) {
        // TODO FIRST WRITE FUNCTION
//        LinkInterpreter.sendRequest();
        return "Title - Author";
    }
}
