package pt.ulisboa.tecnico.cmov.p2photo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import serverConnection.Server;


//@F1

public class RegisterActivity extends AppCompatActivity {


    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private Button mButtonConfirm;
    private Button mButtonCancel;
    private ImageView mWaitingScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordConfirmView = findViewById(R.id.password_confirmation);

        mButtonConfirm = findViewById(R.id.button_confirm);
        mButtonCancel = findViewById(R.id.button_cancel);

        mWaitingScreen = findViewById(R.id.register_waiting_answer)      ;
        mWaitingScreen.setVisibility(View.GONE);
        Log.d("9999", "SHIT1111");
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister(mEmailView.getText().toString(),mPasswordConfirmView.getText().toString());
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void attemptRegister(String username, String pswd) {
        mWaitingScreen.setVisibility(View.VISIBLE);
        Server.attemptRegister(username, pswd, this);
        //handl do result
    }


    public void sucessufulRegister(){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, "REGISTER CONCLUDED", duration);
        toast.show();
        mWaitingScreen.setVisibility(View.GONE);
        finish();
    }

    public void notSucessufulRegister(){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, "User name already taken", duration);
        toast.show();
        mWaitingScreen.setVisibility(View.GONE);
    }

}
