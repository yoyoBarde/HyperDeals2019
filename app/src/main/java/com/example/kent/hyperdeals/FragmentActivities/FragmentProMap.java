package com.example.kent.hyperdeals.FragmentActivities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kent.hyperdeals.LoginActivity;
import com.example.kent.hyperdeals.Model.subsubModel;
import com.example.kent.hyperdeals.Promo_Detail;
import com.example.kent.hyperdeals.R;



import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class FragmentProMap extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static String keyoo;
    private GoogleMap mMap;
    //Play Service Location
    private static final int MY_PERMISSION_REQUEST_CODE = 7192;
    private static final int PLAY_SERVICE_RESULATION_REQUEST = 300193;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocaiton;
    private static final int NOTIFICATION_ID_OPEN_ACTIVITY=9;
    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;
    final static String TAG="HyperDeals";
    Bitmap globalBitmap;
    View mView;
    GeoLocation userGeo;
    DatabaseReference ref;
    GeoFire geoFire;
    Marker mCurrent;
    VerticalSeekBar mVerticalSeekBar;
    MapView mMapView;
  ArrayList<subsubModel> arraysubsub=new ArrayList<subsubModel>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =inflater.inflate(R.layout.fragment_fragment_pro_map, container, false);
        return  mView;


    }

    @Override
    public void onViewCreated( View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"OnViewCreated");
        mMapView = (MapView) mView.findViewById(R.id.map);
        if (mMapView!=null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

        }
        ref = FirebaseDatabase.getInstance().getReference("Geofences");
        geoFire = new GeoFire(ref);
        mVerticalSeekBar = (VerticalSeekBar)getView().findViewById(R.id.verticalSeekBar);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(progress), 1500, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


       LatLng mylatlang = new LatLng(10.232466,123.768052);
      LatLng endLatLng = new LatLng(10.242898,123.788163);
      Log.e(TAG,"Result = "+CalculationByDistance(mylatlang,endLatLng));
        Log.e(TAG,"Result = "+CalculationByDistance(mylatlang,endLatLng));
        Log.e(TAG,"Result = "+CalculationByDistance(mylatlang,endLatLng));

        Log.e(TAG,"Result = "+CalculationByDistance(mylatlang,endLatLng));

