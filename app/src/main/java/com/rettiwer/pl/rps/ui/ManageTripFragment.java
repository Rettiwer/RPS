package com.rettiwer.pl.rps.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.rettiwer.pl.rps.R;
import com.rettiwer.pl.rps.data.AppDatabase;
import com.rettiwer.pl.rps.data.model.Trip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ManageTripFragment extends Fragment {
    private AppDatabase appDatabase;

    @BindView(R.id.driver_first_edit)
    TextInputLayout firstDriver;

    @BindView(R.id.driver_first_edit_text)
    TextInputEditText firstDriverEdit;

    @BindView(R.id.driver_second_edit)
    TextInputLayout secondDriver;

    @BindView(R.id.trailer_id_edit)
    TextInputLayout trailerId;

    @BindView(R.id.vehicle_id_edit)
    TextInputLayout vehicleId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appDatabase = ((TripActivity)getActivity()).db;

        View view = inflater.inflate(R.layout.fragment_manage_trip, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("CheckResult")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null) {
            String tripId = TripEventListFragmentArgs.fromBundle(getArguments()).getTripId();


            appDatabase.tripDao().get(tripId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(trip -> {
                                firstDriver.getEditText().setText(trip.getFirstDriver());
                                secondDriver.getEditText().setText(trip.getSecondDriver());
                                vehicleId.getEditText().setText(trip.getVehicleId());
                                trailerId.getEditText().setText(trip.getTrailerId());
                            } );
        }
    }

    private Trip trip;

    @SuppressLint("CheckResult")
    @OnClick(R.id.save_button)
    public void onSaveClick() {
        firstDriver.setError("");
        vehicleId.setError("");
        trailerId.setError("");

        if (TextUtils.isEmpty(firstDriver.getEditText().getText().toString().trim())) {
            firstDriver.setError("To pole nie może być puste");
            return;
        }

        if (TextUtils.isEmpty(vehicleId.getEditText().getText().toString().trim())) {
            vehicleId.setError("To pole nie może być puste");
            return;
        }

        if (TextUtils.isEmpty(trailerId.getEditText().getText().toString().trim())) {
            trailerId.setError("To pole nie może być puste");
            return;
        }

        trip = new Trip();

        if(getArguments() != null) {
            String tripId = TripEventListFragmentArgs.fromBundle(getArguments()).getTripId();

            trip.setId(tripId);
        }
        else {
            trip.setId(UUID.randomUUID().toString());
        }

        trip.setFirstDriver(firstDriver.getEditText().getText().toString().trim());

        if (TextUtils.isEmpty(secondDriver.getEditText().getText().toString().trim())) {
            trip.setSecondDriver("-");
        }
        else {
            trip.setSecondDriver(secondDriver.getEditText().getText().toString().trim());
        }

        trip.setTrailerId(trailerId.getEditText().getText().toString().trim());
        trip.setVehicleId(vehicleId.getEditText().getText().toString().trim());


        appDatabase.tripDao().add(trip).subscribeOn(Schedulers.io()).subscribe(this::moveToTripListFragment);
    }

    public void moveToTripListFragment() {
        hideKeyboard();
        ManageTripFragmentDirections.ActionManageTripFragmentToTripEventListFragment action = ManageTripFragmentDirections.actionManageTripFragmentToTripEventListFragment(trip.getId());
        NavHostFragment.findNavController(ManageTripFragment.this)
                 .navigate(action);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        View currentFocusedView = getActivity().getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}