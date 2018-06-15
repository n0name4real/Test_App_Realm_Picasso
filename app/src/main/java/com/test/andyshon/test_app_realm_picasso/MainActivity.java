package com.test.andyshon.test_app_realm_picasso;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm realm;
    // adapter
    public TaskAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI(){
        RealmConfiguration.init(MainActivity.this);
        realm = Realm.getDefaultInstance();

        final RealmResults<Item> tasks = realm.where(Item.class).findAll();
        System.out.println("tasks size = " + tasks.size());

        GridView gridView = (GridView) findViewById(R.id.gridView);
        mAdapter = new TaskAdapter(tasks);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = tasks.get(i);
                String imageId = item.getId();
                String imageName = item.getName();

                Intent intent = new Intent(MainActivity.this, CoverActivity.class);
                intent.putExtra("image_Id", imageId);
                intent.putExtra("image_Name", imageName);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CoverDetailActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
