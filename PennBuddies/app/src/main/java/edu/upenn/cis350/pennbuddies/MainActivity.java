package edu.upenn.cis350.pennbuddies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

//MongoDB Service Packages
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
// Utility Packages
import com.mongodb.stitch.core.internal.common.BsonUtils;

// Stitch Sync Packages
import com.mongodb.stitch.core.services.mongodb.remote.sync.ChangeEventListener;
import com.mongodb.stitch.core.services.mongodb.remote.sync.DefaultSyncConflictResolvers;
import com.mongodb.stitch.core.services.mongodb.remote.sync.ErrorListener;

// MongoDB Mobile Local Database Packages
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;

import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import android.text.TextUtils;
import android.app.ProgressDialog;
import android.os.AsyncTask;

//notifications
import android.os.Build;
import android.app.NotificationManager;
import android.app.NotificationChannel;


public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private StitchUser currentUser;

    @Override
    protected void onStart() {
        super.onStart();
        StitchAppClient client = Stitch.getAppClient("pennbuddies-yoero");
        currentUser = client.getAuth().getUser();
        if(currentUser != null){
            Log.e("active user?", "already logged in");
            startActivity(new Intent(MainActivity.this, Buddies.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });

//        final Button noAccount = findViewById(R.id.noAccount);
//
//        noAccount.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
//                startActivity(intent);
//            }
//        });


    }

    private void attemptLogin() {
        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        boolean abort = false;
        View focus = null;

        if (TextUtils.isEmpty(emailText)) {
            email.setError("Required Field");
            focus = email;
            abort = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("Invalid Email");
            focus = email;
            abort = true;
        }

        if (TextUtils.isEmpty(passwordText)) {
            password.setError("Empty Password");
            focus = password;
            abort = true;
        }

        if (abort) {
            focus.requestFocus();
        } else {
            new letsLogin().execute(emailText,passwordText);
        }
    }

    private class letsLogin extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress;
        private byte statusCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(MainActivity.this, "",
                    "PennBuddies is finding you...");
        }

        @Override
        protected Void doInBackground(String...params) {
            try {
                UserPasswordCredential credential = new UserPasswordCredential(params[0], params[1]);
                final StitchAppClient client = Stitch.getAppClient(getString(R.string.stitch_client_app_id));
                client.getAuth().loginWithCredential(credential).addOnCompleteListener
                        (new OnCompleteListener<StitchUser>() {
                    @Override
                    public void onComplete(@NonNull final Task<StitchUser> task){
                    if (task.isSuccessful()) {
                        statusCode = 1;
                        onPostExecute();
                    } else {
                        statusCode = 0;
                        onPostExecute();
                    }
                }
                });
            } catch (Exception e) {
                Log.e("stitch-auth", "Authentication failed");
            }
            return null;
        }

        protected void onPostExecute() {
            if (statusCode == 1) {
                Log.d("stitch-auth", "Login Successful!");
                Toast.makeText(MainActivity.this, "Welcome to PennBuddies!",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Buddies.class));
                progress.dismiss();
                finish();
            } else {
                Log.e("stitch-auth", "Login failed");
                progress.dismiss();
                Toast.makeText(MainActivity.this, "Email/Password combination does " +
                                "not exist in our records",
                        Toast.LENGTH_SHORT).show();
                password.setText("");
            }

        }

    }

}
