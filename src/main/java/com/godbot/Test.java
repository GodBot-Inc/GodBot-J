import org.apache.log4j.BasicConfigurator;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import utils.apis.youtube.UrlConstructor;
import utils.apis.youtube.YoutubeApi;

public class Test {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        AsyncCompletionHandler<Object> handler = new AsyncCompletionHandler<>() {
            @Override
            public Object onCompleted(Response response) {
                System.out.println("In Async" + System.currentTimeMillis());
                System.out.println(response.getResponseBody());
                return response;
            }
        };

        String url = UrlConstructor.getYTVideo()
                .setId("6hq8iwOancs")
                .build();

        long start = System.currentTimeMillis();
        YoutubeApi.sendAsyncRequest(
                url,
                handler
        );
        System.out.println("Out of async" + System.currentTimeMillis());
        System.out.println(System.currentTimeMillis() - start);
    }
}
