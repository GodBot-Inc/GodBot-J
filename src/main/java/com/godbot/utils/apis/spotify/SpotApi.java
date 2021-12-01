package utils.apis.spotify;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONException;
import org.json.JSONObject;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import utils.customExceptions.LinkInterpretation.InternalServerError;
import utils.customExceptions.LinkInterpretation.RateLimitException;
import utils.customExceptions.LinkInterpretation.RequestException;
import utils.linkProcessing.TypeAndId;

import java.io.IOException;
import java.net.URI;

public class SpotApi {

    private final String baseUrl = "https://api.spotify.com/v1";
    private SpotifyApi spotifyApi;
    private final URI redirectUri = SpotifyHttpManager.makeUri("https://godbot-music.com");
    private static final SpotApi OBJ = new SpotApi();

    private SpotApi() {
        Dotenv dotenv = Dotenv.load();
        String client_id = dotenv.get("SPOT_CLIENT_ID");
        String client_secret = dotenv.get("SPOT_CLIENT_SECRET");

        if (client_id == null) {
            System.out.println("ClientId could not be fetched from .env");
            return;
        } else if (client_secret == null) {
            System.out.println("ClientSecret could not be fetched from .env");
            return;
        }

        spotifyApi = SpotifyApi.builder()
                .setClientId(client_id)
                .setClientSecret(client_secret)
                .setRedirectUri(redirectUri)
                .build();

        ClientCredentials clientCredentials;
        try {
            clientCredentials = spotifyApi
                    .clientCredentials()
                    .build()
                    .execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Could not fetch credentials because" + e);
            return;
        }

        spotifyApi.setAccessToken(clientCredentials.getAccessToken());
    }

    /**
     * A method for checking the response code and throwing the corresponding error
     * @param reponseCode you get when a request is sent
     * @throws RequestException Is raised with the following response Codes: 400, 401, 403
     * @throws RateLimitException IS raised with the following response Code: 429
     * @throws InternalServerError Is raised with the following response Codes: 500, 502, 503
     */
    public void spotCheckResposneCode(int reponseCode)
            throws RequestException,
            RateLimitException,
            InternalServerError {
        switch (reponseCode) {
            case 400, 401, 403 -> throw new RequestException("The sent request failed");
            case 429 -> throw new RateLimitException("You send too many requests");
            case 500, 502, 503 -> throw new InternalServerError("The ");
        }
    }

    public void checkJSONError(JSONObject json)
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
    public String getTitleAndAuthor(TypeAndId typeAndId) throws IOException, ParseException, SpotifyWebApiException {
        spotifyApi.getTrack(typeAndId.Id).build().execute();
        return "Title - Author";
    }

    public static SpotApi getObj() {
        return OBJ;
    }
}
