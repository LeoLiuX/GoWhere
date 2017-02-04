package com.example.xiao.gowhere.HomePage.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Model.RoomItem;
import com.example.xiao.gowhere.R;

import java.util.ArrayList;

/**
 * Created by Ricky on 2017/2/4.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomHolder> {

    private Context context;
    private ArrayList<RoomItem> list;
    private int night;
    private ImageLoader loader;

    public RoomAdapter(Context context, ArrayList<RoomItem> list, int night){
        this.context = context;
        this.list = list;
        this.night = night;
    }

    @Override
    public RoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room,parent,false);
        RoomHolder holder = new RoomHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RoomHolder holder, int position) {
        RoomItem item = list.get(position);
        // parse Image
        loader= AppController.getInstance().getImageLoader();
        loader.get(item.getImgUrl(), ImageLoader.getImageListener(holder.image,R.drawable.default_image,android.R.drawable.ic_dialog_alert));
        holder.image.setImageUrl(item.getImgUrl(), loader);
        holder.style.setText(item.getStyle());
        holder.price.setText(item.getPrice());
        holder.total.setText(night * item.getPrice());
        holder.bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Book  this room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class RoomHolder extends RecyclerView.ViewHolder{

    NetworkImageView image;
    TextView style, price, total;
    Button bookBtn;

    public RoomHolder(View itemView) {
        super(itemView);
        image = (NetworkImageView) itemView.findViewById(R.id.roomImage);
        style = (TextView) itemView.findViewById(R.id.roomStyle);
        price = (TextView) itemView.findViewById(R.id.roomPrice);
        total = (TextView) itemView.findViewById(R.id.totalPrice);
        bookBtn = (Button) itemView.findViewById(R.id.bookingBtn);
    }
}