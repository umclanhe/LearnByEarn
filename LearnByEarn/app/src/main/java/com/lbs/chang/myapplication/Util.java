package com.lbs.chang.myapplication;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by chang on 12/12/2015.
 */
public class Util {
    public static String writeFile(String content, String path,Context c,int mode){
        try {
            OutputStreamWriter out;
            if(mode==0)
            out = new OutputStreamWriter( c.getApplicationContext().openFileOutput(path, 0));
            else
             out = new OutputStreamWriter( c.getApplicationContext().openFileOutput(path, Context.MODE_APPEND));
            out.write(content);
            out.close();
            String feedb="The contents are saved in the file.";
            return feedb;

        } catch (Throwable t) {

        return t.toString();

        }
    }



    public static String readFile(String path,Context c) throws IOException {
                StringBuilder buf=new StringBuilder();
            // FileInputStream in =  new FileInputStream(path);
           InputStream in =  c.getApplicationContext().openFileInput(path);
            if (in != null) {

                InputStreamReader tmp=new InputStreamReader(in);

                BufferedReader reader=new BufferedReader(tmp);

                String str="";


                while ((str = reader.readLine()) != null) {

                    buf.append(str+"\n");

                }

                in.close();
                System.out.println(buf.toString());

            }




        return buf.toString();
    }

}
