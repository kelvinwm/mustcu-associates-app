package com.beyondthehorizon.associates.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
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
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.groupchat.CommentsChatActivity;
import com.beyondthehorizon.associates.database.ChatModel;
import com.beyondthehorizon.associates.viewdetails.AndroidMediaPlayerActivity;
import com.beyondthehorizon.associates.viewdetails.ViewImageActivity;
import com.beyondthehorizon.associates.viewmodels.ChatsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import static com.beyondthehorizon.associates.MainActivity.CHAT_PREFS;
import static com.beyondthehorizon.associates.MainActivity.FriendUID;
import static com.beyondthehorizon.associates.MainActivity.MyFriendName;
import static com.beyondthehorizon.associates.util.Constants.Delivered;
import static com.beyondthehorizon.associates.util.Constants.Failed;
import static com.beyondthehorizon.associates.util.Constants.NothingToSend;
import static com.beyondthehorizon.associates.util.Constants.Sending;
import static com.beyondthehorizon.associates.util.Constants.Sent;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<ChatModel> proMemberArrayList;
    private ArrayList<ChatModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    private Context context;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private FirebaseUser fuser;
    private ChatsViewModel chatsViewModel;
    public MediaPlayer mPlayer;


    public ChatsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.their_message, parent, false);
            return new ChatsAdapter.ProMemberViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.my_message, parent, false);
            return new ChatsAdapter.ProMemberViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ProMemberViewHolder holder, int position) {
        SharedPreferences pref = context.getSharedPreferences(CHAT_PREFS, 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        chatsViewModel = ViewModelProviders.of((FragmentActivity) context).get(ChatsViewModel.class);
        final ChatModel proMember = proMemberArrayList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String details;
        holder.myFrame.setVisibility(View.GONE);

        if (proMember.getType().contains("Single")) {
            holder.commentLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }
        holder.message_time.setText(proMember.getTimestamp());
        holder.providerMessage.setText(proMember.getMessage());
        holder.numberOfComments.setText(proMember.getComments());
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

        if (proMember.delivery_status.contains(Sent)) {
            myRef.child("Users").child("UserChats").child(proMember.getMessage_key()).child("delivery_status").setValue("Seen");
            chatsViewModel.updateDeliveryStatus(proMember.message_key, Delivered);
        }
        if (proMember.delivery_status.contains("Seen")) {
            holder.del_status.setTextColor(Color.parseColor("#0398fc"));
            holder.del_status.setText(proMember.getDelivery_status());
        } else {
            holder.del_status.setText(proMember.getDelivery_status());
        }

        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            holder.providerName.setText("");
        } else {
            details = proMember.getSenderName() + " ~" + proMember.getPhoneNumber();
            holder.providerName.setText(details);
        }

        //VIEW COMMENTS
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proMember.getType().contains("Single") || proMember.getSenderName().isEmpty()) {
                    return;
                }
                if(proMember.getDelivery_status().contains(Sending)
                || proMember.getDelivery_status().contains(Failed)){

                    Log.d(TAG, "onClick: ");
                    Toast.makeText(context, "message not delivered..", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent providerDetails = new Intent(context, CommentsChatActivity.class);
                providerDetails.putExtra("MainQuestionKey", proMember.getMessage_key());
                providerDetails.putExtra("RoomId", proMember.getSenderUID());
                context.startActivity(providerDetails);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (proMemberArrayList != null) {
            return proMemberArrayList.size();
        } else {
            return 0;
        }
    }

    public void setProMemberArrayList(ArrayList<ChatModel> proMembers) {
        this.proMemberArrayList = proMembers;
        notifyDataSetChanged();
        this.filteredProMemberArrayList = proMemberArrayList;
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
            mView = itemView;
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
            ArrayList<ChatModel> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList = filteredProMemberArrayList;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ChatModel item : filteredProMemberArrayList) {
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
            proMemberArrayList = (ArrayList<ChatModel>) filterResults.values;
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