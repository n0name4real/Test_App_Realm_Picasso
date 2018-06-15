package com.test.andyshon.test_app_realm_picasso;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class CoverDetailActivity extends AppCompatActivity implements View.OnClickListener{

    public final int SELECT_IMAGE = 100;
    private ImageView ivCover;
    private EditText etName;
    private Realm realm;
    private Item currentItem;
    private String image_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover_detail);

        initUI();
    }

    private void initUI(){
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

        Button btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUploadImage.setOnClickListener(this);

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        Button btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setVisibility(View.GONE);
        btnRemove.setOnClickListener(this);

        ivCover = findViewById(R.id.ivCover);
        ivCover.setImageResource(R.drawable.ic_compare_arrows_black_24dp);
        etName = findViewById(R.id.etName);

        if (image_Id != null) {
            btnRemove.setVisibility(View.VISIBLE);
            RealmObject object = getFirstObject(image_Id);
            currentItem = (Item)object;
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

    private void ConfirmDeleteImage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoverDetailActivity.this);

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

    private RealmObject getFirstObject(String id) {
        return realm.where(Item.class).equalTo("id", id).findFirst();
    }

    private void SelectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите картинку"), SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        int dataSize=0;

        switch (requestCode) {
            case SELECT_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = imageReturnedIntent.getData();

                    String scheme = imageUri.getScheme();
                    if(scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                        try {
                            InputStream fileInputStream=getApplicationContext().getContentResolver().openInputStream(imageUri);
                            dataSize = fileInputStream.available();
                            /*if (dataSize >= 1000000){
                                Toast.makeText(this, "Размер картинки не должен превышать 1 МБ! Размер выбранной картинки: " + dataSize,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {*/
                            Toast.makeText(this, "Размер картинки = " + dataSize, Toast.LENGTH_SHORT).show();

                            Bitmap bitmap = new Utils().decodeUri(imageUri, CoverDetailActivity.this);
                            ivCover.setImageBitmap(bitmap);
                            // }
                        } catch (Exception e) {e.printStackTrace();}
                    }
                }
        }
    }

    private void SaveOrUpdateImage() {
        if (image_Id != null){
            System.out.println("UpdateImage()");
            if (CheckItem())
                UpdateImage();
            else
                Toast.makeText(this, "Отсутствует картинка или её название", Toast.LENGTH_SHORT).show();
        }
        else {
            System.out.println("SaveImage()");
            if (CheckItem())
                SaveImage();
            else
                Toast.makeText(this, "Отсутствует картинка или её название", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean CheckItem(){
        if ((ivCover == null) || (etName.getText().length() == 0)){
            return false;
        }
        else {
            return true;
        }
    }
    private void SaveImage() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
            Bitmap bmp = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            final byte[] byteArray = stream.toByteArray();
            System.out.println("array:" + byteArray.length);

            realm.beginTransaction();
            Item item = new Item();
            item.setId(String.valueOf(new Random().nextInt(10000000) + System.currentTimeMillis()));
            item.setName(etName.getText().toString().trim());
            item.setBitmap(byteArray);
            realm.copyToRealm(item);
            realm.commitTransaction();
            finish();
        } catch (ClassCastException ex){
            Toast.makeText(this, "Картинка не добавлена", Toast.LENGTH_SHORT).show();
        }
    }

    private void UpdateImage(){
        BitmapDrawable drawable = (BitmapDrawable) ivCover.getDrawable();
        Bitmap bmp = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] byteArray = stream.toByteArray();
        System.out.println("array:" + byteArray.length);

        realm.beginTransaction();
        Item item = new Item();
        item.setId(currentItem.getId());
        item.setName(etName.getText().toString().trim());
        item.setBitmap(byteArray);
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
        finish();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUploadImage:
                SelectImageFromGallery();
                break;
            case R.id.btnSave:
                SaveOrUpdateImage();
                break;
            case R.id.btnRemove:
                ConfirmDeleteImage();
                break;
        }
    }
}
