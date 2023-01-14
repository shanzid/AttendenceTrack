package com.attendance.simpleplacepicker;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.attendance.simpleplacepicker.utils.SimplePlacePicker;
import com.attendance.simpleplacepicker.utils.CustomButton;
import com.attendance.simpleplacepicker.utils.CustomTextView;
import com.attendance.simpleplacepicker.utils.FetchAddressIntentService;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    //location
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLAstKnownLocation;
    private LocationCallback locationCallback;
    private final float DEFAULT_ZOOM = 18.0f;
    private int request_code = 100;
    MarkerOptions markerOptions;
    LatLng latLng;


    //places
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    //views
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private RippleBackground rippleBg;
    private CustomTextView mDisplayAddressTextView;
    private ProgressBar mProgressBar;
    private ImageView mSmallPinIv;

    //variables
    private String addressOutput;
    private int addressResultCode;
    private boolean isSupportedArea;
    private LatLng currentMarkerPosition;

    //receiving
    private String mApiKey = "";
    private String[] mSupportedArea = new String[]{};
    private String mCountry = "";
    private String mLanguage = "en";
    private SeekBar seekBar;
    int progressChangedValue = 10;
    TextView textView;
    Circle circle = null;
    private static final String TAG = MapActivity.class.getSimpleName();
    AppCompatTextView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
        receiveIntent();
        initMapsAndPlaces();
    }

    private void initViews() {
        searchView = findViewById(R.id.idSearchView);
        CustomButton submitLocationButton = findViewById(R.id.submit_location_button);
        rippleBg = findViewById(R.id.ripple_bg);
        mDisplayAddressTextView = findViewById(R.id.tv_display_marker_location);
        mProgressBar = findViewById(R.id.progress_bar);
        mSmallPinIv = findViewById(R.id.small_pin);
        textView = findViewById(R.id.textView);
        Places.initialize(getApplicationContext(), "AIzaSyAUV4cZNbsJMAWbmQrQWZU_nVwPgAJPQVk");

        final View icPin = findViewById(R.id.ic_pin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView(icPin);
            }
        }, 1000);

        submitLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResultLocation();
                //startAutocompleteActivity();
            }
        });


        //  findViewById(R.id.ImvAtendance).setOnClickListener(view -> gotoReport());


        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(200);
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Radius " + progressChangedValue);
                circle.setRadius(progressChangedValue);
            }
        });

    }

    private void receiveIntent() {
        Intent intent = getIntent();

        if (intent.hasExtra(SimplePlacePicker.API_KEY)) {
            mApiKey = intent.getStringExtra(SimplePlacePicker.API_KEY);
        }

        if (intent.hasExtra(SimplePlacePicker.COUNTRY)) {
            mCountry = intent.getStringExtra(SimplePlacePicker.COUNTRY);
        }

        if (intent.hasExtra(SimplePlacePicker.LANGUAGE)) {
            mLanguage = intent.getStringExtra(SimplePlacePicker.LANGUAGE);
        }

        if (intent.hasExtra(SimplePlacePicker.SUPPORTED_AREAS)) {
            mSupportedArea = intent.getStringArrayExtra(SimplePlacePicker.SUPPORTED_AREAS);
        }
    }

    private void initMapsAndPlaces() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, mApiKey);
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        // adding on query listener for our search view.
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    assert addressList != null;
                    if (addressList.size()>0){
                        Address address = addressList.get(0);

                        // on below line we are creating a variable for our location
                        // where we will add our locations latitude and longitude.
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // on below line we are adding marker to that position.
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                        // below line is to animate camera to that position.
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }else {
                        Toast.makeText(MapActivity.this, "Please enter a valid address", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/


        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAutocompleteActivity();
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);
//        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                startSearch(text.toString(), true, null, true);
//            }
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//               if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
//                    materialSearchBar.disableSearch();
//                    materialSearchBar.clearSuggestions();
//                }
//            }
//        });

//        materialSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
//                        .setCountry(mCountry)
//                        .setSessionToken(token)
//                        .setQuery(s.toString())
//                        .build();
//                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
//                        if (task.isSuccessful()) {
//                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
//                            if (predictionsResponse != null) {
//                                predictionList = predictionsResponse.getAutocompletePredictions();
//                                List<String> suggestionsList = new ArrayList<>();
//                                for (int i = 0; i < predictionList.size(); i++) {
//                                    AutocompletePrediction prediction = predictionList.get(i);
//                                    suggestionsList.add(prediction.getFullText(null).toString());
//                                }
//                                materialSearchBar.updateLastSuggestions(suggestionsList);
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (!materialSearchBar.isSuggestionsVisible()) {
//                                            materialSearchBar.showSuggestionsList();
//                                        }
//                                    }
//                                }, 1000);
//                            }
//                        } else {
//                            Log.i(TAG, "prediction fetching task unSuccessful");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
//            @Override
//            public void OnItemClickListener(int position, View v) {
//                if (position >= predictionList.size()) {
//                    return;
//                }
//                AutocompletePrediction selectedPrediction = predictionList.get(position);
//                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
//                materialSearchBar.setText(suggestion);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        materialSearchBar.clearSuggestions();
//                    }
//                }, 1000);
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                }
//
//                String placeId = selectedPrediction.getPlaceId();
//                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
//
//                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
//                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//                    @Override
//                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
//                        Place place = fetchPlaceResponse.getPlace();
//                        Log.i(TAG, "place found " + place.getName() + place.getAddress());
//                        LatLng latLng = place.getLatLng();
//                        if (latLng != null) {
//                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
//                        }
//
//                        rippleBg.startRippleAnimation();
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                rippleBg.stopRippleAnimation();
//                            }
//                        }, 2000);
//                    }
//                })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                if (e instanceof ApiException) {
//                                    ApiException apiException = (ApiException) e;
//                                    apiException.printStackTrace();
//                                    int statusCode = apiException.getStatusCode();
//                                    Log.i(TAG, "place not found" + e.getMessage());
//                                    Log.i(TAG, "status code : " + statusCode);
//                                }
//                            }
//                        });
//            }
//
//            @Override
//            public void OnItemDeleteListener(int position, View v) {
//            }
//        });
    }

    private void submitResultLocation() {
        // if the process of getting address failed or this is not supported area , don't submit
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(MapActivity.this, R.string.failed_select_location, Toast.LENGTH_SHORT).show();
        } else {

            Intent data = new Intent();
            data.putExtra(SimplePlacePicker.SELECTED_ADDRESS, addressOutput);
            data.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude);
            data.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude);
            data.putExtra(SimplePlacePicker.RADIUS, String.valueOf(progressChangedValue));
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @SuppressLint("MissingPermission")

    /*
      is triggered when the map is loaded and ready to display
      @param GoogleMap
     *
     * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //enable location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);

        //move location button to the required position and adjust params such margin
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 60, 500);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        //if task failed means gps is disabled so ask user to enable gps
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible()) {
                    materialSearchBar.clearSuggestions();
                }
                if (materialSearchBar.isSearchEnabled()) {
                    materialSearchBar.disableSearch();
                }
                return false;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mSmallPinIv.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "changing address");


//                ToDo : you can use retrofit for this network call instead of using services
                //hint: services is just for doing background tasks when the app is closed no need to use services to update ui
                //best way to do network calls and then update user ui is Retrofit .. consider it
                startIntentService();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }

        if (requestCode == request_code) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "ManishPlace: " + place.getName() + ", " + place.getId());

                //txt_search.setText(place.getName());
                String location = place.toString();

                System.out.println("manishshis" + location);
                if (location != null && !location.equals("")) {
                    new MapActivity.GeocoderTask().execute(place.toString());
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * is triggered whenever we want to fetch device location
     * in order to get device's location we use FusedLocationProviderClient object that gives us the last location
     * if the task of getting last location is successful and not equal to null ,
     * apply this location to mLastLocation instance and move the camera to this location
     * if the task is not successful create new LocationRequest and LocationCallback instances and update lastKnownLocation with location result
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLAstKnownLocation = task.getResult();
                            if (mLAstKnownLocation != null) {


                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLAstKnownLocation = locationResult.getLastLocation();

                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        //remove location updates in order not to continues check location unnecessarily
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);


                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, null);
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "Unable to get last location ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void startIntentService() {
        currentMarkerPosition = mMap.getCameraPosition().target;
        AddressResultReceiver resultReceiver = new AddressResultReceiver(new Handler());

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(SimplePlacePicker.RECEIVER, resultReceiver);
        intent.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude);
        intent.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude);
        intent.putExtra(SimplePlacePicker.LANGUAGE, mLanguage);


        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(currentMarkerPosition.latitude, currentMarkerPosition.longitude))
                .radius(0)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));


        startService(intent);
    }

    private void updateUi() {
        mDisplayAddressTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mMap.clear();
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(currentMarkerPosition.latitude, currentMarkerPosition.longitude))
                .radius(0)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));


        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea(mSupportedArea)) {
                //supported
                addressOutput = addressOutput.replace("Unnamed Road,", "");
                addressOutput = addressOutput.replace("Unnamed Road،", "");
                addressOutput = addressOutput.replace("Unnamed Road New,", "");
                mSmallPinIv.setVisibility(View.VISIBLE);
                isSupportedArea = true;
                mDisplayAddressTextView.setText(addressOutput);


            } else {
                //not supported
                mSmallPinIv.setVisibility(View.GONE);
                isSupportedArea = false;
                mDisplayAddressTextView.setText(getString(R.string.not_support_area));
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            mSmallPinIv.setVisibility(View.GONE);
            mDisplayAddressTextView.setText(addressOutput);

        }
    }

    private boolean isSupportedArea(String[] supportedAreas) {
        if (supportedAreas.length == 0)
            return true;

        boolean isSupported = false;
        for (String area : supportedAreas) {
            if (addressOutput.contains(area)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    private void revealView(View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressResultCode = resultCode;
            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(SimplePlacePicker.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }
            updateUi();
        }
    }

    private void startAutocompleteActivity() {
        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, Place.Field.NAME);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, 100);
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(MapActivity.this);
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(MapActivity.this, "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            mMap.clear();

            // Adding Markers on Google Map for each matching address
            for (int i = 0; i < addresses.size(); i++) {

                Address address = (Address) addresses.get(i);

                System.out.println("svsfssfjfjf " + address);
                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                System.out.println("aadtetsetse " + address.getAddressLine(0));
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(address.getAddressLine(0));
                mMap.addMarker(markerOptions);

                // Locate the first location
                if (i == 0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }
}

