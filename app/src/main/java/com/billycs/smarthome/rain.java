package com.billycs.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class rain extends AppCompatActivity implements View.OnClickListener{
    ToggleButton mButtonPower;
    ImageView mImageStatus;

    private TextView nilai;
    FirebaseDatabase mDatabase;
    String status,servostat;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain);
        String url = "";
        mDatabase = FirebaseDatabase.getInstance(url);
        //buka koneksi he host firebase
        dref = FirebaseDatabase.getInstance().getReference();
        //User Interface
        mButtonPower = findViewById(R.id.button_power);
        mButtonPower.setText("-");
        mButtonPower.setOnClickListener(this);
        mImageStatus = findViewById(R.id.imageView);

        nilai =(TextView)findViewById(R.id.nilai);

        //BOTTOM NAVIGATION CODE
        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.rain);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pump:
                        startActivity(new Intent(getApplicationContext(),
                                Pump.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.led:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.rain:
                        return true;
                }
                return false;
            }
        });

        //Read Rain Sensor
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get field value
                status = dataSnapshot.child("rain").getValue().toString();
                //show value to "nilai"
                nilai.setText(status);
                servostat = dataSnapshot.child("servo").getValue().toString();
                if(servostat.equals("1")){
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.jemuranon);
                }else{
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.jemuranoff);
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
                toggleServo();
                break;
        }
    }

    void toggleServo(){
        if(mButtonPower.isChecked()){
            turnOnservo();
        }else{
            turnOffservo();
        }
    }
    void turnOnservo(){
        mButtonPower.setEnabled(false);
        dref.getDatabase().getReference("servo").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(rain.this, "SHIELD ON", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.jemuranon);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
    void turnOffservo() {
        mButtonPower.setEnabled(false);
        dref.getDatabase().getReference("servo").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(rain.this, "SHIELD OFF", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.jemuranoff);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
}
