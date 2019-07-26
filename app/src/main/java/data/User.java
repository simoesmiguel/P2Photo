package data;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.p2photo.R;

@Entity(tableName = "User")
public class User extends Object implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "name")
    @NonNull
    protected String name;

    @ColumnInfo(name ="color")
    int color;

    @Ignore
    private static User selfUser = null;

    @Ignore
    public static void setSelfUser(User selfUser) {
        User.selfUser = selfUser;
    }

    @Ignore
    public static User getSelfUser(){
        return selfUser;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public User() {
    }

    @Ignore
    public User(String name, Context context) {
        this.name = name;
        setColor(generateColor(context));
    }


    public static User instanceFromHashMap(HashMap<String,String> map, Context context){
        return new User(map.get("username"), context);
    }

    private int randomInterpolate(double a, double b) {
        return (int) Math.round((a + ((b - a) * Math.random())));
    }

    private static int rgbToInt(int red, int green, int blue) {
        return 0xff000000 | (red << 16) | (green << 8) | blue;
    }

    /** Returns an interpoloated color, between <code>a</code> and <code>b</code> */
    private int generateColor(Context context) {
        int red,green,blue;
        int baseColor = ContextCompat.getColor(context, R.color.colorPrimary);
        int endColor = ContextCompat.getColor(context, R.color.colorGreenSh1);
        red = Math.round(randomInterpolate(Color.red(baseColor), Color.red(endColor)));
        green = Math.round(randomInterpolate(Color.green(baseColor), Color.green(endColor)));
        blue = Math.round(randomInterpolate(Color.blue(baseColor), Color.blue(endColor)));
        return rgbToInt(red,green,blue);
    }

    public boolean equals(Object o) {
        if (!(o instanceof User)) {
            return false;
        }
        User other = (User) o;
        return name.equals(other.name);
    }
}
