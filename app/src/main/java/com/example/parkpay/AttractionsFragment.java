package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttractionsFragment extends Fragment {

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_PARK_ID ="parkID";
    private static final String TAG = "myLogs";

    private RecyclerView rv;
    private ProgressBar progressBarAttractions;
    private SwipeRefreshLayout swipeAttr;
    AppCompatImageView searchFilter;

    private List<Attraction> attr;

    private SharedPreferences settings;
    private Context c;

    TextInputEditText nameAttr;
    TextInputEditText ageMin;
    TextInputEditText ageMax;
    TextInputEditText maxWeight;
    TextInputEditText minGrowth;
    AppCompatButton acceptButton;
    ConstraintLayout searchLayout;
    AppCompatTextView searchResult;

    String attrName;
    String minAge;
    String maxAge;
    String weightMax;
    String growthMax;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_attractions,container,false);

        if (container != null) {
            c = container.getContext();
        }

        //simpleList = (ListView)view.findViewById(R.id.parks);

        progressBarAttractions= view.findViewById(R.id.progressBarAttractions);
        rv = view.findViewById(R.id.attrs);
        swipeAttr = view.findViewById(R.id.swipeAttr);
        searchFilter = view.findViewById(R.id.searchFilter);

        nameAttr=view.findViewById(R.id.nameAttr);
        ageMin=view.findViewById(R.id.ageMin);
        ageMax=view.findViewById(R.id.ageMax);
        maxWeight=view.findViewById(R.id.maxWeight);
        minGrowth=view.findViewById(R.id.minGrowth);
        acceptButton=view.findViewById(R.id.acceptButton);
        searchLayout=view.findViewById(R.id.searchLayout);
        searchResult=view.findViewById(R.id.searchResult);

        searchLayout.setVisibility(View.INVISIBLE);
        searchResult.setVisibility(View.INVISIBLE);

        searchFilter.setOnClickListener(view1 -> {

            rv.setVisibility(View.INVISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
            searchResult.setVisibility(View.INVISIBLE);
        });

        acceptButton.setOnClickListener(v -> {

//            rv.setVisibility(View.INVISIBLE);
//            searchLayout.setVisibility(View.VISIBLE);
            progressBarAttractions.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.INVISIBLE);

            attrName=nameAttr.getText().toString();
            minAge=ageMin.getText().toString();
            maxAge=ageMax.getText().toString();
            weightMax=maxWeight.getText().toString();
            growthMax=minGrowth.getText().toString();

            getAttrSearch("https://api.mobile.goldinnfish.com/attr/list");
        });

        swipeAttr.setColorSchemeColors(Color.parseColor("#3F51B5"));

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        rv.setVisibility(View.INVISIBLE);
        progressBarAttractions.setVisibility(View.VISIBLE);

        c=getContext();


        LinearLayoutManager llm = new LinearLayoutManager(c);
        rv.setLayoutManager(llm);



        swipeAttr.setOnRefreshListener(() -> {

                attr = new ArrayList<>();

                getAttr("https://api.mobile.goldinnfish.com/attr/list");
        });

        getAttr("https://api.mobile.goldinnfish.com/attr/list");


        return view;
    }

    public void getAttr(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("park_id",settings.getString(APP_PREFERENCES_PARK_ID, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();

        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            BitmapDescriptor markerIcon;

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

                            attr = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("atr_id"));
                                Log.d(TAG, Jobject.getString("rep_name"));
                                Log.d(TAG, Jobject.getString("image"));
                                Log.d(TAG, Jobject.getString("price"));
                                Log.d(TAG, Jobject.getString("bonus"));
                                Log.d(TAG, Jobject.getString("text"));
                                Log.d(TAG, Jobject.getString("weight"));
                                Log.d(TAG, Jobject.getString("growth"));
                                Log.d(TAG, Jobject.getString("age_min"));
                                Log.d(TAG, Jobject.getString("age_max"));
                                Log.d(TAG, Jobject.getString("level_fear"));
                                Log.d(TAG, Jobject.getString("lat"));
                                Log.d(TAG, Jobject.getString("lng"));

                                String lat=Jobject.getString("lat");
                                String lng=Jobject.getString("lng");

                                if(lat.contains("null")){
                                    lat="0.0";
                                }
                                if(lng.contains("null")){
                                    lng="0.0";
                                }

                                attr.add(new Attraction(
                                        Jobject.getString("atr_id"),
                                        Jobject.getString("rep_name"),
                                        Jobject.getString("image"),
                                        Jobject.getString("price"),
                                        Jobject.getString("bonus"),
                                        Jobject.getString("text"),
                                        Jobject.getString("weight"),
                                        Jobject.getString("growth"),
                                        Jobject.getString("age_min"),
                                        Jobject.getString("age_max"),
                                        Jobject.getString("level_fear"),
                                        Float.parseFloat(lat),
                                        Float.parseFloat(lng)
                                ));

                            }

                            AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
                            rv.setAdapter(adapter);

                            rv.setVisibility(View.VISIBLE);
                            progressBarAttractions.setVisibility(View.INVISIBLE);

                            swipeAttr.setRefreshing(false);

                            searchResult.setVisibility(View.INVISIBLE);

                        } catch (IOException | JSONException e) {

                            Log.d("Ошибка " + e,TAG);
//                            Toast.makeText(c, "Ошибка " + e, Toast.LENGTH_SHORT).show();

                            searchResult.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }


    public void getAttrSearch(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("park_id",settings.getString(APP_PREFERENCES_PARK_ID, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();

        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            BitmapDescriptor markerIcon;

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

                            attr = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("atr_id"));
                                Log.d(TAG, Jobject.getString("rep_name"));
                                Log.d(TAG, Jobject.getString("image"));
                                Log.d(TAG, Jobject.getString("price"));
                                Log.d(TAG, Jobject.getString("bonus"));
                                Log.d(TAG, Jobject.getString("text"));
                                Log.d(TAG, Jobject.getString("weight"));
                                Log.d(TAG, Jobject.getString("growth"));
                                Log.d(TAG, Jobject.getString("age_min"));
                                Log.d(TAG, Jobject.getString("age_max"));
                                Log.d(TAG, Jobject.getString("level_fear"));
                                Log.d(TAG, Jobject.getString("lat"));
                                Log.d(TAG, Jobject.getString("lng"));

                                String lat=Jobject.getString("lat");
                                String lng=Jobject.getString("lng");

                                String minJ=Jobject.getString("age_min");
                                String maxJ=Jobject.getString("age_max");
                                String weightJ=Jobject.getString("weight");
                                String growthJ=Jobject.getString("growth");

                                if(minJ.contains("null")||minJ.equals("") || minJ.length() == 0){
                                    minJ="2";
                                }
                                if(maxJ.contains("null")||maxJ.equals("") || maxJ.length() == 0){
                                    maxJ="100";
                                }
                                if(weightJ.contains("null")||weightJ.equals("") || weightJ.length() == 0){
                                    weightJ="100";
                                }
                                if(growthJ.contains("null")||growthJ.equals("") || growthJ.length() == 0){
                                    growthJ="130";
                                }

                                if(minAge.equals("") || minAge.length() == 0){
                                    minAge="0";
                                }
                                if(maxAge.equals("") || maxAge.length() == 0){
                                    maxAge="100";
                                }
                                if(weightMax.equals("") || weightMax.length() == 0){
                                    weightMax="100";
                                }
                                if(growthMax.equals("") || growthMax.length() == 0){
                                    growthMax="130";
                                }

                                int min=Integer.parseInt(minJ);
                                int max=Integer.parseInt(maxJ);
                                int weight=Integer.parseInt(weightJ);
                                int growth=Integer.parseInt(growthJ);

                                int minE=Integer.parseInt(minAge);
                                int maxE=Integer.parseInt(maxAge);
                                int weightE=Integer.parseInt(weightMax);
                                int growthE=Integer.parseInt(growthMax);

                                if(lat.contains("null")){
                                    lat="0.0";
                                }
                                if(lng.contains("null")){
                                    lng="0.0";
                                }

                                if(
                                        Jobject.getString("rep_name").contains(attrName)&&
                                        min>=minE&&
                                        max<=maxE&&
                                        weight>=weightE&&
                                        growth<=growthE
                                ) {

                                    attr.add(new Attraction(
                                            Jobject.getString("atr_id"),
                                            Jobject.getString("rep_name"),
                                            Jobject.getString("image"),
                                            Jobject.getString("price"),
                                            Jobject.getString("bonus"),
                                            Jobject.getString("text"),
                                            Jobject.getString("weight"),
                                            Jobject.getString("growth"),
                                            Jobject.getString("age_min"),
                                            Jobject.getString("age_max"),
                                            Jobject.getString("level_fear"),
                                            Float.parseFloat(lat),
                                            Float.parseFloat(lng)
                                    ));
                                }

                                else {
                                    searchResult.setVisibility(View.VISIBLE);
                                }

                            }

                            AttractionsAdapter adapter = new AttractionsAdapter(c,attr);
                            rv.setAdapter(adapter);

                            searchLayout.setVisibility(View.INVISIBLE);
                            rv.setVisibility(View.VISIBLE);
                            progressBarAttractions.setVisibility(View.INVISIBLE);

                            swipeAttr.setRefreshing(false);

                        } catch (IOException | JSONException e) {

                            Log.d("Ошибка " + e,TAG);
//                            Toast.makeText(c, "Ошибка " + e, Toast.LENGTH_SHORT).show();

                            searchResult.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

}
