package com.example.xiao.gowhere.HomePage.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.xiao.gowhere.Controller.AdapterCallback;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Controller.SPController;
import com.example.xiao.gowhere.HomePage.adapters.HotelAdapter;
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

public class SearchFragment extends Fragment implements AdapterCallback {

    private final String TAG = "SearchForHotels";
    private ArrayList<HotelItem> hotels;

    View v;
    CardView searchCardView, searchInfoCardView;
    EditText CheckInDate, CheckOutDate,Destination, Room, Adult, Child;
    RadioButton businessBtn, leisureBtn;
    boolean radioButtonChecked = false;
    Button searchBtn;
    TextView emptyResultText, radioClickError, desText, peopleText, nightText;
    ImageButton searchIcon;
    RecyclerView recyclerView;
    HotelAdapter adapter;

    CalendarView calendar;
    Dialog dialog;
    private String checkInDate = "", checkOutDate = "";
    private boolean checkIn = false;
    int dayCount;

    private ProgressDialog pDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        hotels = new ArrayList<HotelItem>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        initView();
        return v;
    }

    private void initView() {
        // initialize views
        searchCardView = (CardView) v.findViewById(R.id.searchCardView);
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
        searchInfoCardView = (CardView) v.findViewById(R.id.searchInfoCardView);
        desText = (TextView) v.findViewById(R.id.desText);
        peopleText = (TextView) v.findViewById(R.id.peopleText);
        nightText = (TextView) v.findViewById(R.id.nightText);
        searchIcon = (ImageButton) v.findViewById(R.id.searchBtn);
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
                showCalendar(0);
                // set check-in date
                // CheckInDate.setText(checkInDate);
            }
        });
        CheckOutDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIn){
                    // show calendar
                    showCalendar(1);
                    // set check-out date
                    // CheckOutDate.setText(checkOutDate);
                }
                else {
                    Toast.makeText(getContext(),"Please select check-in date first!",Toast.LENGTH_SHORT).show();
                }
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
                // check input info availability
                if(infoComplete()){
                    sendSearchRequest();
                    initSearchInfoCardView();
                    searchCardView.setVisibility(View.GONE);
                    emptyResultText.setVisibility(View.GONE);
                    searchInfoCardView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    adapter = new HotelAdapter(getActivity(), hotels, SearchFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void initSearchInfoCardView() {
        desText.setText(Destination.getText().toString());
        // calculate people
        int adult = Integer.parseInt(Adult.getText().toString());
        int child = Integer.parseInt(Child.getText().toString());
        if(child>0){
            peopleText.setText(adult+" adults, "+child+" child");
        } else{
            peopleText.setText(adult+" adults");
        }
        // calculate nights
        nightText.setText(calculateNights() + " (" + checkInDate + " - " + checkOutDate + ")");
        // make a new search
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInfoCardView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                searchCardView.setVisibility(View.VISIBLE);
                emptyResultText.setVisibility(View.VISIBLE);
                hotels.clear();
            }
        });
    }

    private String calculateNights() {
        int check_in_month = Integer.parseInt(checkInDate.substring(0,2));
        int check_in_day = Integer.parseInt(checkInDate.substring(3,5));
        int check_in_year = Integer.parseInt(checkInDate.substring(6));
        int check_out_month = Integer.parseInt(checkOutDate.substring(0,2));
        int check_out_day = Integer.parseInt(checkOutDate.substring(3,5));
        int check_out_year = Integer.parseInt(checkOutDate.substring(6));
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        date1.clear();
        date1.set(check_in_year, check_in_month, check_in_day);
        date2.clear();
        date2.set(check_out_year, check_out_month, check_out_day);
        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();
        dayCount =(int) ((float) diff / (24 * 60 * 60 * 1000));
        if(dayCount>1)
            return ("" + dayCount + " nights");
        else
            return ("" + dayCount + " night");
    }

    private void sendSearchRequest() {
        show();
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
                    callAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG,error.toString());
                error.printStackTrace();
                dismiss();
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
        if(Room.getText().toString().isEmpty()|| Room.getText().toString().equals("0")){
            Room.setError("Please select room number!");
            completed = false;
        }
        if(Adult.getText().toString().isEmpty()|| Adult.getText().toString().equals("0")){
            Adult.setError("Please select adult number!");
            completed = false;
        }
        if(Child.getText().toString().isEmpty()){
            Child.setError("Please select child number!");
            completed = false;
        }
        if(!radioButtonChecked){
            radioClickError.setVisibility(View.VISIBLE);
            completed = false;
        }
        return completed;
    }

    private void showCalendar(final int source){
        dialog.show();
        dialog.setContentView(R.layout.calendar_dialog);
        calendar = (CalendarView) dialog.findViewById(R.id.calendarView);
        if(source == 0){
            // check-in date
            Calendar date = Calendar.getInstance();
            calendar.setMinDate(date.getTimeInMillis()); // current date, previous dates cannot be selected
            calendar.setDate(date.getTimeInMillis()); // current date
            date.add(Calendar.MONTH, 6);
            calendar.setMaxDate(date.getTimeInMillis()); // 6 months ahead
        } else {
            // check-out date, must after check-in date
            Calendar date = Calendar.getInstance();
            int month = Integer.parseInt(checkInDate.substring(0,2))-1;
            int day = Integer.parseInt(checkInDate.substring(3,5))+1;
            int year = Integer.parseInt(checkInDate.substring(6));
            date.set(year, month,day);
            calendar.setMinDate(date.getTimeInMillis()); // current date, previous dates cannot be selected
            calendar.setDate(date.getTimeInMillis()); // current date
            date.add(Calendar.MONTH, 6);
            date.add(Calendar.DATE, -1);
            calendar.setMaxDate(date.getTimeInMillis()); // 6 months ahead
        }
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                switch (source){
                    case 0:
                        checkIn = true;
                        checkInDate = String.format(Locale.getDefault(),"%02d/%02d/%04d",month+1, dayOfMonth, year);
                        CheckInDate.setText(checkInDate);
                        break;
                    case 1:
                        checkOutDate = String.format(Locale.getDefault(),"%02d/%02d/%04d",month+1, dayOfMonth, year);
                        CheckOutDate.setText(checkOutDate);
                        break;
                    default:
                        Toast.makeText(getContext(),"Wrong source!",Toast.LENGTH_SHORT).show();
                        break;
                }
                dialog.dismiss();
            }
        });
        //dialog.show();
    }

    private void callAdapter()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMethodCallback(int position) {
        HotelItem item = hotels.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("HotelId",item.getId());
        bundle.putString("HotelName", item.getName());
        bundle.putString("HotelAdd", item.getAddress());
        bundle.putDouble("HotelLat",item.getLatitude());
        bundle.putDouble("HotelLong",item.getLongitude());
        bundle.putInt("HotelRating", item.getRating());
        bundle.putString("HotelImage", item.getImgUrl());
        bundle.putString("CheckInDate", checkInDate);
        bundle.putString("CheckOutDate", checkOutDate);
        bundle.putInt("Nights",dayCount);
        HotelFragment fragment=new HotelFragment();
        fragment.setArguments(bundle);
        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.main_fragment_container,fragment).commit();
    }


    private void show(){
        if (!pDialog.isShowing()){
            pDialog.show();
            pDialog.setContentView(R.layout.progress_loading);
        }
    }

    private void dismiss(){
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}
