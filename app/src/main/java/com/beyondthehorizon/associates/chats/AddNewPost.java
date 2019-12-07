package com.beyondthehorizon.associates.chats;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beyondthehorizon.associates.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddNewPost extends BottomSheetDialogFragment {
    private AddNewPostListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_new_post_message_area, container, false);

        final EditText message = v.findViewById(R.id.newPostMessage);
        ImageButton addNewPost = v.findViewById(R.id.newPostData);


        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = message.getText().toString().trim();
                if (sms.isEmpty()) {
                    return;
                }
                listener.addNewPostMessage(sms);
                message.setText("");
                dismiss();
            }
        });
        return v;
    }

    public interface AddNewPostListener {
        void addNewPostMessage(String message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddNewPostListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddProperty bottom sheet");
        }
    }
}
