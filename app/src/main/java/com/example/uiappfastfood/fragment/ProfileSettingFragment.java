package com.example.uiappfastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.uiappfastfood.R;
import com.example.uiappfastfood.util.NotificationUtil;
import com.example.uiappfastfood.activity.FavouriteFoodActivity;
import com.example.uiappfastfood.activity.PersonalDataActivity;
import com.example.uiappfastfood.activity.OrderStatusActivity;
import com.example.uiappfastfood.activity.LocationActivity;
import com.example.uiappfastfood.activity.LoginActivity;
import com.example.uiappfastfood.sharePreference.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class ProfileSettingFragment extends Fragment {

    private TextView tvNotiCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_setting, container, false);

//        tvNotiCount = rootView.findViewById(R.id.tv_notiCount);
//        int unreadCount = NotificationUtil.getNewNotification(requireContext());
//        if (unreadCount > 0) {
//            tvNotiCount.setText(String.valueOf(unreadCount));
//        } else {
//            tvNotiCount.setVisibility(View.GONE);
//        }

        // Sign out
        rootView.findViewById(R.id.btn_signout).setOnClickListener(view -> showSignOutDialog());

        // Open personal data
        rootView.findViewById(R.id.personal_data_item).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PersonalDataActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Open favourite food
        rootView.findViewById(R.id.btn_favouriteFood).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), FavouriteFoodActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Open tracking order
        rootView.findViewById(R.id.btn_trackingOrder).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OrderStatusActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // Open order address
        rootView.findViewById(R.id.btn_orderAddress).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LocationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        return rootView;
    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_signout, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btn_confirm_signout).setOnClickListener(v -> {
            SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
            String loginType = sharedPrefManager.getType();

            if ("google".equals(loginType)) {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(
                        requireContext(),
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build()
                );

                googleSignInClient.signOut().addOnCompleteListener(task -> {
                    sharedPrefManager.clearUserId();
                    redirectToLogin();
                });
            } else {
                sharedPrefManager.clearUserId();
                redirectToLogin();
            }

            dialog.dismiss(); // đóng dialog sau khi xử lý
        });;
    }
    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if (tvNotiCount != null) {
//            int unreadCount = NotificationUtil.getNewNotification(this);
//
//            if (unreadCount > 0) {
//                tvNotiCount.setVisibility(View.VISIBLE);
//                tvNotiCount.setText(String.valueOf(unreadCount));
//            } else {
//                tvNotiCount.setVisibility(View.GONE);
//            }
//        }
//    }

