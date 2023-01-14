package com.attendance.tracker.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.attendance.tracker.R;
import com.attendance.tracker.adapter.SearchDateGeoAdapter;
import com.attendance.tracker.data.DateSearchGeoData;
import com.attendance.tracker.data.SearchGeoList;
import com.attendance.tracker.network.APIService;
import com.attendance.tracker.network.ApiUtil.ApiUtils;
import com.attendance.tracker.network.ConstantValue;
import com.attendance.tracker.utils.AppSessionManager;
import com.attendance.tracker.utils.CheckInternetConnection;
import com.google.android.material.snackbar.Snackbar;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateWiseGeoActivity extends AppCompatActivity  {
    RecyclerView reportList;
    SearchDateGeoAdapter geoAttendanceAdapter;
    ArrayList<SearchGeoList> geoReportLists = new ArrayList<>();
    CheckInternetConnection internetConnection;
    AppSessionManager appSessionManager;
    DatePickerDialog picker;
    TextView tv_start_date,tv_end_date;
    String startDate,endDate,startDateTimeStamp,EndDateTimeStamp;
    String userId,type ;

    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    public static final String SAMPLE_FILE = "Attendence.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_wise_geo);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.calendar);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        // below code is used for
        // checking our permissions.
        if (checkPermission()) {
            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        initVariable();
        initView();
        initListener();
        initFunc();

    }

    private void initVariable() {
        Intent mIntent = getIntent();
        userId = mIntent.getStringExtra("userId");
        type = mIntent.getStringExtra("type");
        appSessionManager = new AppSessionManager(this);
        internetConnection = new CheckInternetConnection();
    }

    private void initView() {

        findViewById(R.id.btnSearch).setVisibility(View.GONE);
            tv_start_date = findViewById(R.id.tv_start_date);
            tv_end_date = findViewById(R.id.tv_end_date);

        LoadData();

    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(view -> finish());
        findViewById(R.id.lytStartDate).setOnClickListener(view -> showStartDatePicker());
        findViewById(R.id.lytEndDate).setOnClickListener(view -> showEndDatePicker());
        findViewById(R.id.btnSearch).setOnClickListener(view -> gotoSearch());
        findViewById(R.id.downloadPdf).setOnClickListener(view -> downloadPdf());
    }

    private void gotoSearch() {
        if (startDate.equals("")){
            Toast.makeText(this, "Enter Start Date", Toast.LENGTH_SHORT).show();
        }else if (endDate.equals("")){
            Toast.makeText(this, "Enter End Date", Toast.LENGTH_SHORT).show();
        }else {
            getGeoReport(userId,type);
        }
    }
    private void downloadPdf() {
        generatePDF();
    }

    private void showStartDatePicker() {

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(DateWiseGeoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate= dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        tv_start_date.setText(startDate);
                        convertStartDateTime(startDate);
                    }
                }, year, month, day);
        picker.show();
    }

    private void convertStartDateTime(String startDate) {


     DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
       Date date1 = null;
        try {
            date1 = (Date)formatter.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output=date1.getTime()/1000L;
        String str=Long.toString(output);
        long timestamp = Long.parseLong(str) ;
        startDateTimeStamp = String.valueOf(timestamp);
        Log.d("endtimestamap",""+startDateTimeStamp);


    }

    private void showEndDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(DateWiseGeoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate= dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        tv_end_date.setText(endDate);
                        convertEndDateTime(endDate);
                    }
                }, year, month, day);
        picker.show();
    }

    private void convertEndDateTime(String endDate) {


        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date2 = null;
        try {
            date2 = (Date)formatter.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output=date2.getTime()/1000L;
        String str=Long.toString(output);
        long timestamp = Long.parseLong(str) ;


        int valueend = Integer.parseInt(String.valueOf(timestamp));
        int enddatetotalend=valueend+3600000;
            EndDateTimeStamp = String.valueOf(enddatetotalend);

       if(startDateTimeStamp.equals(EndDateTimeStamp)){
           int value = Integer.parseInt(EndDateTimeStamp);
           int enddatetotal=value+3600000;
           EndDateTimeStamp=String.valueOf(enddatetotal);
       }
        findViewById(R.id.btnSearch).setVisibility(View.VISIBLE);


    }

    private void initFunc() {
       // getGeoReport(userId,type);
    }

    public void getGeoReport(String userId,String type){
        geoReportLists.clear();
        if (internetConnection.isInternetAvailable(DateWiseGeoActivity.this)) {
            final MaterialDialog dialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.loading))
                    .content(getResources().getString(R.string.pleaseWait))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            APIService mApiService = ApiUtils.getApiService(ConstantValue.URL);
            mApiService.showDateGeoReport(userId,startDateTimeStamp,EndDateTimeStamp,type).enqueue(new Callback<DateSearchGeoData>() {
                @Override
                public void onResponse(Call<DateSearchGeoData> call, Response<DateSearchGeoData> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getError() == 0) {
                            geoReportLists.addAll(response.body().getReport());
                            geoAttendanceAdapter.notifyDataSetChanged();
                            dialog.dismiss();

                        } else if (response.body().getError() == 1) {
                            dialog.dismiss();
                          //  Toast.makeText(DateWiseGeoActivity.this, response.body().getErrorReport(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DateSearchGeoData> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("LOGIN", "onFailure: " + t.getMessage());
                }
            });
        } else {
            Snackbar.make(findViewById(android.R.id.content), "(*_*) Internet connection problem!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void LoadData() {

        reportList = findViewById(R.id.reportList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        reportList.setHasFixedSize(true);
        reportList.setLayoutManager(layoutManager);
        geoAttendanceAdapter = new SearchDateGeoAdapter(geoReportLists,DateWiseGeoActivity.this);
        reportList.setAdapter(geoAttendanceAdapter);

    }


    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void generatePDF() {
        int width = 200;
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(scaledbmp, 56, 40, paint);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(20);
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        canvas.drawText("Artificial soft Limited", 209, 80, title);
        canvas.drawText("Employee attendance report", 209, 110, title);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.purple_200));
        title.setTextSize(15);
        title.setTextAlign(Paint.Align.LEFT);
        for (int i=0;i<geoReportLists.size();i++){
            width = width+20;
            title.setColor(ContextCompat.getColor(this, R.color.black));
            canvas.drawText(geoReportLists.get(i).getDate()+"         " +geoReportLists.get(i).getStatus(), 200, width, title);

        }


/*        for (int i=0;i<=10;i++){
            height = height+20;
            width = width+10;
            canvas.drawBitmap(scaledbmp, 56, 40, paint);
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setColor(ContextCompat.getColor(this, R.color.black));
            title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            title.setColor(ContextCompat.getColor(this, R.color.black));
            title.setTextSize(15);
            title.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Md. Shanzid Hassan", height, width, title);
        }*/
        pdfDocument.finishPage(myPage);
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "Attendence.pdf");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "Attendence.pdf");
        }
        try {
            pdfDocument.writeTo(new FileOutputStream(dir));
            Toast.makeText(DateWiseGeoActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        //displayFromAsset(dir);
        // Make sure the path directory exists.
        if (!dir.exists())
        {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success)
            {
                dir = null;

            }else {

            }
        }
    }


    private void displayFromAsset(File fileName) {
        Uri path = Uri.fromFile(fileName );
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path , "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(pdfIntent );
        }
        catch (ActivityNotFoundException e) {

        }
    }
}
