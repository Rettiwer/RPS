package com.rettiwer.pl.rps.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rettiwer.pl.rps.R;
import com.rettiwer.pl.rps.data.AppDatabase;
import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.ui.ManageTripFragment;
import com.rettiwer.pl.rps.ui.ManageTripFragmentDirections;
import com.rettiwer.pl.rps.ui.TripActivity;
import com.rettiwer.pl.rps.ui.TripListFragment;

import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TripListAdapter extends ArrayAdapter<Trip> {
    private List<Trip> tripList;
    private Context mContext;
    private AppDatabase appDatabase;

    public TripListAdapter(Context context, List<Trip> tripList, AppDatabase appDatabase)
    {
        super(context, R.layout.trip_row_item, tripList);
        this.mContext = context;
        this.tripList = tripList;
        this.appDatabase = appDatabase;
    }

    public static class TripViewHolder {
        @BindView(R.id.trip_event_type_text)
        TextView eventType;
        @BindView(R.id.city_text)
        TextView city;
        @BindView(R.id.departure_text)
        TextView departureDate;
        @BindView(R.id.arrival_text)
        TextView arrivalDate;

        private Trip trip;

        @SuppressLint("CheckResult")
        public TripViewHolder(View view, Trip trip, AppDatabase appDatabase) {
            ButterKnife.bind(this, view);

            appDatabase.tripEventDao().getAllByTrip(trip.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(tripEvents -> {
                        if (tripEvents.size() > 0) {
                            int last = tripEvents.size()-1;
                            eventType.setText(tripEvents.get(last).getEventType());
                            city.setText(tripEvents.get(last).getCity());
                            departureDate.setText(tripEvents.get(last).getDepartureDate());
                            arrivalDate.setText(tripEvents.get(last).getArrivalDate());
                        }
                        else {
                            eventType.setText("Nowa");
                            city.setText("-");
                            departureDate.setText("-");
                            arrivalDate.setText("-");
                        }
                    });
            eventType.setText("Rozpoczecie");
            city.setText("91578 lentershansen");
            departureDate.setText("04.02.2020 18:00");
            arrivalDate.setText("04.02.2020 18:00");
            this.trip = trip;
        }

        public Trip getTrip() {
            return this.trip;
        }
    }

    @Override
    public int getCount()
    {
        return tripList.size();
    }

    @Override
    public Trip getItem(int position)
    {
        return tripList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Trip trip = getItem(position);

        TripViewHolder tripViewHolder;
        View v = convertView;
        if (v == null)
        {
            v = LayoutInflater.from(mContext).inflate(R.layout.trip_row_item, parent, false);
            tripViewHolder = new TripViewHolder(v, trip, appDatabase);
            v.setTag(tripViewHolder);
        }
        return v;
    }
}