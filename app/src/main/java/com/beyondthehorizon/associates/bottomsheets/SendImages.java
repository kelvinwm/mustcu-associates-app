package com.beyondthehorizon.associates.bottomsheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beyondthehorizon.associates.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SendImages extends BottomSheetDialogFragment {
    private SendImagesListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.send_images, container, false);

        final String[] gender = new String[1];
        gender[0] = "Male";
        final EditText propName = v.findViewById(R.id.imageText);
        ImageView theImage = v.findViewById(R.id.theImage);
        ImageView sendImgText = v.findViewById(R.id.sendImgText);


        return v;
    }

    public interface SendImagesListener {
        void addMember(String name, String description, String gender);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SendImagesListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddProperty bottom sheet");
        }
    }
}
