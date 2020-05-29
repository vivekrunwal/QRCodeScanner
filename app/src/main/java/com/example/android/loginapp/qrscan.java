package com.example.android.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.loginapp.R;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class qrscan extends AppCompatActivity {
    private Button takePictureButton;
    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    private void permissionAsk(String permission){
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.qrcode);
        super.onCreate(savedInstanceState);

        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        textView=(TextView)findViewById(R.id.textView);
        Button btn_scan = (Button)findViewById(R.id.btn_scan);
        final EditText txt_email = (EditText)findViewById(R.id.txt_email);
        final EditText txt_number = (EditText)findViewById(R.id.txt_number);
        databaseReference = FirebaseDatabase.getInstance().getReference("Student");
        firebaseAuth = FirebaseAuth.getInstance();
        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        String permission = Manifest.permission.CAMERA;
        permissionAsk(permission);
        permissionAsk(Manifest.permission.RECORD_AUDIO);
        permissionAsk(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(640,480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(qrscan.this, new String[] {Manifest.permission.CAMERA}, 0);

                }

                try {
                    cameraSource.start(holder);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size()!=0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            textView.setText(qrCodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });

    btn_scan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String email = MainActivity.email;
            final String password = MainActivity.password;
            final String txt_code = textView.getText().toString();

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(qrscan.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                student information = new student(
                                        email,
                                        password,
                                        txt_code
                                );

                                FirebaseDatabase.getInstance().getReference("Student")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(qrscan.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.

                            }

                            // ...
                        }
                    });

        }
    });

    }
}
