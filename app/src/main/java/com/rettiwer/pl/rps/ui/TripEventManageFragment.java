package com.rettiwer.pl.rps.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTouch;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.rettiwer.pl.rps.R;
import com.rettiwer.pl.rps.data.AppDatabase;
import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.data.model.TripEvent;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class TripEventManageFragment extends Fragment {
    @BindView(R.id.event_type_edit)
    Spinner eventType;

    @BindView(R.id.departure_date_edit)
    EditText departureDate;

    @BindView(R.id.arrival_date_edit)
    EditText arrivalDate;

    @BindView(R.id.country_edit)
    TextInputLayout country;

    @BindView(R.id.city_edit)
    TextInputLayout city;

    @BindView(R.id.meter_status_edit)
    TextInputLayout meterStatus;

    @BindView(R.id.weight_edit)
    TextInputLayout weight;

    @BindView(R.id.comments_edit)
    TextInputLayout comments;

    private AppDatabase appDatabase;

    private Trip trip;

    private String tripEventId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_event_manage, container, false);

        ButterKnife.bind(this, view);

        appDatabase = ((TripActivity)getActivity()).db;

        return view;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments() != null) {
            String tripId = TripEventListFragmentArgs.fromBundle(getArguments()).getTripId();

            appDatabase.tripDao().get(tripId).subscribeOn(Schedulers.io())
                    .subscribe(trip -> {
                       this.trip = trip;
                    });

            if (TripEventManageFragmentArgs.fromBundle(getArguments()).getTripEventId() != null) {
                tripEventId = TripEventManageFragmentArgs.fromBundle(getArguments()).getTripEventId();

                appDatabase.tripEventDao().get(tripEventId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(tripEvent -> {

                            List<String> eventTypes = Arrays.asList((getResources().getStringArray(R.array.trip_event_type)));
                            eventType.setSelection(eventTypes.indexOf(tripEvent.getEventType()));

                            departureDate.setText(tripEvent.getDepartureDate());
                            arrivalDate.setText(tripEvent.getArrivalDate());
                            country.getEditText().setText(tripEvent.getCountry());
                            city.getEditText().setText(tripEvent.getCity());
                            meterStatus.getEditText().setText(String.valueOf(tripEvent.getMeterStatus()));
                            weight.getEditText().setText(String.valueOf(tripEvent.getWeight()));
                            comments.getEditText().setText(tripEvent.getComments());
                });
            }
        }

    }

    @OnClick(R.id.departure_date_edit)
    public void departureDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, monthOfYear, dayOfMonth) -> {

            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                departureDate.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date.getTime()));
            },currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }

    @OnClick(R.id.arrival_date_edit)
    public void arrivalDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, monthOfYear, dayOfMonth) -> {

            date.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                arrivalDate.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date.getTime()));
            },currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }

    @OnClick(R.id.save_button)
    public void save() {
        country.setError("");
        city.setError("");
        meterStatus.setError("");

        if (TextUtils.isEmpty(departureDate.getText().toString().trim()))
            return;

        if (TextUtils.isEmpty(arrivalDate.getText().toString().trim()))
            return;

        if (TextUtils.isEmpty(country.getEditText().getText().toString().trim())) {
            country.setError("Pole nie może być puste");
            return;
        }
        if (TextUtils.isEmpty(city.getEditText().getText().toString().trim())) {
            city.setError("Pole nie może być puste");
            return;
        }
        if (TextUtils.isEmpty(meterStatus.getEditText().getText().toString().trim())) {
            meterStatus.setError("Pole nie może być puste");
            return;
        }

        TripEvent tripEvent = new TripEvent();

        if (tripEventId != null)
            tripEvent.setId(tripEventId);
        else
            tripEvent.setId(UUID.randomUUID().toString());

        tripEvent.setEventType(eventType.getSelectedItem().toString());
        tripEvent.setDepartureDate(departureDate.getText().toString().trim());
        tripEvent.setArrivalDate(arrivalDate.getText().toString().trim());
        tripEvent.setCountry(country.getEditText().getText().toString().trim());
        tripEvent.setCity(city.getEditText().getText().toString().trim());
        tripEvent.setMeterStatus(Integer.parseInt(meterStatus.getEditText().getText().toString().trim()));

        if (TextUtils.isEmpty(weight.getEditText().getText())) {
            tripEvent.setWeight(-1);
        }
        else {
            tripEvent.setWeight(Integer.parseInt(weight.getEditText().getText().toString().trim()));
        }

        tripEvent.setComments(comments.getEditText().getText().toString());
        tripEvent.setTripId(trip.getId());

        tripEvent.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());

        Log.d("timestamp", new Timestamp(System.currentTimeMillis()).toString());

        appDatabase.tripEventDao().add(tripEvent).subscribeOn(Schedulers.io()).subscribe(this::moveToTripEventList);
    }

    private void moveToTripEventList() {
        hideKeyboard();
        TripEventManageFragmentDirections.ActionTripEventManageFragmentToTripEventListFragment action =
                TripEventManageFragmentDirections.actionTripEventManageFragmentToTripEventListFragment(trip.getId());
        NavHostFragment.findNavController(TripEventManageFragment.this)
                .navigate(action);
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = getActivity().getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}