package de.mkservices.insulinrechner;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ImportPlan extends AppCompatActivity {
    private Uri data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_plan);

        data=getIntent().getData();
        if(data!=null){
            getIntent().setData(null);
            try {
                int check= ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if(check== PackageManager.PERMISSION_GRANTED){
                    importData(data);
                    Intent intent=new Intent(this,InsulinBerechnung.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1024);
                }
            } catch(Exception e){
                finish();
                return;
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1024: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "The app was allowed to read from your storage", Toast.LENGTH_LONG).show();
                    importData(data);
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to read from your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void importData(Uri data){
        final String scheme=data.getScheme();

        try{
            ContentResolver cr=getApplicationContext().getContentResolver();
            InputStream is=cr.openInputStream(data);
            if(is==null) return;
            StringBuffer buf=new StringBuffer();
            BufferedReader reader=new BufferedReader(new InputStreamReader(is));
            String str;
            if(is!=null){
                while((str=reader.readLine())!=null){
                    buf.append(str);
                }
            }
            is.close();

            SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
            SharedPreferences.Editor edit=sharedPref.edit();
            edit.putString(getString(R.string.INSULIN_PLAN), buf.toString());
            edit.apply();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