setUpdateLocation();


    }


    public double CalculationByDistance(LatLng StartP, LatLng EndP) {


        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.e(TAG,"onMapReady");
       new GetImageFromURL().execute("asd");
        //add GeoQuery here

        Log.e(TAG,"onMapReady");

    }
    public void putMarkerImage(final Bitmap mybitmap,GeoPoint mygeo,LatLng mypromoGeo,String promoName){

        final Bitmap resizedBitmap = Bitmap.createScaledBitmap(mybitmap, 50, 50, true);

        mMap.addMarker(new MarkerOptions().title(promoName)
                .position(mypromoGeo)
                .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        Log.e(TAG,"putMarkerImage"+promoName);

    }
    public class GetImageFromURL2 extends AsyncTask<String,Void,Bitmap> {
        String Url ;
        GeoPoint mygeo;
        LatLng PromoGeo;
        String PromoName;


        public void OnNewSensorData(final Bitmap myBitmap, final GeoPoint myGeom, final LatLng myPromoGeo, final String myPromoName) {
            runOnUiThread(new Runnable() {
                public void run() {
                    putMarkerImage(myBitmap,myGeom,myPromoGeo,myPromoName);
                }
            });
        }
        @Override
        protected Bitmap doInBackground(String... url) {

            String urldisplay =url[0];
            Bitmap bitmaps = null;
            try {
                InputStream srt = new java.net.URL(urldisplay).openStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(srt);
                bitmaps = myBitmap;
                OnNewSensorData(myBitmap,mygeo,PromoGeo,PromoName);
                Log.e(TAG,"successxx ulol gago inutil"+PromoName+PromoGeo);
            } catch (Exception e){
                Log.d(TAG,"failed");
                e.printStackTrace();
            }

            return bitmaps;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }

    public class GetImageFromURL extends AsyncTask<String,Void,Bitmap> {


        @Override
        protected Bitmap doInBackground(String... url) {

            Log.e(TAG,"doInBackground");
            Bitmap bitmaps = null;
            String myUrl="default";
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("PromoDetails")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String Url = document.getString("promoImageLink");
                                   GetImageFromURL2 myImageUrl = new GetImageFromURL2();

                                    GeoPoint mygeo=  document.getGeoPoint("promoGeo");
                                    LatLng PromoGeo = new LatLng(mygeo.getLatitude(), mygeo.getLongitude());
                                    String PromoName = document.getString("promoname");
                                    myImageUrl.mygeo = mygeo;
                                    myImageUrl.PromoGeo = PromoGeo;
                                    myImageUrl.PromoName = PromoName;
                                    myImageUrl.execute(Url);
                                    Log.e(TAG,"Loops fetch"+globalBitmap);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


            return bitmaps;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

        }
    }


    public void detectGeofence(){

        GeoQuery geoQuery = geoFire.queryAtLocation(userGeo, 20);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.e("OnkeyEnted ",key );

                getGeofenceDetails(key);


            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("ERROR", ""+error);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayService()) {
                        createLocationRequest();
                        buildGoogleApiClient();
                        displayLocation();
                    }
                }
                break;
        }

    }

    private void setUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayService()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        mLastLocaiton = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocaiton != null) {
            final double latitude = mLastLocaiton.getLatitude();
            final double longitude = mLastLocaiton.getLongitude();
            userGeo = new GeoLocation(latitude,longitude);

            // geoFire.setLocation("Chowking02Promo", new GeoLocation(10.232721,123.768191), new GeoFire.CompletionListener() {
            //       @Override
            //  public void onComplete(String key, DatabaseError error) {
            if (mCurrent != null)
                mCurrent.remove();
            mCurrent = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("You"));
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 12);
            mMap.animateCamera(yourLocation);
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, latitude), 12.0f));
            //   }
            //  });

            detectGeofence();
            Log.d(TAG, String.format("Your last location was chaged: %f / %f", latitude, longitude));
        } else {
            Log.d(TAG, "Can not get your location.");
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


    }

    private boolean checkPlayService() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this.getContext());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this.getActivity(), result, PLAY_SERVICE_RESULATION_REQUEST).show();
            } else {
                Toast.makeText(this.getActivity(), "This Device is not supported.", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        return true;
    }



    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void sendNotification(String title, String content,String key) {
        Intent intent = new Intent(this.getContext(), Promo_Detail.class);
        if(key!=null)
            saveInfos(key);
        PendingIntent contentIntent = PendingIntent.getActivity(this.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder = new Notification.Builder(this.getContext())
                .setSmallIcon(R.drawable.hyperdealslogo)
                .setContentTitle(title)
                .setContentText(content);

        NotificationManager manager = (NotificationManager)this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        manager.notify(new Random().nextInt(), notification);
    }
    private void saveInfos(String key){

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("userInfo",this.getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key",key);
        editor.apply();
        Log.d(TAG,"Shared Preferencedd");
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocaiton = location;
        //displayLocation();

    }

    private void getGeofenceDetails(final String key){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PromoDetails").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String storeName = document.getString("promoStore");
                        String promoName = document.getString("promoname");
                        GeoPoint promoLocation = document.getGeoPoint("promoGeo");
                        String subsubTag = document.getString("subsubTag");
                            sendNotification(storeName, promoName, key);
                        Log.d(TAG, storeName+promoName+key);
                        Log.d(TAG, "There such document");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        Log.d(TAG,"get geofence");
    }
    private void geoUserPreference(){
        String userID=LoginActivity.Companion.getUserUIDS();
        Log.d(TAG,userID+"asdkmadkmas");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userID).collection("Cetegories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String subsubCategory=document.getString("subsubcategoryName");
                                Boolean clicked=document.getBoolean("checked");
                                arraysubsub.add(new subsubModel(clicked,subsubCategory));
                                Log.d(TAG,subsubCategory+clicked.toString());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




    }


}

