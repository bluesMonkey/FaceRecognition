package org.opencv.samples.facedetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by Alex on 18/01/15.
 */
public class EditDB extends Activity implements OnItemClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editdb);

        final ListView listview = (ListView) findViewById(R.id.listContact);
        Log.e("blah", String.valueOf(FdActivity.properties.getProperty("user1.nom")));
        String[] values = {FdActivity.properties.getProperty("user1.nom"), FdActivity.properties.getProperty("user2.nom"), FdActivity.properties.getProperty("user3.nom"),
                FdActivity.properties.getProperty("user4.nom"),
                FdActivity.properties.getProperty("user5.nom")};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int itemPosition = position;

        Activity context = this;
        Intent defineIntent = new Intent(context, org.opencv.samples.facedetect.Detail.class);

        defineIntent.putExtra("num",position);
        startActivity(defineIntent);
    }
}