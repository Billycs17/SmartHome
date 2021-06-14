package com.billycs.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;
import android.widget.Toast;
import android.opengl.Visibility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ToggleButton mButtonPower;
    ImageView mImageStatus;
    FirebaseDatabase mDatabase;
    String lampstat;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FIREBASE
        String url = "";
        mDatabase = FirebaseDatabase.getInstance(url);
        //buka koneksi he host firebase
        dref = FirebaseDatabase.getInstance().getReference();
        //User Interface
        mButtonPower = findViewById(R.id.button_power);
        mButtonPower.setText("-");
        mButtonPower.setOnClickListener(this);
        mImageStatus = findViewById(R.id.imageView);

        //BOTTOM NAVIGATION CODE
        //Initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.led);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.pump:
                        startActivity(new Intent(getApplicationContext(),
                                Pump.class));
                        overridePendingTransition(0, 0);
                        finish();//menutup class ini
                        return true;
                    case R.id.led:
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

        //Read lamp status
        dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lampstat = dataSnapshot.child("lamp").getValue().toString();
                if(lampstat.equals("1")){
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.lampuon);
                }else{
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.lampuoff);
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
    void toggleLed(){//on dan off lednya
        if(mButtonPower.isChecked()){
            turnOnled();
        } else{
            turnOffled();
        }
    }
    void turnOnled(){
        mButtonPower.setEnabled(false);
        mDatabase.getReference("lamp").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "LAMP ON", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(true);
                    mImageStatus.setImageResource(R.drawable.lampuon);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
    void turnOffled(){
        mButtonPower.setEnabled(false);
        mDatabase.getReference("lamp").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "LAMP OFF", Toast.LENGTH_SHORT).show();
                    mButtonPower.setChecked(false);
                    mImageStatus.setImageResource(R.drawable.lampuoff);
                }
                mButtonPower.setEnabled(true);
            }
        });
    }
}
