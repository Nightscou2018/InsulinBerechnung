package de.mkservices.insulinrechner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InsulinBerechnung extends AppCompatActivity {

    private final ArrayList<InsulinPlanEntry> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulin_berechnung);

        setInsulinPlanList();
        Spinner spn=(Spinner)findViewById(R.id.spinner_zeitwahl);
        ArrayAdapter adapter=new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        spn.setAdapter(adapter);

        Button btn=(Button)findViewById(R.id.btnCalcIe);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText bzEdit=(EditText)findViewById(R.id.txtBzValue);
                EditText keEdit=(EditText)findViewById(R.id.txtKeValue);
                Spinner spn=(Spinner)findViewById(R.id.spinner_zeitwahl);
                TextView tv=(TextView)findViewById(R.id.ieAnzeige);

                String bzString=String.valueOf(bzEdit.getText()).trim();
                String keString=String.valueOf(keEdit.getText()).trim();
                InsulinPlanEntry ipe=(InsulinPlanEntry)spn.getSelectedItem();

                float ie=Integer.parseInt(keString)*ipe.faktor;

                int bz=Integer.parseInt(bzString);
                if(bz>149){
                    ArrayList<KorrekturWert> korrekturWerte=ipe.korrekturWerte;
                    for(int i=0;i<korrekturWerte.size();i++){
                        KorrekturWert kw=korrekturWerte.get(i);
                        if((kw.von<=bz)&&(kw.bis>=bz)){
                            ie+=korrekturWerte.get(i).wert;
                            break;
                        }
                    }
                }
                tv.setText(String.valueOf(ie));
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        setInsulinPlanList();
        Spinner spn=(Spinner)findViewById(R.id.spinner_zeitwahl);
        ArrayAdapter adapter= (ArrayAdapter) spn.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menuInsulinPlan:
                Intent intent=new Intent(this, InsulinPlanListe.class);
                startActivity(intent);
                return true;
            case R.id.shareMenu:

                SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), MODE_PRIVATE);
                String iplan=sharedPref.getString(getString(R.string.INSULIN_PLAN),"");
                Intent sharingIntent=new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("application/octet-stream");
                String shareBodyText="Im Anhang befindet sich ein Insulinplan. Um den Plan zu importieren, öffnen Sie die angehängte Datei. Dies startet die InsulinBerechnungs-App und importiert die Daten.";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Insulin-Plan");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);

                String filename="insulinplan.mksoftiplan";
                File file=new File(getExternalCacheDir(), filename);
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    byte[] bytes=iplan.getBytes();
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!file.exists()||!file.canRead()){
                  Toast.makeText(this, "Der Mailanhang konnte nicht erstellt werden", Toast.LENGTH_LONG).show();
                  return true;
                }
                Uri uri=Uri.parse("file://"+file.getAbsolutePath());
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(sharingIntent, "Insulinplan teilen"));
                return true;
            case R.id.menuLebensmittel:
                intent=new Intent(this, LebensmittelListe.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    private void setInsulinPlanList() {
        SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
        String ipString=sharedPref.getString(getString(R.string.INSULIN_PLAN), new JSONArray().toString());
        try{
            JSONArray jArr=new JSONArray(ipString);
            list.clear();
            for(int i=0;i<jArr.length(); i++){
                InsulinPlanEntry tmp=InsulinPlanEntry.fromJsonObject(jArr.getJSONObject(i));
                list.add(tmp);
            }

        } catch (JSONException e){}
    }

}
