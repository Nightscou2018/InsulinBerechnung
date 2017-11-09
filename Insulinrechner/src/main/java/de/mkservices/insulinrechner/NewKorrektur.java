package de.mkservices.insulinrechner;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewKorrektur extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_korrektur);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button saveBtn=(Button)findViewById(R.id.btnSaveKorrekturWert);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText vonEdit=(EditText)findViewById(R.id.txtNewKorrekturVon);
                EditText bisEdit=(EditText)findViewById(R.id.txtNewKorrekturBis);
                EditText wertEdit=(EditText)findViewById(R.id.txtNewKorrekturWert);

                String vonStr=String.valueOf(vonEdit.getText()).trim();
                String bisStr=String.valueOf(bisEdit.getText()).trim();
                String wertStr=String.valueOf(wertEdit.getText()).trim();


                if(vonStr.length()==0||bisStr.length()==0||wertStr.length()==0) {
                    Toast.makeText(getApplicationContext(), "Bitte geben Sie in jedes Feld einen Wert ein!", Toast.LENGTH_LONG).show();
                } else {
                    int von=Integer.parseInt(vonStr);
                    int bis=Integer.parseInt(bisStr);
                    float wert=Float.parseFloat(wertStr);

                    Intent intent=new Intent();
                    intent.putExtra("von", von);
                    intent.putExtra("bis", bis);
                    intent.putExtra("wert", wert);
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                    //Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
