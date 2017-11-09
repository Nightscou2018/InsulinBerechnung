package de.mkservices.insulinrechner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by manfr on 19.10.2017.
 */

public class InsulinPlanEntry {
    String name;
    float faktor;
    ArrayList<KorrekturWert> korrekturWerte;

    InsulinPlanEntry(){
        korrekturWerte=new ArrayList<>();
    }
    InsulinPlanEntry(String name, float faktor, JSONArray korrekturen){
        this.name=name;
        this.faktor=faktor;
        korrekturWerte=new ArrayList<>();
        for(int i=0;i<korrekturen.length(); i++){
            try {
                JSONObject jsonObj = korrekturen.getJSONObject(i);
                KorrekturWert tmp = new KorrekturWert();
                tmp.von=jsonObj.getInt("von");
                tmp.bis=jsonObj.getInt("bis");
                tmp.wert=(float)jsonObj.getDouble("wert");
                korrekturWerte.add(tmp);
            } catch(JSONException e){}
        }
    }

    JSONObject toJsonObject(){
        try {
            JSONObject tmp = new JSONObject();
            tmp.put("name", this.name);
            tmp.put("faktor", this.faktor);
            JSONArray jArr=new JSONArray();
            for(int i=0; i<korrekturWerte.size(); i++){
                JSONObject korrObj=new JSONObject();
                korrObj.put("von", korrekturWerte.get(i).von);
                korrObj.put("bis", korrekturWerte.get(i).bis);
                korrObj.put("wert", korrekturWerte.get(i).wert);
                jArr.put(korrObj);
            }
            tmp.put("korrektur", jArr);
            return tmp;
        } catch(JSONException e){}
        return new JSONObject();
    }

    static InsulinPlanEntry fromJsonObject(JSONObject jObj){
        try {
            String name = jObj.getString("name");
            float faktor = (float) jObj.getDouble("faktor");
            JSONArray korrekturen=jObj.getJSONArray("korrektur");
            InsulinPlanEntry ipe = new InsulinPlanEntry(name, faktor, korrekturen);
            return ipe;
        } catch(JSONException e){}
        return new InsulinPlanEntry();
    }

    @Override
    public String toString(){
        return this.name;
    }
}
