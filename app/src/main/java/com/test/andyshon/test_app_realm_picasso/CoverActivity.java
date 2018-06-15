package com.test.andyshon.test_app_realm_picasso;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class CoverActivity extends AppCompatActivity {

    private Realm realm;
    private String image_Id;
    private ImageView ivCover;
    private TextView etName;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);

        realm = Realm.getDefaultInstance();

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#a0ff8000")));
            bar.setTitle("Детали");
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        //image_Name = intent.getStringExtra("image_Name");
        image_Id = intent.getStringExtra("image_Id");

        ivCover = findViewById(R.id.ivCover);
        ivCover.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
        etName = findViewById(R.id.tvName);
    }

    private void getCover(){
        if (image_Id != null) {
            RealmObject object = getFirstObject(image_Id);
            currentItem = (Item) object;
            System.out.println("currentItem:" + currentItem.getName());
            etName.setText(currentItem.getName());

            if (currentItem.getBitmap() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(currentItem.getBitmap(), 0, currentItem.getBitmap().length);
                ivCover.setImageBitmap(bitmap);
            } else {
                System.out.println("Bitmap == null");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCover();
    }

    private RealmObject getFirstObject(String id) {
        return realm.where(Item.class).equalTo("id", id).findFirst();
    }

    private void ConfirmDeleteImage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoverActivity.this);

        alertDialogBuilder
                .setMessage("Удалить " + currentItem.getName() + "?")
                .setCancelable(false)
                .setPositiveButton("Да",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        DeleteImage();
                    }
                })
                .setNegativeButton("Нет",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    private void DeleteImage() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Item> result = realm.where(Item.class).equalTo("id", image_Id).findAll();
                result.deleteAllFromRealm();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.updateDetail:
                Intent intent = new Intent(CoverActivity.this, CoverDetailActivity.class);
                intent.putExtra("image_Id", image_Id);
                intent.putExtra("image_Name", currentItem.getName());
                startActivity(intent);
                return true;
            case R.id.deleteDetail:
                ConfirmDeleteImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_simple, menu);
        return true;
    }
}
