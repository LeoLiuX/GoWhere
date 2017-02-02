package com.example.xiao.gowhere.StartPage.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Controller.SPController;
import com.example.xiao.gowhere.HomePage.HomeActivity;
import com.example.xiao.gowhere.R;
import com.example.xiao.gowhere.StartPage.StartActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liuxi on 2017/1/30.
 */

public class LoginFragment extends Fragment {

    private View view;
    private final String baseUrl = "http://rjtmobile.com/ansari/ohr/ohrapp/ohr_login.php";

    private static final String TAG = "LoginFragment";


    private Button loginBtn;
    private EditText inputMobile;
    private EditText inputPassword;
    private TextView linkSignup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SPController.getInstance(getActivity()).hasUserLoggedIn()){
            startActivity(new Intent(getActivity(), HomeActivity.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);

        loginBtn = (Button) view.findViewById(R.id.login_btn);
        inputMobile = (EditText) view.findViewById(R.id.login_mobile);
        inputPassword = (EditText) view.findViewById(R.id.login_password);
        linkSignup = (TextView) view.findViewById(R.id.login_link_signup);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });

        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment signup = new SignupFragment();
                if (!inputMobile.getText().equals("")){
                    Bundle bundle = new Bundle();
                    bundle.putString("MOBILE", inputMobile.getText().toString());
                    signup.setArguments(bundle);
                }
                StartActivity.getPager().setCurrentItem(1);
            }
        });

        return view;
    }

    private void checkLogin(){
        StringRequest loginRequest = new StringRequest(Request.Method.GET, buildUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("success")){
                    // JSONArray
                    try {
                        JSONArray msgArr = new JSONArray(response);
                        JSONObject result = msgArr.getJSONObject(0);
                        SPController.getInstance(getContext()).setName(result.getString("UserName"));
                        SPController.getInstance(getContext()).setAddress(result.getString("UserAddress"));
                        SPController.getInstance(getContext()).setEmail(result.getString("UserEmail"));
                        SPController.getInstance(getContext()).setMobile(result.getString("UserMobile"));
                        SPController.getInstance(getContext()).setPwd(inputPassword.getText().toString());
                        Toast.makeText(getActivity(), "Welcome Back " + result.getString("UserName"), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else{
                    //JSONObject
                    Toast.makeText(getActivity(), "Invalid mobile or password! Please Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(loginRequest, TAG);
    }

    private String buildUrl(){
        return baseUrl + "?mobile=" + inputMobile.getText().toString() + "&password=" + inputPassword.getText().toString();
    }

}
