package de.mkservices.insulinrechner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class InsulinPlanListe extends AppCompatActivity {
    private final int NEW_PLAN_ENTRY=100;
    private ArrayList<InsulinPlanEntry> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insulin_plan_liste);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        list=new ArrayList<>();

        Button newListEntry = (Button) findViewById(R.id.btnNewInsulinplanItem);
        newListEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPrefs.edit();

                JSONArray jarr = new JSONArray();
                String jarrStr = jarr.toString();

                edit.putString(getString(R.string.TMP_KEY), jarrStr);
                edit.apply();
                Intent intent = new Intent(getApplicationContext(), NewInsulinPlanEntry.class);
                startActivityForResult(intent, NEW_PLAN_ENTRY);
            }
        });

        Button delBtn=(Button)findViewById(R.id.btnDelInsulinPlan);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray jArr=new JSONArray();
                SharedPreferences sharedPref=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit=sharedPref.edit();
                edit.putString(getString(R.string.INSULIN_PLAN), jArr.toString());
                edit.apply();
                //update Liste
                list.clear();
                ListView lv=(ListView)findViewById(R.id.lstPlanListe);
                ArrayAdapter adapter= (ArrayAdapter) lv.getAdapter();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setInsulinPlanList();
        final ListView lv = (ListView) findViewById(R.id.lstPlanListe);
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PLAN_ENTRY && resultCode == RESULT_OK) {
            //Refresh Listview
            SharedPreferences sharedPrefs=getApplicationContext().getSharedPreferences(getString(R.string.PREF_FILE), Context.MODE_PRIVATE);
            String tmp=sharedPrefs.getString(getString(R.string.INSULIN_PLAN),"[]");
            Toast.makeText(getApplicationContext(), tmp, Toast.LENGTH_LONG).show();
        }
    }
}