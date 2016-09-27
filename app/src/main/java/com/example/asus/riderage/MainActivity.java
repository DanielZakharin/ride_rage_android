package com.example.asus.riderage;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private BluetoothManagerClass bluetoothManagerClass;
    private CommunicationHandler communicationHandler;
    private final String TAG = "MainActivity";

    private Button matinTest;
    ImageButton blSelectBtn;
    SpeedometerGauge speedoRPM, speedoSpeed;
    ArrayList<BluetoothDevice> devices;
    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setContentView(R.layout.activity_main);
        this.communicationHandler = CommunicationHandler.getCommunicationHandlerInstance();
        this.bluetoothManagerClass = BluetoothManagerClass.getBluetoothManagerClass();
        this.communicationHandler.passContext(this);
        this.matinTest = (Button) findViewById(R.id.matinTest);
        initButtonListners();
        initSpeedos();
    }

    private void initButtonListners() {
        this.blSelectBtn = (ImageButton) findViewById(R.id.selectDeviceButton);
        this.blSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!communicationHandler.checkBluetoothStatus()) {

                } else showDeviceSelectScreen();
            }
        });

        this.matinTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicationHandler.stopObdJobService();
            }
        });
    }

    private void showDeviceSelectScreen() {

        ArrayList<String> deviceStrs = this.communicationHandler.getDeviceStrings();

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // TODO: 27/09/2016 create connection
                communicationHandler.createBluetoothConnection(which);
            }
        });
        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    private void initSpeedos() {
        speedoRPM = (SpeedometerGauge) findViewById(R.id.speedoRPM);

        speedoRPM.setMaxSpeed(60);
        speedoRPM.setMajorTickStep(10);
        speedoRPM.setMinorTicks(4);

        speedoRPM.addColoredRange(0, 22, Color.GREEN);
        speedoRPM.addColoredRange(22, 32, Color.YELLOW);
        speedoRPM.addColoredRange(32, 60, Color.RED);

        speedoRPM.setLabelTextSize(40);

        speedoRPM.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        speedoSpeed = (SpeedometerGauge) findViewById(R.id.speedoSpeed);
        speedoSpeed.setMaxSpeed(240);
        speedoSpeed.setMajorTickStep(20);
        speedoSpeed.setMinorTicks(1);

        speedoSpeed.setLabelTextSize(20);

        speedoSpeed.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

    }

    private void requestPermission() {
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    public void makeToast(final String stringToShow) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), stringToShow, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void startObdJobService() {
        startService(new Intent(this, ObdJobService.class));
    }

    public void stopObdJobService() {
        stopService(new Intent(this, ObdJobService.class));
    }

    public void updateGauges(final double rpm, final double speed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speedoRPM.setSpeed((rpm / 100), 0, 0);
                speedoSpeed.setSpeed(speed, 0, 0);
            }
        });

    }
}


