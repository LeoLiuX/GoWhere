package com.example.xiao.gowhere.HomePage.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.HomePage.adapters.RoomAdapter;
import com.example.xiao.gowhere.Model.RoomItem;
import com.example.xiao.gowhere.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ricky on 2017/2/3.
 */

public class HotelFragment extends Fragment {
    private String TAG = "HotelFragment";

    private String hotel_id, hotel_name, hotel_add, hotel_image, checkInDate, checkOutDate;
    private double hotel_lat, hotel_long; // offline map
    private int hotel_rating, night;
    ArrayList<RoomItem> rooms;
    private ProgressDialog pDialog;

    View v;
    TextView HotelName, HotelAddress,checkinDate, checkoutDate;
    ImageView star1, star2, star3, star4, star5;
    NetworkImageView HotelImage;
    ImageLoader loader;
    RecyclerView recyclerView;
    RoomAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        hotel_id = bundle.getString("HotelId");
        hotel_name = bundle.getString("HotelName");
        hotel_add = bundle.getString("HotelAdd");
        hotel_lat = bundle.getDouble("HotelLat");
        hotel_long = bundle.getDouble("HotelLong");
        hotel_rating = bundle.getInt("HotelRating");
        hotel_image = bundle.getString("HotelImage");
        checkInDate = bundle.getString("CheckInDate");
        checkOutDate = bundle.getString("CheckOutDate");
        night = bundle.getInt("Nights");
        rooms = new ArrayList<>();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        String urlRoom = "http://rjtmobile.com/ansari/ohr/ohrapp/room_detail.php?&hotelID="+hotel_id;
        sendJsonRequest(urlRoom);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_hotel, container, false);
        // init views
        HotelName = (TextView) v.findViewById(R.id.hotel_name);
        HotelAddress = (TextView) v.findViewById(R.id.hotel_address);
        HotelImage = (NetworkImageView) v.findViewById(R.id.hotel_image);
        checkinDate = (TextView) v.findViewById(R.id.checkinDate);
        checkoutDate = (TextView) v.findViewById(R.id.checkoutDate);
        star1 = (ImageView) v.findViewById(R.id.star1);
        star2 = (ImageView) v.findViewById(R.id.star2);
        star3 = (ImageView) v.findViewById(R.id.star3);
        star4 = (ImageView) v.findViewById(R.id.star4);
        star5 = (ImageView) v.findViewById(R.id.star5);
        recyclerView = (RecyclerView) v.findViewById(R.id.roomRecyclerView);
        // init values
        HotelName.setText(hotel_name);
        HotelAddress.setText(hotel_add);
        checkinDate.setText(checkInDate);
        checkoutDate.setText(checkOutDate);
        // parse Image
        loader= AppController.getInstance().getImageLoader();
        loader.get(hotel_image, ImageLoader.getImageListener(HotelImage,R.drawable.default_image,android.R.drawable.ic_dialog_alert));
        HotelImage.setImageUrl(hotel_image, loader);
        // set rating
        switch(hotel_rating){
            case 1:
                star1.setImageResource(R.drawable.star_select);
                break;
            case 2:
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                break;
            case 3:
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                break;
            case 4:
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                star4.setImageResource(R.drawable.star_select);
                break;
            case 5:
                star1.setImageResource(R.drawable.star_select);
                star2.setImageResource(R.drawable.star_select);
                star3.setImageResource(R.drawable.star_select);
                star4.setImageResource(R.drawable.star_select);
                star5.setImageResource(R.drawable.star_select);
                break;
            default:
                Toast.makeText(getActivity(),"Wrong rating!",Toast.LENGTH_SHORT).show();
                break;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RoomAdapter(getActivity(), rooms, night);
        recyclerView.setAdapter(adapter);
        return v;
    }

    private void sendJsonRequest(String url) {
        show();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arr = response.getJSONArray("Rooms");
                    for(int i=0; i<arr.length(); i++){
                        JSONObject room = arr.getJSONObject(i);
                        RoomItem item = new RoomItem();
                        item.setId(room.getString("roomId"));
                        item.setStyle(room.getString("roomStyle"));
                        item.setPrice(room.getInt("roomPrice"));
                        item.setImgUrl(room.getString("roomThumb"));
                        rooms.add(i, item);
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
                VolleyLog.d(TAG, "ERROR" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        AppController.getInstance().addToRequestQueue(req, TAG);
    }

    private void callAdapter() {
        adapter.notifyDataSetChanged();
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
