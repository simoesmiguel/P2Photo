package serverConnection;
import android.util.Log;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class RequestHandler{

    private static RequestHandler instance;

    private AsyncHttpClient client;

    private RequestHandler(){
        client = new AsyncHttpClient();
    }

    public static RequestHandler getInstance(){
        if(instance == null){
            instance = new RequestHandler();
        }
        return instance;
    }

    // You can add more parameters if you need here.
    public void makeRequest(String url, final RequestListener listener){
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                //Some debugging code here
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                listener.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Some debugging code here, show retry dialog, feedback etc.
            }

            @Override
            public void onRetry(int retryNo) {
                //Some debugging code here-------

            }
        });
    }
}
