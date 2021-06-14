package com.billycs.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Pump extends AppCompatActivity implements View.OnClickListener{
    ToggleButton mButtonPower;
    ImageView mImageStatus;
    FirebaseDatabase mDatabase;
    String pumpstat;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);
        String url = "";
        mDatabase = FirebaseDatabase.getInstance(url);
        dref = FirebaseDatabase.getInstance().getReference();
        //User Interface
        mButtonPower = findViewById(R.id.button_power);
        mButtonPower.setText("-");
        mButtonPower.setOnClickListener(this);
        mImageStatus = findViewById(R.id.imageView);

        //BOTTOM NAVIGATION CODE
        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Pump Selected
        bottomNavigationView.setSelectedItemId(R.id.pump);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pump:
                        return true;
                    case R.id.led:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.rain:
                        startActivity(new Intent(getApplicationContext(),
                                rain.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pumpstat = dataSnapshot.child("pump").getValue().toString();
                if(pumpstat.equals("1")){
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.penyiramanon);
                }else{
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.penyiramanoff);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_power:
                toggleLed();
                break;
        }
    }
    void toggleLed(){//on and off pump
        if(mButtonPower.isChecked()){
            turnOnpump();
        }else{
            turnOffpump();
        }
    }
    void turnOnpump(){
        mButtonPower.setEnabled(false);
        mDatabase.getReference("pump").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Pump.this, "PUMP ON", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.penyiramanon);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
    void turnOffpump() {
        mButtonPower.setEnabled(false);
        mDatabase.getReference("pump").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Pump.this, "PUMP OFF", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.penyiramanoff);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
}