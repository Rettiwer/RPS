package com.rettiwer.pl.rps.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.rettiwer.pl.rps.R;
import com.rettiwer.pl.rps.data.AppDatabase;
import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.ui.adapters.TripListAdapter;

import java.util.ArrayList;
import java.util.List;

public class TripListFragment extends Fragment {
    @BindView(R.id.trip_list)
    ListView tripListView;

    ListAdapter tripListAdapter;

    private AppDatabase appDatabase;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        ButterKnife.bind(this, view);

        appDatabase = ((TripActivity)getActivity()).db;
        return view;
    }

    @SuppressLint("CheckResult")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Trip> tripList = new ArrayList<>();

        tripListAdapter = new TripListAdapter(getContext(), tripList, appDatabase);

        tripListView.setAdapter(tripListAdapter);

        appDatabase.tripDao().getAll().subscribeOn(Schedulers.io()).subscribe((Consumer<List<Trip>>) tripList::addAll);

        tripListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            TripListAdapter.TripViewHolder tripViewHolder = (TripListAdapter.TripViewHolder) view1.getTag();

            TripListFragmentDirections.ActionTripListFragmentToTripEventListFragment action = TripListFragmentDirections.actionTripListFragmentToTripEventListFragment(tripViewHolder.getTrip().getId());
            Navigation.findNavController(view1).navigate(action);
        });

        tripListView.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            TripListAdapter.TripViewHolder tripViewHolder = (TripListAdapter.TripViewHolder) view12.getTag();
            String[] options = {"Usuń", "Modyfikuj"};

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            appDatabase.tripEventDao().removeAllById(tripViewHolder.getTrip().getId())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe();
                            appDatabase.tripDao().remove(tripViewHolder.getTrip()).subscribeOn(Schedulers.io()).subscribe();
                            tripList.remove(tripViewHolder.getTrip());
                            tripListAdapter = new TripListAdapter(getContext(), tripList, appDatabase);
                            tripListView.setAdapter(tripListAdapter);
                            break;
                        case 1:
                            TripListFragmentDirections.ActionTripListFragmentToManageTripFragment action =
                                    TripListFragmentDirections.actionTripListFragmentToManageTripFragment(tripViewHolder.getTrip().getId());
                            NavHostFragment.findNavController(TripListFragment.this)
                                    .navigate(action);
                            break;
                    }
                }
            });
            builder.show();
            return true;
        });
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        NavHostFragment.findNavController(TripListFragment.this)
                .navigate(R.id.action_tripListFragment_to_manageTripFragment);
    }
}