package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.ChatActivity;
import com.beyondthehorizon.associates.database.RecentChatModel;
import com.beyondthehorizon.associates.database.SendingImagesModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class SendingImagesAdapter extends RecyclerView.Adapter<SendingImagesAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<SendingImagesModel> providerModelArrayList;
    private Context ctx;
    private ArrayList<SendingImagesModel> sendingImagesModelArrayList;
    private SendMyTxtImage listener;
    private static final String TAG = "PAPA";

    public SendingImagesAdapter(Context ctx, ArrayList<SendingImagesModel> providerModelArrayList, SendMyTxtImage listener) {
        inflater = LayoutInflater.from(ctx);
        this.providerModelArrayList = providerModelArrayList;
        this.sendingImagesModelArrayList = providerModelArrayList;
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    public SendingImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.send_images, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SendingImagesAdapter.MyViewHolder holder, final int position) {
        final SendingImagesModel provider = providerModelArrayList.get(position);
        holder.theImage.setImageURI(Uri.fromFile(new File(provider.getImageUri())));
//        sendingImagesModelArrayList.add(new SendingImagesModel(provider.getImageUri(), ""));
        holder.imageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendingImagesModelArrayList.get(position).setTxtMessage("*hak*none0#");
                } else {
                    sendingImagesModelArrayList.get(position).setTxtMessage(holder.imageText.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.sendImgText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send text and image
                listener.sendTextImage(sendingImagesModelArrayList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView theImage, sendImgText;
        EditText imageText;

        public MyViewHolder(View itemView) {
            super(itemView);
            theImage = itemView.findViewById(R.id.theImage);
            imageText = itemView.findViewById(R.id.imageText);
            sendImgText = itemView.findViewById(R.id.sendImgText);
        }
    }

    public interface SendMyTxtImage {
        void sendTextImage(ArrayList<SendingImagesModel> arrayList);
    }
}