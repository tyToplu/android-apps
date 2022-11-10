package com.tahayunus.assignmenttravelbook.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import com.tahayunus.assignmenttravelbook.MainActivity;
import com.tahayunus.assignmenttravelbook.R;

import java.io.ByteArrayOutputStream;


public class UploadFragment extends Fragment {
    private SQLiteDatabase sqLiteDatabase;
    private Bitmap selectedImage;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ImageView imageView;
    private Button uploadButton;
    private TextView artView;
    private TextView artistView;
    private TextView yearView;

    public UploadFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sqLiteDatabase = getActivity().openOrCreateDatabase("Arts", MainActivity.MODE_PRIVATE,null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView);
        uploadButton = view.findViewById(R.id.save);
        artView = view.findViewById(R.id.art_name);
        artistView = view.findViewById(R.id.artist_name);
        yearView = view.findViewById(R.id.year_text);

        String newOrOld = UploadFragmentArgs.fromBundle(getArguments()).getNewOrOld();

        if(newOrOld.equals("new")){
            artistView.setText("");
            yearView.setText("");
            artView.setText("");
            uploadButton.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.select_image);
        }else{
            int artId = UploadFragmentArgs.fromBundle(getArguments()).getId();
            uploadButton.setVisibility(View.INVISIBLE);
            try {
                Cursor cursor = this.sqLiteDatabase.rawQuery("SELECT * FROM arts WHERE id = ?",new
                        String[]{String.valueOf(artId)});
                int artIx = cursor.getColumnIndex("artName");
                int artisIx = cursor.getColumnIndex("artistName");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    artView.setText(cursor.getString(artIx));
                    artistView.setText(cursor.getString(artisIx));
                    yearView.setText(cursor.getString(yearIx));

                    byte[]bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    imageView.setImageBitmap(bitmap);

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(v);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(v);
            }
        });
        registerLauncher(imageView);
    }
    public Bitmap getSmallerImage(Bitmap image,Integer maxSize){
        Integer width = image.getWidth();
        Integer height = image.getHeight();
        Float ratio = (float)width / (float)height;
        if(ratio > 1) {
            width = maxSize;
            height = (int)( width/ratio);
        }else{
            height = maxSize;
            width = (int)(ratio * height);
        }

        return image.createScaledBitmap(image,width,height,true);
    }
    private void registerLauncher(ImageView view){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == -1){
                            Intent intentFromResult = result.getData();
                            if(intentFromResult != null){
                                Uri imageData = intentFromResult.getData();
                                //binding.selectImage.setImageURI(imageData);
                                try {
                                    if(Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(getContext().
                                                        getContentResolver(),
                                                imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        view.setImageBitmap(getSmallerImage(selectedImage,300));
                                    }else{
                                        selectedImage = MediaStore.Images.Media.getBitmap(
                                                getContext().getContentResolver(), imageData);
                                        view.setImageBitmap(getSmallerImage(selectedImage,300));
                                    }
                                }catch (Exception e){
                                    System.out.println("hata");
                                    System.out.println(e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result){
                            //access granted
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intentToGallery);////////
                        }else{
                            //access denied
                            Toast.makeText(getContext(),"Permission needed"
                                    ,Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery access",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }
    public void save (View view){
        String artName = artView.getText().toString();
        String artistName = artistView.getText().toString();
        Integer year = Integer.parseInt(yearView.getText().toString());

        Bitmap smallImage = getSmallerImage(selectedImage,300);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        try {
            this.sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artName VARCHAR,artistName VARCHAR" +
                    ",year INTEGER,image BLOB)");
            String sqLiteStatement = "INSERT INTO arts (artName, artistName,year, image) VALUES (?,?,?,?)";
            SQLiteStatement statement = this.sqLiteDatabase.compileStatement(sqLiteStatement);

            //index starts from one!
            statement.bindString(1,artName);
            statement.bindString(2,artistName);
            statement.bindLong(3,year);
            statement.bindBlob(4,bytes);

            // if you weren't to say execute, it wouldn't save it;
            statement.execute();

            /*Intent intent = new Intent(ArtActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
             */
            NavDirections directions = UploadFragmentDirections
                    .actionUploadFragmentToRecyclerViewFragment();
            Navigation.findNavController(view).navigate(directions);



        }catch (Exception e){

        }
    }
}