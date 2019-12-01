package com.beyondthehorizon.testfirebasefunctionstest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ProMemberViewHolder>
        implements Filterable {
    private ArrayList<ChatModel> proMemberArrayList;
    private ArrayList<ChatModel> filteredProMemberArrayList = new ArrayList<>();
    private static final String TAG = "PROMEMBER";
    Context context;


    public ChatsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.feedback_chat_item, parent, false);
        return new ChatsAdapter.ProMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProMemberViewHolder holder, int position) {

        final ChatModel proMember = proMemberArrayList.get(position);
        holder.feedbackSenderName.setText(proMember.getSenderName());
        holder.theFeedBack.setText(proMember.getMessage());
        holder.feedBackTime.setText(proMember.getTimestamp());

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
        TextView feedbackSenderName, theFeedBack, feedBackTime;

        public ProMemberViewHolder(View itemView) {
            super(itemView);
            feedbackSenderName = itemView.findViewById(R.id.feedbackSenderName);
            theFeedBack = itemView.findViewById(R.id.theFeedBack);
            feedBackTime = itemView.findViewById(R.id.feedBackTime);
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
}