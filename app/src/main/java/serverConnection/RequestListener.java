package serverConnection;

import cz.msebera.android.httpclient.Header;

public interface RequestListener{
    public void onSuccess(int statusCode, Header[] headers, byte[] response);
}
