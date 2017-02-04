package com.example.xiao.gowhere.HomePage.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.xiao.gowhere.Controller.AdapterCallback;
import com.example.xiao.gowhere.Controller.AppController;
import com.example.xiao.gowhere.Model.HotelItem;
import com.example.xiao.gowhere.R;

import java.util.ArrayList;

/**
 * Created by Ricky on 2017/2/3.
 */

public class HotelAdapter extends RecyclerView.Adapter<HotelHolder> {

    private Context context;
    private ArrayList<HotelItem> list;
    private AdapterCallback callback;
    private ImageLoader loader;

    public HotelAdapter(Context context, ArrayList<HotelItem> list,AdapterCallback callback){
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @Override
    public HotelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_hotel,parent,false);
        HotelHolder holder = new HotelHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(HotelHolder holder, int position) {
        HotelItem item = list.get(position);
        // parse Image
        loader= AppController.getInstance().getImageLoader();
        loader.get(item.getImgUrl(), ImageLoader.getImageListener(holder.image,R.drawable.default_image,android.R.drawable.ic_dialog_alert));
        holder.image.setImageUrl(item.getImgUrl(), loader);
        holder.name.setText(item.getName());
        holder.address.setText(item.getAddress());
        holder.price.setText(String.valueOf(item.getPrice()));
        int rating = item.getRating();
        switch (rating){
            case 1:
                holder.star1.setImageResource(R.drawable.star_select);
                break;
            case 2:
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                break;
            case 3:
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                break;
            case 4:
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                holder.star4.setImageResource(R.drawable.star_select);
                break;
            case 5:
                holder.star1.setImageResource(R.drawable.star_select);
                holder.star2.setImageResource(R.drawable.star_select);
                holder.star3.setImageResource(R.drawable.star_select);
                holder.star4.setImageResource(R.drawable.star_select);
                holder.star5.setImageResource(R.drawable.star_select);
                break;
            default:
                Toast.makeText(context,"Wrong rating!",Toast.LENGTH_SHORT).show();
                break;
        }
        holder.cv.setTag(holder);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                HotelHolder holder = (HotelHolder) v.getTag();
                int position = holder.getLayoutPosition();
                Log.d("click search result "+position,"success");
                callback.onMethodCallback(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class HotelHolder extends RecyclerView.ViewHolder{
    CardView cv;
    NetworkImageView image;
    TextView name, address, price;
    ImageView star1, star2, star3, star4, star5;

    public HotelHolder(View itemView) {
        super(itemView);
        cv = (CardView) itemView.findViewById(R.id.hotelCardView);
        image = (NetworkImageView) itemView.findViewById(R.id.hotelImage);
        name = (TextView) itemView.findViewById(R.id.hotelName);
        address = (TextView) itemView.findViewById(R.id.hotelAddress);
        price = (TextView) itemView.findViewById(R.id.hotelPrice);
        star1 = (ImageView) itemView.findViewById(R.id.rate_star1);
        star2 = (ImageView) itemView.findViewById(R.id.rate_star2);
        star3 = (ImageView) itemView.findViewById(R.id.rate_star3);
        star4 = (ImageView) itemView.findViewById(R.id.rate_star4);
        star5 = (ImageView) itemView.findViewById(R.id.rate_star5);
    }
}