package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.CommentsModel;
import com.beyondthehorizon.associates.viewdetails.AndroidMediaPlayerActivity;
import com.beyondthehorizon.associates.viewdetails.ViewImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.util.Constants.NothingToSend;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<CommentsModel> proMemberArrayList;
    private ArrayList<CommentsModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    private Context context;
    private FirebaseUser fuser;


    public CommentsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.their_message, parent, false);
            return new CommentsAdapter.ProMemberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.my_message, parent, false);
            return new CommentsAdapter.ProMemberViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ProMemberViewHolder holder, int position) {

        final CommentsModel proMember = proMemberArrayList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String details;
        holder.commentLayout.setVisibility(View.GONE);
        holder.divider.setVisibility(View.GONE);
        SharedPreferences pref = context.getSharedPreferences(CHAT_PREFS, 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        holder.myFrame.setVisibility(View.GONE);


        holder.message_time.setText(proMember.getTimestamp());
        holder.providerMessage.setText(proMember.getMessage());
        holder.del_status.setTextColor(Color.parseColor("#000000"));

        //SHOW IMAGE
        if (proMember.getImageUrl().contains(NothingToSend)) {
            holder.sendImage.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onBindViewHolder: " + proMember.getImageUrl());
            holder.myFrame.setVisibility(View.VISIBLE);
            holder.sendImage.setVisibility(View.VISIBLE);
            holder.myRel.setVisibility(View.VISIBLE);
            holder.providerMessage.setText(proMember.getDocName());
            holder.sendImage.setImageURI(Uri.fromFile(new File(proMember.getImageUrl())));
            holder.sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.putExtra("Uri", proMember.getImageUrl());
                    intent.putExtra("mediaType", "image");
                    context.startActivity(intent);
                }
            });
        }

        //SHOW VIDEO
        if (proMember.getVideoUrl().contains(NothingToSend)) {
            holder.videoView1.setVisibility(View.GONE);
            holder.playVideo.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onBindViewHolder: " + proMember.getVideoUrl());
            holder.myFrame.setVisibility(View.VISIBLE);
            holder.playVideo.setVisibility(View.VISIBLE);
            holder.videoView1.setVisibility(View.VISIBLE);
            holder.providerMessage.setText(proMember.getDocName());
            holder.myRel.setVisibility(View.VISIBLE);
            holder.videoView1.setVideoURI(Uri.parse(proMember.getVideoUrl()));
            holder.playVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewImageActivity.class);
                    intent.putExtra("Uri", proMember.getVideoUrl());
                    intent.putExtra("mediaType", "video");
                    context.startActivity(intent);
                }
            });

            holder.videoView1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    holder.videoView1.seekTo(0);
                }
            });
        }

        //SHOW FILE
        if (proMember.getFileUrl().contains(NothingToSend)) {
            holder.fileLayout.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "onBindViewHolder: " + proMember.getVideoUrl());
            holder.fileLayout.setVisibility(View.VISIBLE);
            holder.fileImage.setVisibility(View.VISIBLE);
            holder.fileName.setVisibility(View.VISIBLE);
            holder.fileName.setText(proMember.getDocName());
            holder.fileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(proMember.getFileUrl());
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // New Approach
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Uri apkURI = FileProvider.getUriForFile(context, context.getApplicationContext()
                                .getPackageName() + ".provider", file);
                        install.setDataAndType(apkURI, "*/*");
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        // End New Approach
                    } else {
                        // Old Approach
                        install.setDataAndType(Uri.fromFile(file), "*/*");
                    }
                    context.startActivity(install);
                }
            });
        }
        //SHOW AUDIO
        if (proMember.getAudioUrl().contains(NothingToSend)) {
            holder.audioLayout.setVisibility(View.GONE);
        } else {
            holder.audioLayout.setVisibility(View.VISIBLE);
//            holder.playAudio.setVisibility(View.VISIBLE);
            holder.providerMessage.setText(proMember.getDocName());
            final boolean[] isPlaying = {false};
            Uri myUri = Uri.parse(proMember.getAudioUrl());

            holder.playAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    editor.putString("audioUri", proMember.getAudioUrl());
//                    editor.putString("audioName", proMember.getDocName());
//                    editor.apply();
                    Intent intent = new Intent(context, AndroidMediaPlayerActivity.class);
                    intent.putExtra("audioUri", proMember.getAudioUrl());
                    intent.putExtra("audioName", proMember.getDocName());
                    context.startActivity(intent);
                }
            });
        }

        holder.del_status.setText(proMember.getDeliveryState());

        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            holder.providerName.setText("");
        } else {
            details = proMember.getSenderName() + " ~" + proMember.getPhoneNumber();
            holder.providerName.setText(details);
        }

    }


    @Override
    public int getItemCount() {
        if (proMemberArrayList != null) {
            return proMemberArrayList.size();
        } else {
            return 0;
        }
    }

    public void setProMemberArrayList(ArrayList<CommentsModel> proMembers) {
        this.proMemberArrayList = proMembers;
        this.filteredProMemberArrayList = proMemberArrayList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return filteredProvidersList;
    }

    public static class ProMemberViewHolder extends RecyclerView.ViewHolder {
        TextView providerName, numberOfComments, del_status, message_time, fileName, audioTime;
        ImageView sendImage, playVideo, fileImage;
        ImageButton playAudio;
        EmojiconTextView providerMessage;
        View mView, divider;
        LinearLayout commentLayout, r2, fileLayout, audioLayout;
        RelativeLayout myRel;
        FrameLayout myFrame;
        VideoView videoView1;
        SeekBar audioProgrs;

        public ProMemberViewHolder(View itemView) {
            super(itemView);
            providerName = (TextView) itemView.findViewById(R.id.name);
            providerMessage = (EmojiconTextView) itemView.findViewById(R.id.message_body);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            numberOfComments = (TextView) itemView.findViewById(R.id.numberOfComments);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            divider = itemView.findViewById(R.id.divider);
            del_status = itemView.findViewById(R.id.del_status);
            sendImage = itemView.findViewById(R.id.sendImage);
            playVideo = itemView.findViewById(R.id.playVideo);
            r2 = itemView.findViewById(R.id.r2);
            fileLayout = itemView.findViewById(R.id.fileLayout);
            myFrame = itemView.findViewById(R.id.myFrame);
            fileImage = itemView.findViewById(R.id.fileImage);
            myRel = itemView.findViewById(R.id.myRel);
            videoView1 = itemView.findViewById(R.id.videoView1);
            fileName = itemView.findViewById(R.id.fileName);
            audioLayout = itemView.findViewById(R.id.audioLayout);
            playAudio = itemView.findViewById(R.id.playAudio);
            audioProgrs = itemView.findViewById(R.id.audioProgrs);
            audioTime = itemView.findViewById(R.id.audioTime);
        }
    }

    private Filter filteredProvidersList = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<CommentsModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList = filteredProMemberArrayList;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (CommentsModel item : filteredProMemberArrayList) {
                    if (item.getMessage().toLowerCase().contains(filterPattern) ||
                            item.getSenderName().toLowerCase().contains(filterPattern) ||
                            item.getPhoneNumber().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            proMemberArrayList = (ArrayList<CommentsModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            return 0;
        } else {
            return 1;
        }
    }
}