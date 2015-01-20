package org.opencv.samples.facedetect;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.opencv.samples.facedetect.R;

/**
 * Created by Alex on 18/01/15.
 */
public class Detail extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        int i = this.getIntent().getIntExtra("num",-1);
        final TextView name;
        final TextView h1;
        final TextView h2;
        final TextView video_url;
        Log.e("blah",i+"");
        name = (TextView) findViewById(R.id.name);
        h1 = (TextView) findViewById(R.id.h1);
        h2 = (TextView) findViewById(R.id.h2);
        video_url = (TextView) findViewById(R.id.video);
        name.setText(FdActivity.properties.getProperty("user"+(i+1)+".nom"));
        h1.setText(FdActivity.properties.getProperty("user"+(i+1)+".h1"));
        h2.setText(FdActivity.properties.getProperty("user"+(i+1)+".h2"));
    }
}