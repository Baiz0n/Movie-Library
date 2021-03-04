package com.joel.movielibrary.Tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joel.movielibrary.R;
import com.joel.movielibrary.ui.MovieDetails;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    private static final String TAG = "RecycleAdapter";

    private ArrayList<String> mTitles;
    private ArrayList<String> mDescs;
    private ArrayList<Bitmap> mImages;
    private Context mContext;
    private boolean library;
    private ArrayList<String> imgUrl;

    public RecycleAdapter(ArrayList<String> mTitle, ArrayList<String> mDesc, ArrayList<Bitmap> mImages, Context mContext, boolean library, ArrayList<String> imgUrl) {
        this.mTitles = mTitle;
        this.mImages = mImages;
        this.mDescs = mDesc;
        this.mContext = mContext;
        this.library = library;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "CALLED VIEW HOLDER");

        Bitmap mLocalImage = mImages.get(position);
        String mLocalTitle = mTitles.get(position);
        String mLocalDesc = mDescs.get(position);

        holder.image.setImageBitmap(mLocalImage);
        holder.title.setText(mLocalTitle);
        holder.desc.setText(getDesc(mLocalDesc));

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetails.class);
                intent.putExtra("title", mTitles.get(position));
                intent.putExtra("desc", mDescs.get(position));
                //intent.putExtra("image", mImages.get(position));
                intent.putExtra("library", library);
                intent.putExtra("img_url", imgUrl.get(position));
                mContext.startActivity(intent);
            }
        });

    }

    private String getDesc(String longDesc) {

        String fixedDesc;

        int limit = 76;

        if ( limit < longDesc.length() ) {

            if ((longDesc.charAt(76) + "").equals(" ")) {

                fixedDesc = longDesc.substring(0,76) + "...";

            } else {

                fixedDesc = longDesc.substring(0,77) + "...";
            }
        } else {

            return longDesc;
        }

        return fixedDesc;
    }


    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView title;
        TextView desc;
        RelativeLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.list_image);
            title = itemView.findViewById(R.id.list_title);
            desc = itemView.findViewById(R.id.list_desc);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
