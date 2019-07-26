package serverConnection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import data.Album;
import data.AppDatabase;
import data.Participant;
import data.User;
import jsonParser.JsonParser;
import pt.ulisboa.tecnico.cmov.p2photo.LoginInActivity;
import pt.ulisboa.tecnico.cmov.p2photo.RegisterActivity;

public class Server {
    public static final String TAG= "SERVER_LOG";

    public static String IP = "http://25.38.100.33:5000/";
    //private static String IP = "http://10.0.2.2:5000/";
    private static final String LOGIN = "login";
    private static final String GET_ALL_USERS_COOMAND = "getallusers";
    public static final String NEW_USER = "newuser";
    private static final String ADD_PARTICIPANT = "addparticipant";
    private static final String GET_PARTICIPANTS = "getparticipants";
    private static final String GET_ALBUNS_FROM_USER = "getalbums";

    public static void getAllUsers(final Context context, final String username){

        RequestHandler handler = RequestHandler.getInstance();
        handler.makeRequest(IP + GET_ALL_USERS_COOMAND, new RequestListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String responseText = new String(response, "utf8");
                    Log.d(TAG, responseText);
                    List<Object> list = JsonParser.jsonArrayToMap(responseText);
                    AppDatabase db = AppDatabase.getAppDatabase(context);
                    for (Object object:list
                         ) {
                        HashMap<String,String> objectMap = (HashMap<String, String>) object;
                        User user = User.instanceFromHashMap(objectMap, context);

                        //If user does not exists
                        if (db.userDao().findByName(user.getName()) == null) {
                         db.userDao().insertAll(user);
                        }
                        User.setSelfUser(db.userDao().findByName(username));

                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR WHEN PARSING: " + e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new RuntimeException("ERROR WHEN PARSING: " + e.getMessage());
                }
            }
        });
    }


    public  static  void attemptLogin(final String username, final String pswd, final Context context, final LoginInActivity loginInActivity){
        RequestHandler handler = RequestHandler.getInstance();
        handler.makeRequest(IP + LOGIN  + "?username="+username+"&pswd="+pswd, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String res = new String(response, "utf8");
                    Log.d(TAG, "" + res);
                    HashMap<String, String> map = JsonParser.jsonToMap(res);
                    String result = map.get("result");
                    if(result.equals("1")){
                        loginInActivity.confirmLogin(username);
                    }
                    else if (result.equals("0")){
                        loginInActivity.giveLoginError("Wrong password");
                    }else{
                        loginInActivity.giveLoginError("Wrong user");
                    }

                } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public  static  void addParticipants(final Participant participant,final AppDatabase db,final Context context){
        RequestHandler handler = RequestHandler.getInstance();

        String d = IP + ADD_PARTICIPANT + "?albumid="+participant.getAlbumId().toString() +"&participantname="+participant.getUserName() + "&albumslicelink=" + participant.getAlbumSliceLink();
        handler.makeRequest(IP + ADD_PARTICIPANT + "?albumid="+participant.getAlbumId().toString() +"&participantname="+participant.getUserName() + "&albumslicelink=" + participant.getAlbumSliceLink(), new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String res = new String(response, "utf8");
                    Log.d(TAG, "" + res);
                    HashMap<String, String> map = JsonParser.jsonToMap(res);
                    String result = map.get("result");
                    if(result.equals("1")){
                        //participant.addToDb(db);
                        ;
                    }
                    else{
                        CharSequence text = "Failed to add participant!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
        participant.addToDb(db);
    }

    public  static  void getParticipants(String albumID, final Context context){
        RequestHandler handler = RequestHandler.getInstance();
        handler.makeRequest(IP + GET_PARTICIPANTS + "?albumid="+albumID, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String responseText = new String(response, "utf8");
                    Log.d(TAG, responseText);
                    List<Object> list = JsonParser.jsonArrayToMap(responseText);
                    AppDatabase db = AppDatabase.getAppDatabase(context);
                    for (Object object:list
                    ) {
                        HashMap<String, String> objectMap = (HashMap<String, String>) object;
                        Participant participant = Participant.instanceFromHashMap(objectMap);




                        Object a  = db.participantDao().getAll();
                        Object b  = db.albumDao().getAll();
                        Object c  = db.userDao().getAll();

                        //If participant does not exists
                        if (db.participantDao().findByKey(participant.getAlbumId(),  participant.getUserName(), participant.getAlbumSliceLink()) == null) {
                            db.participantDao().insertAll(participant);
                        }
                    }

                } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static  void attemptRegister(String username, String pswd, final RegisterActivity registerActivity){
        RequestHandler handler = RequestHandler.getInstance();
        handler.makeRequest(IP + "register?username="+username+"&pswd="+pswd, new RequestListener() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String res = new String(response, "utf8");
                    Log.d(TAG, "" + res);
                    HashMap<String, String> map = JsonParser.jsonToMap(res);
                    String result = map.get("result");
                    Log.d(TAG,result);
                    if (result.equals("1"))
                        registerActivity.sucessufulRegister();
                    else
                        registerActivity.notSucessufulRegister();
                } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void getAllAlbuns(final String username, final Context context){
        final AppDatabase db = AppDatabase.getAppDatabase(context);
        RequestHandler handler = RequestHandler.getInstance();
        handler.makeRequest(IP + GET_ALBUNS_FROM_USER + "?username="+username, new RequestListener() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String res = new String(response, "utf8");
                    Log.d(TAG, "" + res);
                    List<Object> list = JsonParser.jsonArrayToMap(res);
                    for (Object object:list
                    ) {
                        HashMap<String,String> objectMap = (HashMap<String, String>) object;
                        Album album = Album.instanceFromHashMap(objectMap);

                        //If user does not exists
                        if (db.albumDao().findById(album.getId()) == null) {
                            Album.addAlbumToDb(db, album, context);
                            getParticipants(album.getId().toString(), context);
                        }
                    }
                } catch (UnsupportedEncodingException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public static void updateAll(Context context){
        getAllUsers(context, User.getSelfUser().getName());
        getAllAlbuns(User.getSelfUser().getName(), context);

    }

}

