package com.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private  JSONArray array_ImageBitmap;
    private RecyclerView recyclerView;
    private Button pic_btn;

    private RecyclerView types_good_recyclerView;
    private ArrayList<Bitmap> listImagesGoods;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final int REQUEST_CODE_STORAGE_PERMS = 321;
    public static int imagecount = 0;
    private Uri file;
    GoodsImagesAdapter goodsImagesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerImages);
        pic_btn = findViewById(R.id.pic_btn);


        Bitmap bitmap1 = null,bitmap2=null,bitmap3=null,bitmap4=null;
        listImagesGoods = new ArrayList<>();
        listImagesGoods.add(bitmap1);
/*        listImagesGoods.add(bitmap2);
        listImagesGoods.add(bitmap3);
        listImagesGoods.add(bitmap4);*/
        setImagesList();

        pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    if (!hasPermissions()){requestNecessaryPermissions();}
                    else {openCamera();}
                } else {
                    Toast.makeText(MainActivity.this, "Camera not supported", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    //--------------------------------------------Camera Permisstion------------------------
    @SuppressLint("WrongConstant")
    private boolean hasPermissions() {
        int res = 0;
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String perms : permissions){
            res = this.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                // it return false because your app dosen't have permissions.
                return false;
            }
        }return true;
    }

    private void requestNecessaryPermissions() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE_STORAGE_PERMS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grandResults) {
        boolean allowed = true;
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMS:
                for (int res : grandResults) {
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }
        if (allowed) {
            openCamera();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(this, "Camera Permissions denied", Toast.LENGTH_SHORT).show();
                }
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Toast.makeText(this, "Storage Permissions denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


//--------------------------------openCamera----------------

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),file);
                ExifInterface ei = new ExifInterface(file.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                goodsImagesAdapter = (GoodsImagesAdapter) recyclerView.getAdapter();
                goodsImagesAdapter.notiFydata(getResizedBitmap(rotatedBitmap,400), imagecount);
                imagecount++;
            }catch (Exception e){

            }
        }
    }

    private void setImagesList() {
        goodsImagesAdapter = new GoodsImagesAdapter(MainActivity.this, listImagesGoods);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(goodsImagesAdapter);
        //recyclerView.getLayoutManager().scrollToPosition(recyclerView.getHorizontalFadingEdgeLength());
        goodsImagesAdapter.notifyDataSetChanged();

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width/ (float) height;
        if (bitmapRatio < 1 && width > maxSize) {

            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else if(height > maxSize){
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private File getFile() {
        File folder = Environment.getExternalStoragePublicDirectory("/From_camera/ka");// the file path
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timeStamp + "_";
        File image_file = null;
        try {
            image_file = File.createTempFile(imageFileName,".jpg",folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // mCurrentPhotoPath = image_file.getAbsolutePath();
        return image_file;
    }

    private File onCaptureImageResult(Bitmap bitmap) {
        File imgFile;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        imgFile = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            imgFile.createNewFile();
            fo = new FileOutputStream(imgFile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imgFile;
    }


//---------------------------------------------------End Camera Permisstion-----------------

}

