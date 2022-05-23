package com.example.cakesbeans;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        Button btnSeeMenu;
        ImageView btnProfile;

        ImageSlider imageSlider1;
        imageSlider1 = findViewById(R.id.image_slider);
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.bannerlogo, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.cakes, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.coffee, ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel(R.drawable.coffee2, ScaleTypes.CENTER_CROP));
        imageSlider1.setImageList(imageList);

        btnSeeMenu = findViewById(R.id.button_see_menu);
        btnSeeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(Menu.this, MainMenu.class);
                startActivity(next);
                finish();
            }
        });
        //dp bilog
        btnProfile = findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcPopupProfile();
            }
        });
    }

    private void funcPopupProfile() {
        Intent next = new Intent(Menu.this, Profile.class);
        startActivity(next);
        finish();
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Quit App?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}