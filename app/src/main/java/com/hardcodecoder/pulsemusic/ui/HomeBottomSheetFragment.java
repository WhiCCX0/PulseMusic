package com.hardcodecoder.pulsemusic.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hardcodecoder.pulsemusic.GlideApp;
import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.activities.AppInfo;
import com.hardcodecoder.pulsemusic.activities.SettingsActivity;
import com.hardcodecoder.pulsemusic.utils.UserInfo;

import java.util.Objects;

public class HomeBottomSheetFragment extends BottomSheetDialogFragment {

    static final String TAG = "HomeBottomSheetFragment";
    private static final int PICK_AVATAR = 1500;
    private ImageView profilePic;
    private TextView mUserName;

    static HomeBottomSheetFragment newInstance() {
        return new HomeBottomSheetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_bottom_sheet_dialog, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserName = view.findViewById(R.id.user_name);

        if (null != getContext()) updateUserName(UserInfo.getUserName(getContext()));

        profilePic = view.findViewById(R.id.user_profile);
        loadProfilePic();

        mUserName.setOnClickListener(v1 -> addUserName());

        profilePic.setOnClickListener(v1 -> pickPhoto());

        view.findViewById(R.id.check_source_code).setOnClickListener(v1 -> openLink(getString(R.string.source_code_link)));

        view.findViewById(R.id.git_profile).setOnClickListener(v1 -> openLink(getString(R.string.github_link)));

        view.findViewById(R.id.app_info).setOnClickListener(v1 -> startActivity(new Intent(getContext(), AppInfo.class)));

        view.findViewById(R.id.settings).setOnClickListener(v1 -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            if (isVisible()) dismiss();
        });
    }

    private void loadProfilePic() {
        GlideApp.with(this)
                .load(UserInfo.getUserProfilePic(profilePic.getContext()))
                .error(R.drawable.def_avatar)
                .circleCrop()
                .into(profilePic);
    }

    private void pickPhoto() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_AVATAR);
    }

    private void openLink(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void updateUserName(String title) {
        mUserName.setText(title);
    }

    private void addUserName() {
        View layout = View.inflate(getContext(), R.layout.dialog_create_playlist, null);
        BottomSheetDialog sheetDialog = new CustomBottomSheet(layout.getContext());
        sheetDialog.setContentView(layout);
        sheetDialog.show();

        ((TextView) layout.findViewById(R.id.header)).setText(getResources().getString(R.string.enter_name));
        ((TextInputLayout) layout.findViewById(R.id.edit_text_container)).setHint(getResources().getString(R.string.enter_name));
        TextInputEditText et = layout.findViewById(R.id.text_input_field);

        layout.findViewById(R.id.confirm_btn).setOnClickListener(v -> {
            if (null != getContext() && et.getText() != null && et.getText().toString().length() > 0) {
                String name = et.getText().toString();
                UserInfo.saveUserName(getContext(), name);
                if (sheetDialog.isShowing()) sheetDialog.dismiss();
                updateUserName(name);
            } else
                Toast.makeText(getContext(), getString(R.string.enter_name_toast), Toast.LENGTH_SHORT).show();
        });

        layout.findViewById(R.id.cancel_btn).setOnClickListener(v -> {
            if (sheetDialog.isShowing())
                sheetDialog.dismiss();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR) {
            if (null == data) {
                Toast.makeText(getContext(), getString(R.string.error_select_image_toast), Toast.LENGTH_SHORT).show();
                return;
            }
            if (null != getContext()) {
                UserInfo.saveUserProfilePic(getContext(), data.getDataString());
                loadProfilePic();
            }
        }
    }
}
