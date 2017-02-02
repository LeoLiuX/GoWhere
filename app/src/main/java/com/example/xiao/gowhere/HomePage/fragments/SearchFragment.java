package com.example.xiao.gowhere.HomePage.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Controller.SPController;
import com.example.xiao.gowhere.Model.HotelItem;
import com.example.xiao.gowhere.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ricky on 2017/2/1.
 */

public class SearchFragment extends Fragment{

    private final String TAG = "SearchForHotels";
    private ArrayList<HotelItem> hotels;
    private String selectedDate = "";

    View v;
    EditText CheckInDate, CheckOutDate,Destination, Room, Adult, Child;
    RadioButton businessBtn, leisureBtn;
    Button searchBtn;
    TextView emptyResultText, radioClickError;
    RecyclerView recyclerView;
    boolean radioButtonChecked = false;
    CalendarView calendar;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        v = inflater.inflate(R.layout.fragment_search, container, false);
        hotels = new ArrayList<HotelItem>();
        initView();
        return v;
    }

    private void initView() {
        // initialize views
        Destination = (EditText) v.findViewById(R.id.search_destination);
        CheckInDate = (EditText) v.findViewById(R.id.search_checkin);
        CheckOutDate = (EditText) v.findViewById(R.id.search_checkout);
        Room = (EditText) v.findViewById(R.id.search_room);
        Adult = (EditText) v.findViewById(R.id.search_adult);
        Child = (EditText) v.findViewById(R.id.search_children);
        businessBtn = (RadioButton) v.findViewById(R.id.search_business);
        leisureBtn = (RadioButton) v.findViewById(R.id.search_leisure);
        radioClickError = (TextView) v.findViewById(R.id.radioClickError);
        searchBtn = (Button) v.findViewById(R.id.search_button);
        emptyResultText = (TextView) v.findViewById(R.id.emptyResultText);
        recyclerView = (RecyclerView) v.findViewById(R.id.hotelRecyclerView);
        // initialize text
        Destination.setText(SPController.getInstance(getContext()).getAddress());
        Room.setText("1");
        Adult.setText("2");
        Child.setText("0");
        String initDate;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            initDate = dateFormat.format(new Date());
        } else {
            int month = Calendar.getInstance().get(Calendar.MONTH)+1;
            initDate = String.format(Locale.getDefault(),"%02d/%02d/%04d",
                    month, Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.YEAR));
        }
        CheckInDate.setText(initDate);
        CheckOutDate.setText(initDate);
        // set OnClickListener
        CheckInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show calendar
                showCalendar();
                // set check-in date
                CheckInDate.setText(selectedDate);
            }
        });
        CheckOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show calendar
                showCalendar();
                // set check-out date
                CheckOutDate.setText(selectedDate);
            }
        });
        businessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonChecked = true;
                radioClickError.setVisibility(View.GONE);
            }
        });
        leisureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonChecked = true;
                radioClickError.setVisibility(View.GONE);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"search",Toast.LENGTH_SHORT).show();
                // check input info availability
                if(infoComplete()){
                    emptyResultText.setVisibility(View.GONE);
                    sendSearchRequest();
                    recyclerView.setVisibility(View.VISIBLE);
                    // set adapter
                }
            }
        });
    }

    private void sendSearchRequest() {
        String url = "http://rjtmobile.com/ansari/ohr/ohrapp/search_hotel.php";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arr = response.getJSONArray("Hotels");
                    for(int i=0; i<arr.length(); i++){
                        JSONObject result = arr.getJSONObject(i);
                        HotelItem item = new HotelItem();
                        item.setId(result.getString("hotelId"));
                        item.setName(result.getString("hotelName"));
                        item.setAddress(result.getString("hotelAdd"));
                        item.setLatitude(result.getDouble("hotelLat"));
                        item.setLongitude(result.getDouble("hotelLong"));
                        item.setRating(result.getInt("hotelRating"));
                        item.setPrice(result.getInt("price"));
                        item.setImgUrl(result.getString("hotelThumb"));
                        hotels.add(i, item);
                    }
                    // notify data changes
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.toString());
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(req,TAG);
    }

    private boolean infoComplete() {
        boolean completed = true;
        if(Destination.getText().toString().isEmpty()){
            Destination.setError("Please input your destination!");
            completed = false;
        }
        if(CheckInDate.getText().toString().isEmpty()){
            CheckInDate.setError("Please select the check-in date!");
            completed = false;
        }
        if(CheckOutDate.getText().toString().isEmpty()){
            CheckOutDate.setError("Please select the check-out date!");
            completed = false;
        }
        if(Room.getText().toString().isEmpty()){
            Room.setError("Please select room number!");
            completed = false;
        }
        if(Adult.getText().toString().isEmpty()){
            Adult.setError("Please select room number!");
            completed = false;
        }
        if(Child.getText().toString().isEmpty()){
            Child.setError("Please select room number!");
            completed = false;
        }
        if(!radioButtonChecked){
            radioClickError.setVisibility(View.VISIBLE);
            completed = false;
        }
        return completed;
    }

    private void showCalendar(){
        dialog.show();
        dialog.setContentView(R.layout.calendar_dialog);
        calendar = (CalendarView) dialog.findViewById(R.id.calendarView);
        Calendar date = Calendar.getInstance();
        calendar.setMinDate(date.getTimeInMillis()); // current date, previous dates cannot be selected
        calendar.setDate(date.getTimeInMillis()); // current date
        date.add(Calendar.MONTH, 6);
        calendar.setMaxDate(date.getTimeInMillis()); // 6 months ahead
        //Calendar.getInstance().add(Calendar.MONTH, -6);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.getDefault(),"%02d/%02d/%04d",month+1, dayOfMonth, year);
                dialog.dismiss();
            }
        });
        //dialog.show();
    }


}
