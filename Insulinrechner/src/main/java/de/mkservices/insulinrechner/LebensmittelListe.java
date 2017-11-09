package de.mkservices.insulinrechner;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LebensmittelListe extends AppCompatActivity {
    private final ArrayList<String> stringListe=new ArrayList<>();
    private final ArrayList<Lebensmitteleintrag> lebensmittelListe=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lebensmittel_liste);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        loadResFile();
        AutoCompleteTextView acv=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, stringListe);
        acv.setAdapter(adapter);
        acv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AutoCompleteTextView a=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
                String txt=String.valueOf(a.getText());
                for(int j=0; j<lebensmittelListe.size(); j++){
                    if(lebensmittelListe.get(j).name.equalsIgnoreCase(txt)){
                        TextView tv=(TextView)findViewById(R.id.txtCodeDisplay);
                        tv.setText(lebensmittelListe.get(j).id);
                        break;
                    }
                }
            }
        });
        ImageButton id=(ImageButton)findViewById(R.id.delField);
        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView a=(AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
                a.setText("");
                TextView tv=(TextView)findViewById(R.id.txtCodeDisplay);
                tv.setText("");
            }
        });
    }

    private void loadResFile(){
        stringListe.clear();
        lebensmittelListe.clear();
        InputStream is=this.getResources().openRawResource(R.raw.liste);
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        StringBuilder sb=new StringBuilder();
        String readline=null;
        try {
            while((readline=br.readLine())!=null){
                sb.append(readline);
            }
        } catch(IOException e){}

        try{
            JSONArray jArr=new JSONArray(sb.toString());
            for(int i=0;i<jArr.length();i++){
                JSONObject jObj=jArr.getJSONObject(i);
                Lebensmitteleintrag le=new Lebensmitteleintrag();
                le.id=jObj.getString("id");
                le.name=jObj.getString("text");
                lebensmittelListe.add(le);
                stringListe.add(jObj.getString("text"));
            }
        } catch(JSONException e){}
    }
}
