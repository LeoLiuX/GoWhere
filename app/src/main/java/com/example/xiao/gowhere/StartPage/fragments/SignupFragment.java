package com.example.xiao.gowhere.StartPage.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Controller.SPController;
import com.example.xiao.gowhere.HomePage.HomeActivity;
import com.example.xiao.gowhere.R;
import com.example.xiao.gowhere.StartPage.StartActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by liuxi on 2017/1/30.
 */

public class SignupFragment extends Fragment {
    private View view;

    private Button signupBtn;
    private EditText inputName;
    private EditText inputEmail;
    private EditText inputMobile;
    private EditText inputPassword;
    private EditText inputAddress;
    private TextView linkLogin;

    private final String baseUrl = "http://rjtmobile.com/ansari/ohr/ohrapp/ohr_reg.php";
    private final String TAG = "SignupFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        signupBtn = (Button) view.findViewById(R.id.signup_btn);
        inputName = (EditText) view.findViewById(R.id.signup_name);
        inputEmail = (EditText) view.findViewById(R.id.signup_email);
        inputMobile = (EditText) view.findViewById(R.id.signup_mobile);
        inputPassword = (EditText) view.findViewById(R.id.signup_password);
        inputAddress = (EditText) view.findViewById(R.id.signup_address);
        linkLogin = (TextView) view.findViewById(R.id.signup_link_login);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registRequest();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartActivity.getPager().setCurrentItem(0);
            }
        });
        return view;
    }

    public void registRequest(){
        StringRequest signupRequest = new StringRequest(Request.Method.GET, buildUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, response.toString());
                if (response.contains("successfully registered")){
                    Toast.makeText(getActivity(), "Sign Up Successful. Please Login.", Toast.LENGTH_SHORT).show();
                    StartActivity.getPager().setCurrentItem(0);
                }
                else {
                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(signupRequest, TAG);
    }

    private String buildUrl(){
        return baseUrl + "?name=" + inputName.getText().toString()
                + "&email=" + inputEmail.getText().toString()
                + "&mobile=" + inputMobile.getText().toString()
                + "&password=" + inputPassword.getText().toString()
                + "&userAdd=" + inputAddress.getText().toString();
    }
}
