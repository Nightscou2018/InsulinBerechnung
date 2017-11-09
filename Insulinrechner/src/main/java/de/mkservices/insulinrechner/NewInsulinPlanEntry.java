package de.mkservices.insulinrechner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewInsulinPlanEntry extends AppCompatActivity {
    public static final int NEW_KORREKTUR_INTENT=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_insulin_plan_entry);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Button newKorrekturBtn=(Button)findViewById(R.id.btnNeueKorrektur);
        newKorrekturBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), NewKorrektur.class);
                startActivityForResult(intent, NEW_KORREKTUR_INTENT);
            }
        });

        Button saveBtn=(Button)findViewById(R.id.btnNeuesPlanItemSpeichern);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
                try {
                    String tmp=sharedPref.getString(getString(R.string.INSULIN_PLAN),"[]");
                    JSONArray jArr=new JSONArray(tmp);

                    EditText name=(EditText)findViewById(R.id.txtEditName);
                    EditText faktor=(EditText)findViewById(R.id.txtEditFaktor);

                    String nameStr=String.valueOf(name.getText()).trim();
                    float faktorFloat=Float.parseFloat(String.valueOf(faktor.getText()).trim());

                    String korrStr=sharedPref.getString(getString(R.string.TMP_KEY), "[]");
                    JSONArray korr=new JSONArray(korrStr);

                    InsulinPlanEntry ipe=new InsulinPlanEntry(nameStr, faktorFloat, korr);
                    JSONObject jObj=ipe.toJsonObject();
                    jArr.put(jObj);
                    SharedPreferences.Editor edit=sharedPref.edit();
                    edit.putString(getString(R.string.INSULIN_PLAN), jArr.toString());
                    edit.apply();
                } catch(JSONException e){}
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==NEW_KORREKTUR_INTENT&&resultCode==RESULT_OK) {
            int von=data.getIntExtra("von", 0);
            int bis=data.getIntExtra("bis", 0);
            float wert=data.getFloatExtra("wert", 0);

            SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
            String oldKorr=sharedPref.getString(getString(R.string.TMP_KEY), new JSONArray().toString());
            JSONArray jArr=new JSONArray();
            try {
                jArr = new JSONArray(oldKorr);

                JSONObject jObj=new JSONObject();
                jObj.put("von", von);
                jObj.put("bis", bis);
                jObj.put("wert", wert);
                jArr.put(jObj);
                SharedPreferences.Editor edit=sharedPref.edit();
                edit.putString(getString(R.string.TMP_KEY), jArr.toString());
                edit.apply();
                StringBuilder sb=new StringBuilder();
                for(int i=0;i<jArr.length(); i++){
                    JSONObject tmp=jArr.getJSONObject(i);
                    KorrekturWert korrWert=new KorrekturWert();
                    korrWert.von=tmp.getInt("von");
                    korrWert.bis=tmp.getInt("bis");
                    korrWert.wert=(float)tmp.getDouble("wert");
                    sb.append("Von: ").append(korrWert.von).append(" Bis: ").append(korrWert.bis).append(" Wert: ");
                    sb.append(korrWert.wert).append(System.lineSeparator());
                }
                TextView korrValTxt=(TextView)findViewById(R.id.txtKorrekturwerte);
                korrValTxt.setText(sb.toString());
            } catch(JSONException e){
            }
        }
    }
}
