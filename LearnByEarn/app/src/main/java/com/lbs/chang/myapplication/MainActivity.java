package com.lbs.chang.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import  android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    private static final long MIN_TIME_BW_UPDATES =5000 ;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES =10 ;
    private Button but;
    private TextView uname;
    private TextView uscore;
    private ArrayList<PLocation> locationList;
    private GoogleMap map;
    private Location location;
    private Dialog dialog;
    private String username="User";
    private int score=0;
    private String file="";
    private String[] uinfor;
    private long lasttime=0;
    final private String PATH="user.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.but = (Button) super.findViewById(R.id.button);
        this.uname= (TextView) super.findViewById(R.id.name);
        this.uscore= (TextView) super.findViewById(R.id.score);

        Util.writeFile(username+"\n"+50+"\n"+lasttime,"user.txt",getApplicationContext(),0);
        try {
            file= Util.readFile(PATH, this);
        } catch (IOException e) {
            showToast(e.toString());
        }
        uinfor = file.split("\n");
        username = uinfor[0];
        score = Integer.parseInt(uinfor[1]);
        lasttime=Long.parseLong(uinfor[2]);
        uname.setText( username);
        uscore.setText(score+"");

        locationList=getLocationList();
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapfrag)).getMap();
        initMap();

        but.setOnClickListener(new checkinListener());

    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.finish){
            finish();
        }
        if(id == R.id.coupon){
            Intent coupon = new Intent(getApplicationContext(), CouponActivity.class);
            coupon.putExtra("username",username);
            coupon.putExtra("score",score);
            coupon.putExtra("time",lasttime);
            startActivityForResult(coupon,1);
        }
        if(id==R.id.menuhistory){
            Intent history=new Intent(getApplicationContext(),HistoryActivity.class);
            history.putExtra("username",username);
            startActivity(history);
        }
        if(id==R.id.username){
            // get prompts.xml view
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View promptView = layoutInflater.inflate(R.layout.prompts, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set prompts.xml to be the layout file of the alertdialog builder
            alertDialogBuilder.setView(promptView);
            final EditText inputname = (EditText) promptView.findViewById(R.id.userInput);

            // setup a dialog window
            alertDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // get user input and set it to result
                            username=inputname.getText().toString();
                            uname.setText(username);
                            String content=username+"\n"+Integer.toString(score)+"\n"+lasttime;
                            showToast(Util.writeFile(content, PATH, getApplicationContext(),0));
                        }
                    });

            // create an alert dialog
            AlertDialog alertD = alertDialogBuilder.create();

            alertD.show();
            uname.setText(username);
            uscore.setText( score+"");

        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                score=data.getExtras().getInt("newscore");
                uscore.setText(score+"");
            }
        }
    }
    private void showToast(String s){

            Toast.makeText(this, s, Toast.LENGTH_LONG)

                    .show();

    }


    private class checkinListener implements OnClickListener {
        public void onClick(View v) {
            int flag;
            flag=findNearbyLocation(location,locationList);
            Date date = new Date();
            long currenttime=date.getTime();
            if(flag!=-1&&flag!=5&&flag!=-2) {
                String name = locationList.get(flag).getName();

                if ((currenttime - lasttime) > 2 * 3600000){
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("EarnByLearn")
                            .setMessage("You are at " + name + "\nYou can get 10 points.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    score = score + 10;
                    uname.setText( username);
                    uscore.setText( score+"");
                    String s = username + "\n" + Integer.toString(score) + "\n" + currenttime;
                    showToast(Util.writeFile(s, PATH, getApplicationContext(),0));
                    lasttime=currenttime;
                }
                else{
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("EarnbyLearn")
                            .setMessage("You have checked in too frequently. Please check in later.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create();
                    dialog.show();
                }
            }
            else if(flag==5) {
                if ((currenttime - lasttime) > 2 * 3600000) { //can not checkin again during  2 hour
                    String name = locationList.get(flag).getName();
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("EarnbyLearn")
                            .setMessage("You are at " + name + "\nYou can get 20 points.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    score = score + 20;
                    uname.setText( username);
                    uscore.setText( score+"");
                    String s = username + "\n" + Integer.toString(score)+ "\n" + currenttime;
                    showToast(Util.writeFile(s, PATH, getApplicationContext(),0));
                    lasttime=currenttime;
                }
                else{
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("EarnbyLearn")
                            .setMessage("You have checked in too frequently. Please check in later.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            }).create();
                    dialog.show();
                }

            }
            else {
                if (flag != -2) {
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("EarnbyLearn")
                            .setMessage("You cannot get points here")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                }
            }
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    private void initMap() {
        if (map != null) {
            //System.out.println("show map now");
            // to set current location
            map.setMyLocationEnabled(true);
            location=getLocation();
            if(location!=null){
                //PLACE THE INITIAL MARKER
                drawMarker(location);
                LatLng curl = new LatLng(location.getLatitude(),location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(curl, 15));
            }
//            locationManager.requestLocationUpdates(provider, 50000, 0, locationListener);
            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }
    private Location getLocation() {
        try {
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            LocationListener locationListener = new LocationListener() {

                public void onLocationChanged(Location location) {
                    // redraw the marker when get location update.
                    drawMarker(location);
                    LatLng curl = new LatLng(location.getLatitude(), location.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(curl, 15));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                latitude = location.getLatitude();
//                                longitude = location.getLongitude();
//                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
    private void drawMarker(Location location) {
        // Remove any existing markers on the map
        map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("Lat:" + location.getLatitude() + "Lng:" + location.getLongitude())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("curent position"));
    }

    private int  findNearbyLocation(Location location, ArrayList<PLocation> locationList){
        int n=locationList.size();
        double min=99999999;
        int flag=-1;
        if(location!=null) {
            for (int i = 0; i < n; i++) {

                if (min > locationList.get(i).getDistance(location)) {
                    min = locationList.get(i).getDistance(location);
                    flag = i;
                }
            }
            if (min < 100) {
                return flag;
            } else return -1;
        }
        else {
            dialog =new AlertDialog.Builder(MainActivity.this)
                    .setTitle("EarnbyLearn")
                    .setMessage("Getting your location... Please check your GPS is on.")
                    .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).create();
            dialog.show();
            return -2;
        }
    }
    private ArrayList<PLocation> getLocationList(){
        ArrayList<PLocation> mList=new ArrayList<PLocation>();
        PLocation mylocation1=new PLocation(40.441640, -79.953817, "Wesley W. Posvar Hall", "230 S Bouquet St Pittsburgh" ," No Camputer Lab");
        mList.add(mylocation1);
        PLocation mylocation2=new PLocation(40.447412, -79.952666, "Information Sciences Building", " 135 North Bellefield Avenue,","computer Lab");
        mList.add(mylocation2);
        PLocation mylocation3=new PLocation(40.445751, -79.957834, "Chevron Science Center", "219 Parkman Avenue " ,"Computer Lab");
        mList.add(mylocation3);
        PLocation mylocation4=new PLocation(40.445433, -79.950617, "Bellefield Hall", "315 S. Bellefield Ave " ,"No Computer Lab");
        mList.add(mylocation4);
        PLocation mylocation5=new PLocation(40.443100, -79.961370, "Scaife Hall", "Alan Magee Scaife Hall, 3550 Terrace Street " , "No computer Lab");
        mList.add(mylocation5);
        PLocation mylocation6=new PLocation(40.442611, -79.954155, "Hillman Library", "3960 Forbes Ave Pittsburgh, PA 15260","Computer Lab");
        mList.add(mylocation6);
        PLocation mylocation7=new PLocation(40.444286, -79.953193, "Cathedral of Learning", "4200 Fifth Ave,Pittsburgh, PA 15213","No Computer Lab");
        mList.add(mylocation7);
        PLocation mylocation8=new PLocation(40.445306, -79.961672, "Sutherland Hall", "Panther Hall, 3725 Sutherland Dr","No Computer Lab");
        mList.add(mylocation8);
        PLocation mylocation11 = new PLocation(40.442509,-79.951328,"Frick Fine Arts Library","4127 Schenley Dr,Pittsburgh, PA 15213","Computer Lab");
        mList.add(mylocation11);
        PLocation mylocation12 = new PLocation(40.441314,-79.953601,"Mervis Hall","3950 Roberto Clemente Dr,Pittsburgh, PA 15260","No Computer Lab");
        PLocation mylocation13 = new PLocation(40.441333,-79.961325,"Victoria Hall","Biomedical Science Tower, Pittsburgh, PA 15213","No Computer Lab");
        PLocation mylocation14 = new PLocation(40.445706,-79.954044,"B-40 Alumni Hall Computing Lab","Alumni Hall,Pittsburgh, PA,15213","Computer Lab");
        PLocation mylocation15 = new PLocation(40.445915,-79.957866,"Chemistry Library","100 S Bouquet St,Pittsburgh, PA,15213","Computer Lab");
        PLocation mylocation16 = new PLocation(40.446823,-79.953700,"Langley Library","Langley Hall, Pittsburgh, PA 15260","Computer Lab");
        PLocation mylocation17 = new PLocation(40.446772,-79.952317,"Music Building","Music Bldg,4337 Fifth Ave,Pittsburgh,PA 15213","Computer Lab");
        PLocation mylocation18 = new PLocation(40.444138,-79.958142,"Bevier Engineering Library","Mascaro Center,Pittsburgh, PA 15213","Computer Lab");
        PLocation mylocation19 = new PLocation(40.443424,-79.949947,"Carnegie Complex","Carnegie Museum&Library,4400 Forbes Ave,Pittsburgh, PA 15213","Computer Lab");
        PLocation mylocation20 = new PLocation(40.442037,-79.947333,"Falk Library of the Health Sciences","5000 Forbes Ave, Pittsburgh, PA 15213","Computer Lab");
        PLocation mylocation21 = new PLocation(40.444259,-79.95874,"Old Engineering Hall","Learning Research and Development Center,Pittsburgh, PA 15213","No Computer Lab");
        PLocation mylocation22 = new PLocation(40.443829,-79.958651,"Benedum Hall","3700 O'Hara St, Pittsburgh, PA 15213","No Computer Lab");
        mList.add(mylocation12);
        mList.add(mylocation13);
        mList.add(mylocation14);
        mList.add(mylocation15);
        mList.add(mylocation16);
        mList.add(mylocation17);
        mList.add(mylocation18);
        mList.add(mylocation19);
        mList.add(mylocation20);
        mList.add(mylocation21);
        mList.add(mylocation22);
        return mList;
    }

}


