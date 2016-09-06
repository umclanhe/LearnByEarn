package com.lbs.chang.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class CouponActivity extends ActionBarActivity {
    int score=0;
    int point=0;
    String code="";
    String username="";
    long time=0;
    RadioGroup rg;
    Button submit;
    Button cancel;
    String infor="You don't have enough score";
    TextView scoret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username=extras.getString("username");
            score = extras.getInt("score");
            time=extras.getLong("time");
        }
        scoret= (TextView) findViewById(R.id.rewscore);
        scoret.setText(score+"");
        rg= (RadioGroup) findViewById(R.id.rgcoupon);
        rg.setOnCheckedChangeListener(new couponListener());
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new submitListener());
        cancel= (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.putExtra("newscore",score);
                setResult(RESULT_OK, intent);
                finish();

            }
        });


    }
    private class couponListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {

            switch (i){
            case R.id.c50:
                if(score<50) {

                    Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                            .setTitle("RewardStore")
                            .setMessage(infor)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    submit.setClickable(false);
                    submit.setBackgroundColor(Color.GRAY);
                }
                else {
                    point=50;
                    submit.setClickable(true);
                    submit.setBackgroundColor(0Xffffb453);
                }
                break;
            case R.id.c100:
                if(score<100) {
                    Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                            .setTitle("RewardStore")
                            .setMessage(infor)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    submit.setClickable(false);
                    submit.setBackgroundColor(Color.GRAY);
                }
                else {
                    point=100;
                    submit.setClickable(true);
                    submit.setBackgroundColor(0Xffffb453);
                }
                break;
            case R.id.c150:
                if(score<150) {
                    Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                            .setTitle("RewardStore")
                            .setMessage(infor)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    submit.setClickable(false);
                    submit.setBackgroundColor(Color.GRAY);
                }
                else {
                    point=150;
                    submit.setClickable(true);
                    submit.setBackgroundColor(0Xffffb453);
                }
                break;
            case R.id.c200:
                if(score<200) {
                    Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                            .setTitle("RewardStore")
                            .setMessage(infor)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).create();
                    dialog.show();
                    submit.setClickable(false);
                    submit.setBackgroundColor(Color.GRAY);
                }
                else {
                    point=200;
                    submit.setClickable(true);
                    submit.setBackgroundColor(0Xffffb453);
                }
                break;

        }
        }
    }

    private class submitListener implements View.OnClickListener

    {
        @Override
        public void onClick(View view) {
            if(score>=point&&point!=0){
                score-=point;
                scoret.setText(score+"");
                Util.writeFile(username+"\n"+score+"\n"+time,"user.txt",getApplicationContext(),0);
                try {
                    code=getCode(username);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                        .setTitle("Coupon Code")
                        .setMessage("The coupon code is " + code + ". You can find it in history.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                    Util.writeFile("Your Coupon code of "+point+"points is "+code+"\n","coupon.txt",getApplicationContext(),1);
                            }
                        }).create();
                dialog.show();
            }
            else{
                Dialog dialog = new AlertDialog.Builder(CouponActivity.this)
                        .setTitle("RewardStore")
                        .setMessage(infor)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).create();
                dialog.show();
                submit.setClickable(false);
            }
            submit.setClickable(false);
            submit.setBackgroundColor(Color.GRAY);
        }
    }

    protected String getCode(String username) throws NoSuchAlgorithmException {
        this.username = username;
        Date date=new Date();
        String s=username+Long.toString(date.getTime());
        MessageDigest alga = MessageDigest.getInstance("SHA-1");
        alga.update(s.getBytes());
        byte[] digesta = alga.digest();
       // System.out.println("Digest with SHA: " + tohex(digesta));
        return tohex(digesta);

    }
    private static String tohex(byte[] b)
    {
        String hs = "";
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            //0xff== 00 00 00 ff   255
            stmp = (java.lang.Integer.toHexString(b[i] & 0xFF));  //b[n]&0xFF is for byte to int
            //System.out.println(stmp);
            if (stmp.length() == 1) // 0-255  one or two bit hex
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }

        return hs.toUpperCase();
    }
}
