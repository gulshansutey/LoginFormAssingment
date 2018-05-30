package com.technophile.userformassignment.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.technophile.userformassignment.database.MyDataBase;
import com.technophile.userformassignment.R;
import com.technophile.userformassignment.databinding.ActivityLoginBinding;
import com.technophile.userformassignment.fragments.UserInfoFragment;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {

    private LoginButton loginButton;
    ActivityLoginBinding activityLoginBinding;
    private MyDataBase myDataBase;
    private CallbackManager callbackManager;
    private ActionBar actionBar;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences("myPrefs",MODE_PRIVATE);
        myDataBase=new MyDataBase(this);
        callbackManager = CallbackManager.Factory.create();
        activityLoginBinding=DataBindingUtil.setContentView(this, R.layout.activity_login);
        activityLoginBinding.loginButton.registerCallback(callbackManager, this);
        actionBar=getSupportActionBar();
        setToolbarTitle("Login");
        List<String> permission =new ArrayList<>();
        permission.add("public_profile");
        permission.add("user_gender");
        permission.add("user_birthday");
        permission.add("email");
        activityLoginBinding.loginButton.setReadPermissions(permission);


        if (sharedPreferences.getBoolean("logged",false)){
            activityLoginBinding.rlLogin.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content,UserInfoFragment.newInstance(),"form").commitNow();
        }

    }

    public void setToolbarTitle(String title){
        if (actionBar!=null){
            actionBar.setTitle(title);
        }
    }




    @Override
    public void onSuccess(LoginResult loginResult) {

        AccessToken accessToken = loginResult.getAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn)
        getFbProfile(accessToken);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {
        Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void getFbProfile(AccessToken currentAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        logoutBtn.setVisible(true);
                        ContentValues values = new ContentValues();
                        String id=object.optString("id");
                        String img_url="http://graph.facebook.com/"+id+"/picture?type=large";
                        String name=object.optString("first_name")+" "+object.optString("last_name");
                        values.put(MyDataBase.KEY_NAME,name);
                        values.put(MyDataBase.KEY_EMAIL,object.optString("email"));
                        values.put(MyDataBase.KEY_GENDER,object.optString("gender"));
                        values.put(MyDataBase.KEY_AGE,getAge(object.optString("birthday")));
                        values.put(MyDataBase.KEY_ID,id);
                        values.put(MyDataBase.KEY_PIC,img_url);
                        myDataBase.addUser(values);
                        activityLoginBinding.rlLogin.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().add(android.R.id.content,UserInfoFragment.newInstance(),"form").commitNow();
                        sharedPreferences.edit().putBoolean("logged",true).apply();


                        }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    MenuItem logoutBtn;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu,menu);
        logoutBtn=menu.findItem(R.id.action_log_out);
        logoutBtn.setVisible(sharedPreferences.getBoolean("logged",false));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_log_out){
            sharedPreferences.edit().clear().apply();
            item.setVisible(false);
            setToolbarTitle("Login");
            LoginManager.getInstance().logOut();

            for (Fragment fragment:getSupportFragmentManager().getFragments()) {
                  if (fragment!=null) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
            activityLoginBinding.rlLogin.setVisibility(View.GONE);
            myDataBase.deleteDB();

        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode,  data);
    }

    private String getAge(String dateT){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm/dd/yyyy", Locale.US);
        Date date = null;
        try {
              date=simpleDateFormat.parse(dateT);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        return String.valueOf(age);
    }

}
