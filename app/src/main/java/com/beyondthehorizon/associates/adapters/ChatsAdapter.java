package com.beyondthehorizon.associates.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beyondthehorizon.associates.R;
import com.beyondthehorizon.associates.chats.CommentsChatActivity;
import com.beyondthehorizon.associates.database.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<ChatModel> proMemberArrayList;
    private ArrayList<ChatModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    private Context context;
    private FirebaseUser fuser;


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
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
        final ChatModel proMember = proMemberArrayList.get(position);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final String details;
        final int numberOfComments;

        if (proMember.getType().contains("Single")) {
            holder.commentLayout.setVisibility(View.GONE);
        }
        //Show you have been added to new group
//        if (TextUtils.isEmpty(proMember.getSenderName())) {
////            holder.r2.setVisibility(View.GONE);
////            holder.r2.setBackgroundColor(Color.TRANSPARENT);
////            holder.providerMessage.setText(proMember.getMessage());
////            holder.providerMessage.setBackgroundColor(0x900C0C0C);
////            holder.providerMessage.setGravity(Gravity.CENTER);
////            holder.message_time.setVisibility(View.GONE);
//            holder.commentLayout.setVisibility(View.GONE);
////            holder.providerName.setVisibility(View.GONE);
//        }
//        else {
        holder.message_time.setText(proMember.getTimestamp());
        holder.providerMessage.setText(proMember.getMessage());
//        }
        if (proMemberArrayList.get(position).getPhoneNumber().equals(fuser.getPhoneNumber())) {
            holder.providerName.setText("");
        } else {
            details = proMember.getSenderName() + " ~" + proMember.getPhoneNumber();
            holder.providerName.setText(details);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                if (proMember.getType().contains("Single") || proMember.getSenderName().isEmpty()) {
                    return;
                }
                Intent providerDetails = new Intent(context, CommentsChatActivity.class);
                providerDetails.putExtra("MainQuestionKey", proMember.getMessage_key());
                providerDetails.putExtra("RoomId", proMember.getSenderUID());
//                providerDetails.putExtra("Job", provider.getJob());
//                providerDetails.putExtra("Name", provider.getName()); Get rating
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
        TextView providerName, numberOfComments;
        TextView providerMessage, message_time, newGroup;
        View mView;
        LinearLayout commentLayout, r2;

        public ProMemberViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            providerName = (TextView) itemView.findViewById(R.id.name);
            providerMessage = (TextView) itemView.findViewById(R.id.message_body);
            message_time = (TextView) itemView.findViewById(R.id.message_time);
            numberOfComments = (TextView) itemView.findViewById(R.id.numberOfComments);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            newGroup = itemView.findViewById(R.id.newGroup);
            r2 = itemView.findViewById(R.id.r2);
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
            Log.d(TAG, "getItemViewType: MINE");
            return 0;
        } else {
            Log.d(TAG, "getItemViewType: THEIRS");
            return 1;
        }
    }
}