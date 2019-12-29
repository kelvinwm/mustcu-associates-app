package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.database.SendingImagesModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.beyondthehorizon.associates.util.Constants.NothingToSend;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SendingImagesAdapter extends RecyclerView.Adapter<SendingImagesAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<SendingImagesModel> providerModelArrayList;
    private Context ctx;
    public ArrayList<SendingImagesModel> sendingImagesModelArrayList;
    private SendMyTxtImage listener;
    private static final String TAG = "PAPA";
    public MediaPlayer mPlayer;

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
        //AUDIO
        if (provider.getMediaType().contains("2")) {
            holder.audioLayout.setVisibility(View.VISIBLE);
            holder.playAudio.setBackgroundResource(R.drawable.ic_play_arrow);
            final boolean[] isPlaying = {false};
            Uri myUri = Uri.parse(provider.getImageUri());
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(ctx, myUri);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            holder.audioProgrs.setClickable(false);
            int startTime = mPlayer.getDuration();

            holder.audioTime.setText(String.format("%d min %d sec", TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                            toMinutes((long) startTime)))
            );

            holder.playAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying[0]) {
                        //PAUSED
                        mPlayer.pause();
                        isPlaying[0] = false;
                        holder.playAudio.setBackgroundResource(R.drawable.ic_play_arrow);
                    } else {
                        //PLAYING AUDIO
                        mPlayer.start();
                        isPlaying[0] = true;
                        holder.playAudio.setBackgroundResource(R.drawable.ic_pause);
                        holder.audioProgrs.setMax(mPlayer.getDuration() / 1000);


                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                holder.audioProgrs.setProgress(mPlayer.getCurrentPosition() / 1000);

                                if (mPlayer.getDuration() / 1000 == mPlayer.getCurrentPosition() / 1000) {
                                    holder.playAudio.setBackgroundResource(R.drawable.ic_play_arrow);
                                    isPlaying[0] = false;
                                    mPlayer.seekTo(0);
                                    holder.audioProgrs.setProgress(0);
                                    mPlayer.pause();
                                }
                            }
                        }, 0, 1000);

                    }
                }
            });

            // DRAG SONG TO POSITION
            holder.audioProgrs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mPlayer != null && fromUser) {
                        mPlayer.seekTo(progress * 1000);
                    }
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
                    sendingImagesModelArrayList.get(position).setTxtMessage(NothingToSend);
                } else {
                    sendingImagesModelArrayList.get(position).setTxtMessage(holder.imageText.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return providerModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView theImage, sendImgText, theFile, playAudio;
        VideoView videoView;
        EditText imageText;
        LinearLayout theFileLayout, audioLayout;
        TextView theFileName, totalItems2, audioTime;
        SeekBar audioProgrs;

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
            playAudio = itemView.findViewById(R.id.playAudio);
            audioLayout = itemView.findViewById(R.id.audioLayout);
            audioProgrs = itemView.findViewById(R.id.audioProgrs);
            audioTime = itemView.findViewById(R.id.audioTime);
        }
    }

    public interface SendMyTxtImage {
        void sendTextImage(ArrayList<SendingImagesModel> arrayList, String imageUrl, String videoUrl,
                           String audioUrl, String fileUrl, String profileUrl, String friend_Uid,
                           String chatType, String myFriend_Name);
    }
}