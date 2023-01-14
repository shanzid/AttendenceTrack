package com.attendance.tracker.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.attendance.tracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class ForegroundService extends Service {



    private final IBinder mBinder = new MyBinder();

    private static final String CHANNEL_ID = "2";



    @Override

    public IBinder onBind(Intent intent) {

        return mBinder;

    }



    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }



    @Override
    public void onCreate() {

        super.onCreate();

        buildNotification();

        requestLocationUpdates();

    }



    private void buildNotification() {

        String stop = "stop";

        PendingIntent broadcastIntent = PendingIntent.getBroadcast(

                this, 0, new Intent(stop), PendingIntent.FLAG_IMMUTABLE);

        // Create the persistent notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)

                .setContentTitle(getString(R.string.app_name))

                .setContentText("Location tracking is working")

                .setOngoing(true)

                .setContentIntent(broadcastIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name),

                    NotificationManager.IMPORTANCE_DEFAULT);

            channel.setShowBadge(false);

            channel.setDescription("Location tracking is working");

            channel.setSound(null, null);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);

        }



        startForeground(1, builder.build());

    }

    private void requestLocationUpdates() {

        LocationRequest request = new LocationRequest();

        request.setInterval(1000);

        request.setFastestInterval(3000);

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        int permission = ContextCompat.checkSelfPermission(this,

                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {

            // Request location updates and when an update is

            // received, store the location in Firebase

            client.requestLocationUpdates(request, new LocationCallback() {

                @Override

                public void onLocationResult(LocationResult locationResult) {



                    String location = "Latitude : " + locationResult.getLastLocation().getLatitude() +

                            "\nLongitude : " + locationResult.getLastLocation().getLongitude();



                    Toast.makeText(ForegroundService.this, location, Toast.LENGTH_SHORT).show();

                }

            }, null);

        } else {

            stopSelf();

        }

    }

    public class MyBinder extends Binder {

        public ForegroundService getService() {

            return ForegroundService.this;

        }

    }

}

