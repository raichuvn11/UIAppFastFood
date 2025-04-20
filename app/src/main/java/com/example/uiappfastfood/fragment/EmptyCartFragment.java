package com.example.uiappfastfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.uiappfastfood.R;
import androidx.fragment.app.Fragment;

public class EmptyCartFragment extends Fragment {
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_empty_cart, container, false);

        rootView.findViewById(R.id.cvBack).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Back to home page", Toast.LENGTH_SHORT).show();
            // Optional: quay lại màn hình trước đó
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return rootView;
    }
}

