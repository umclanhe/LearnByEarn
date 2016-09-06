package com.lbs.chang.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class HistoryActivity extends ActionBarActivity {
    TextView history;
    Button back;
    String username;
    TextView hisname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        history= (TextView) findViewById(R.id.history);
        hisname= (TextView) findViewById(R.id.hisusername);
        back=(Button) findViewById(R.id.back);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username=extras.getString("username");
            hisname.setText(username);
    }
        String content="";

        try {
            content=Util.readFile("coupon.txt",getApplicationContext());
            if(content.equals("")){
                history.setText("You don't have any reward now!");

            }
            else{
                history.setText(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

}
