package com.rettiwer.pl.rps.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.itextpdf.tool.xml.html.table.Table;
import com.rettiwer.pl.rps.BuildConfig;
import com.rettiwer.pl.rps.R;
import com.rettiwer.pl.rps.data.AppDatabase;
import com.rettiwer.pl.rps.data.model.Trip;
import com.rettiwer.pl.rps.data.model.TripEvent;
import com.rettiwer.pl.rps.ui.adapters.TripEventListAdapter;
import com.rettiwer.pl.rps.ui.adapters.TripListAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TripEventListFragment extends Fragment {
    @BindView(R.id.departure_text)
    TextView departureText;

    @BindView(R.id.arrival_text)
    TextView arrivalText;

    @BindView(R.id.driver_first_text)
    TextView driverFirstText;

    @BindView(R.id.driver_second_text)
    TextView driverSecondText;

    @BindView(R.id.vehicle_id_text)
    TextView vehicleIdText;

    @BindView(R.id.trailer_id_text)
    TextView trailerIdText;

    @BindView(R.id.event_list)
    RecyclerView eventList;

    private TripEventListAdapter tripEventListAdapter;

    private AppDatabase appDatabase;

    private Trip trip;
    private List<TripEvent> tripEvents;

    private boolean hasPermission = false;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        appDatabase = ((TripActivity)getActivity()).db;

        View view = inflater.inflate(R.layout.fragment_trip_event_list, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @SuppressLint("CheckResult")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
        else {
            hasPermission = true;
        }

        String tripId = TripEventListFragmentArgs.fromBundle(getArguments()).getTripId();

        appDatabase.tripDao().get(tripId).subscribeOn(Schedulers.io())
                .subscribe(newTrip -> {
                    trip = newTrip;
                    driverFirstText.setText("Kierowca 1: " + trip.getFirstDriver());
                    driverSecondText.setText("Kierowca 2: " +trip.getSecondDriver());
                    vehicleIdText.setText("Nr pojazdu: " +trip.getVehicleId());
                    trailerIdText.setText("Nr naczepy: " +trip.getTrailerId());
                } );

        tripEvents = new ArrayList<>();

        eventList.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        eventList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventList.getContext(),
                layoutManager.getOrientation());
        eventList.addItemDecoration(dividerItemDecoration);

        tripEventListAdapter = new TripEventListAdapter(getContext(), tripEvents, appDatabase, NavHostFragment.findNavController(TripEventListFragment.this));

        eventList.setAdapter(tripEventListAdapter);

        appDatabase.tripEventDao().getAllByTrip(tripId).subscribeOn(Schedulers.io()).subscribe(newTripEvents -> {
            tripEvents.addAll(newTripEvents);
            Collections.sort(tripEvents);
            tripEventListAdapter.notifyDataSetChanged();

            if(tripEvents.size() > 0) {
                departureText.setText("Wyjazd: " + tripEvents.get(0).getDepartureDate());
                arrivalText.setText("Przyjazd: " + tripEvents.get(tripEvents.size()-1).getArrivalDate());
            }
            else {
                departureText.setText("Wyjazd: -");
                arrivalText.setText("Przyjazd: -");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    hasPermission = true;
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @OnClick(R.id.add_button)
    public void addEvent() {
        TripEventListFragmentDirections.ActionTripEventListFragmentToTripEventManageFragment action =
                TripEventListFragmentDirections.actionTripEventListFragmentToTripEventManageFragment(TripEventListFragmentArgs.fromBundle(getArguments()).getTripId(), null);
        NavHostFragment.findNavController(TripEventListFragment.this)
                .navigate(action);
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.print_button)
    public void generatePDF() {
        try {
            generateTripRaport(trip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    public void generateTripRaport(Trip trip) throws IOException, DocumentException {
        File tripLog = getFile(getContext(), "trip_" + trip.getId() +".pdf");

        if (tripLog.exists()) {
            tripLog.delete();
        }

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(tripLog));

        document.setPageSize(PageSize.A4.rotate());
        document.open();

        PdfPTable table = new PdfPTable(3);
        table.getDefaultCell().setBorder(0);
        table.setWidthPercentage(100);

        table.addCell(getCell("KARTA ROZLICZENIA PODRÓŻY\nSŁUŻBOWEJ",true));
        table.addCell(getCell("DANE KIEROWCY",true));
        table.addCell(getCell("DANE POJAZDU(ZESTAWU)",true));
        if (tripEvents.size() > 0)
            table.addCell(getCell("Wyjazd: " + tripEvents.get(0).getDepartureDate(),true));
        else
            table.addCell(getCell("Wyjazd: -",true));

        table.addCell(getCell("Kierowca 1: " + trip.getFirstDriver(),true));
        table.addCell(getCell("Nr pojazdu: " + trip.getVehicleId(),true));

        if (tripEvents.size() > 0)
            table.addCell(getCell("Przyjazd: " + tripEvents.get(tripEvents.size()-1).getArrivalDate(),true));
        else
            table.addCell(getCell("Przyjazd: -",true));

        table.addCell(getCell("Kierowca 2: " + trip.getSecondDriver(),true));
        table.addCell(getCell("Nr naczepy: " + trip.getTrailerId(),true));

        document.add(table);

        BaseFont helvetica = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.EMBEDDED);
        Font helvetica16  = new Font(helvetica,14);
        Paragraph p = new Paragraph("TRASA - SZCZEGÓŁOWY RAPORT PUNKTÓW PODRÓŻY", helvetica16);
        p.setSpacingBefore(10);
        p.setSpacingAfter(10);
        document.add(p);

        PdfPTable eventTable = new PdfPTable(10);
        eventTable.setWidthPercentage(100);
        eventTable.setWidths(new int[]{4, 11, 16, 16, 5, 15, 9, 8, 7, 9});

        eventTable.addCell(getCell("L.p.",false));
        eventTable.addCell(getCell("Zdarzenie",false));
        eventTable.addCell(getCell("Data OD",false));
        eventTable.addCell(getCell("Data DO",false));
        eventTable.addCell(getCell("Kraj",false));
        eventTable.addCell(getCell("Miejsce",false));
        eventTable.addCell(getCell("Stan licznika",false));
        eventTable.addCell(getCell("Dystans",false));
        eventTable.addCell(getCell("Masa",false));
        eventTable.addCell(getCell("Uwagi",false));


        if (tripEvents.size() > 0) {
            TripEvent tripEvent = tripEvents.get(0);

            eventTable.addCell(getCell("1",false));
            eventTable.addCell(getCell(tripEvent.getEventType(),false));
            eventTable.addCell(getCell(tripEvent.getDepartureDate(),false));
            eventTable.addCell(getCell(tripEvent.getArrivalDate(),false));
            eventTable.addCell(getCell(tripEvent.getCountry(),false));
            eventTable.addCell(getCell(tripEvent.getCity(),false));
            eventTable.addCell(getCell(String.valueOf(tripEvent.getMeterStatus()),false));
            eventTable.addCell(getCell("-",false));
            if (tripEvent.getWeight() == -1) {
                eventTable.addCell(getCell(" ",false));
            }
            else {
                eventTable.addCell(getCell(String.valueOf(tripEvent.getWeight()),false));
            }
            eventTable.addCell(getCell(tripEvent.getComments(),false));


            for (int i = 1; i < tripEvents.size(); i++) {
                tripEvent = tripEvents.get(i);

                eventTable.addCell(getCell(String.valueOf(i + 1),false));
                eventTable.addCell(getCell(tripEvent.getEventType(),false));
                eventTable.addCell(getCell(tripEvent.getDepartureDate(),false));
                eventTable.addCell(getCell(tripEvent.getArrivalDate(),false));
                eventTable.addCell(getCell(tripEvent.getCountry(),false));
                eventTable.addCell(getCell(tripEvent.getCity(),false));
                eventTable.addCell(getCell(String.valueOf(tripEvent.getMeterStatus()),false));
                eventTable.addCell(getCell(String.valueOf(tripEvent.getMeterStatus() - tripEvents.get(i-1).getMeterStatus()),false));

                if (tripEvent.getWeight() == -1) {
                    eventTable.addCell(" ");
                }
                else {
                    eventTable.addCell(getCell(String.valueOf(tripEvent.getWeight()),false));
                }

                eventTable.addCell(getCell(tripEvent.getComments(),false));
            }

            document.add(eventTable);
        }

        document.close();


        Uri uri = getFileUri(getContext(), "trip_" + trip.getId() +".pdf");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");

        // FLAG_GRANT_READ_URI_PERMISSION is needed on API 24+ so the activity opening the file can read it
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            // Show an error
        } else {
            startActivity(intent);
        }
    }

    private PdfPCell getCell(String text, boolean border) throws IOException, DocumentException {
        BaseFont helvetica = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.EMBEDDED);
        Font helvetica16  = new Font(helvetica,12);

        PdfPCell pdfPCell = new PdfPCell(new Phrase(text , helvetica16));
        if (border)
            pdfPCell.setBorderWidth(0);
        return pdfPCell;
    }

    public static String getAppPath(Context context) {
        File dir = new File(android.os.Environment.getExternalStorageDirectory()
                + File.separator
                + context.getResources().getString(R.string.app_name)
                + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.getPath() + File.separator;
    }

    public File getFile(Context context, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File storageDir = context.getExternalFilesDir(null);
        return new File(storageDir, fileName);
    }

    public Uri getFileUri(Context context, String fileName) {
        File file = getFile(context, fileName);
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    }
}