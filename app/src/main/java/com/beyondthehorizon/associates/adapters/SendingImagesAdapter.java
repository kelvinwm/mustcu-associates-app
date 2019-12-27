package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.SendingImagesModel;

import java.io.File;
import java.util.ArrayList;

public class SendingImagesAdapter extends RecyclerView.Adapter<SendingImagesAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<SendingImagesModel> providerModelArrayList;
    private Context ctx;
    public ArrayList<SendingImagesModel> sendingImagesModelArrayList;
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
        String items = position + 1 + "/" + providerModelArrayList.size() + " item(s)";
        holder.totalItems2.setText(items);
        if (provider.getMediaType().contains("1")) {
            holder.theImage.setVisibility(View.VISIBLE);
            holder.theImage.setImageURI(Uri.fromFile(new File(provider.getImageUri())));
        }
        if (provider.getMediaType().contains("0")) {
            holder.theFileLayout.setVisibility(View.VISIBLE);
            holder.theFileName.setText(provider.getMediaFile().getName());
        }
        if (provider.getMediaType().contains("3")) {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(provider.getImageUri()));
            MediaController mediaController = new MediaController(ctx);
            holder.videoView.setMediaController(mediaController);
            mediaController.setAnchorView(holder.videoView);
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    holder.videoView.seekTo(0);
                }
            });
        }
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
//
//        holder.sendImgText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //send text and image
//                listener.sendTextImage(sendingImagesModelArrayList);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView theImage, sendImgText, theFile;
        VideoView videoView;
        EditText imageText;
        LinearLayout theFileLayout;
        TextView theFileName, totalItems2;

        public MyViewHolder(View itemView) {
            super(itemView);
            theImage = itemView.findViewById(R.id.theImage);
            imageText = itemView.findViewById(R.id.imageText);
            theFileLayout = itemView.findViewById(R.id.theFileLayout);
            theFile = itemView.findViewById(R.id.theFile);
            theFileName = itemView.findViewById(R.id.theFileName);
            totalItems2 = itemView.findViewById(R.id.totalItems2);
//            sendImgText = itemView.findViewById(R.id.sendImgText);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }

    public interface SendMyTxtImage {
        void sendTextImage(ArrayList<SendingImagesModel> arrayList);
    }
}