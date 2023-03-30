package com.rtllabs.vehical.ai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CarModelDashBoardActivity extends AppCompatActivity{

    private RelativeLayout rl_frontview;
    private RelativeLayout rl_sideview;
    private RelativeLayout rl_backview;
    private RelativeLayout rl_steering;
    private RelativeLayout rl_tyres;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_carmodel_dashboard);
        rl_frontview = findViewById(R.id.rl_frontview);
        rl_sideview = findViewById(R.id.rl_sideview);
        rl_backview = findViewById(R.id.rl_backview);
        rl_steering = findViewById(R.id.rl_steering);
        rl_tyres = findViewById(R.id.rl_tyres);

        rl_frontview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CarModelDashBoardActivity.this,CarCameraCaptureActivity.class);
                i.putExtra("carposition",1);
                startActivity(i);
            }
        });
        rl_sideview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CarModelDashBoardActivity.this,CarCameraCaptureActivity.class);
                i.putExtra("carposition",2);
                startActivity(i);
            }
        });
        rl_backview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CarModelDashBoardActivity.this,CarCameraCaptureActivity.class);
                i.putExtra("carposition",3);
                startActivity(i);
            }
        });
        rl_steering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CarModelDashBoardActivity.this,CarCameraCaptureActivity.class);
                i.putExtra("carposition",4);
                startActivity(i);
            }
        });
        rl_tyres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(CarModelDashBoardActivity.this,CarCameraCaptureActivity.class);
                i.putExtra("carposition",5);
                startActivity(i);
            }
        });



    }
}
