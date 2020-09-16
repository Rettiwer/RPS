package com.rettiwer.pl.rps.ui.adapters;

import android.app.AlertDialog;
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
import com.rettiwer.pl.rps.data.model.TripEvent;
import com.rettiwer.pl.rps.data.model.TripEvent;
import com.rettiwer.pl.rps.ui.TripEventListFragmentDirections;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.schedulers.Schedulers;

public class TripEventListAdapter extends RecyclerView.Adapter<TripEventListAdapter.TripEventViewHolder> {
    private List<TripEvent> tripEventList;
    private Context mContext;
    private AppDatabase appDatabase;
    private NavController navController;

    public TripEventListAdapter(Context context, List<TripEvent> tripList, AppDatabase appDatabase, NavController navController)
    {
        this.mContext = context;
        this.tripEventList = tripList;
        this.appDatabase = appDatabase;
        this.navController = navController;
    }

    class TripEventViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.trip_event_number)
        TextView tripEventNumber;
        @BindView(R.id.trip_event_type_text)
        TextView eventType;
        @BindView(R.id.city_text)
        TextView city;
        @BindView(R.id.departure_text)
        TextView departureDate;
        @BindView(R.id.arrival_text)
        TextView arrivalDate;

        private TripEvent tripEvent;

        public TripEventViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(int position) {
            tripEvent = tripEventList.get(position);

            tripEventNumber.setText(String.valueOf(position));
            eventType.setText(tripEvent.getEventType());
            city.setText(tripEvent.getCity());
            departureDate.setText("Data OD: " +tripEvent.getDepartureDate());
            arrivalDate.setText("Data DO: " + tripEvent.getArrivalDate());
        }

        @OnLongClick
        public void editOrDelete() {
            String[] options = {"Usuń", "Modyfikuj"};

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        appDatabase.tripEventDao().remove(tripEvent).subscribeOn(Schedulers.io()).subscribe();
                        tripEventList.remove(tripEvent);
                        notifyDataSetChanged();
                        break;
                    case 1:
                        TripEventListFragmentDirections.ActionTripEventListFragmentToTripEventManageFragment action =
                                TripEventListFragmentDirections.actionTripEventListFragmentToTripEventManageFragment(tripEvent.getTripId(), tripEvent.getId());
                                navController.navigate(action);
                        break;
                }
            });
            builder.show();
        }
    }

    @NonNull
    @Override
    public TripEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trip_event_row_item, parent, false);

        TripEventViewHolder tripEventViewHolder = new TripEventViewHolder(view);
        return tripEventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripEventViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return tripEventList.size();
    }

    private TripEvent getItem(int position) {
        return this.tripEventList.get(position);
    }
}