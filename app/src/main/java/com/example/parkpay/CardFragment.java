package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CardFragment extends Fragment {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_CARDS ="Cards";
    public static final String APP_PREFERENCES_NAMES_CARDS ="namesCards";
    public static final String APP_PREFERENCES_VIRTUAL_CARDS ="virtualCards";
    public static final String APP_PREFERENCES_TOKEN ="Token";
    public static final String APP_PREFERENCES_NAME ="Name";
    public static final String APP_PREFERENCES_NUMBER ="Number";
    public static final String APP_PREFERENCES_MAIL ="Email";
    public static final String APP_PREFERENCES_DATE_BIRTHDAY ="DateBirthday";
    public static final String APP_PREFERENCES_STATUS ="Status";
    private static final String TAG = "myLogs";

    ExpandableListView listView;
    ArrayList<ArrayList<String>> groups;
    ArrayList<String> child;
    ArrayList<String> children1;
    ArrayList<String> codes;
    ArrayList<String> children2;
    ExpListAdapter adapter;
    ImageView updateCard;

    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_card,container,false);

        if (container != null) {
            c = container.getContext();
        }
        // Находим наш list
        listView= (ExpandableListView)view.findViewById(R.id.exListView);
        updateCard=(ImageView)view.findViewById(R.id.updateCard);

        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Создаем набор данных для адаптера
        groups = new ArrayList<ArrayList<String>>();
        children1 = new ArrayList<String>();
        codes = new ArrayList<String>();
        child = new ArrayList<String>();
        children2 = new ArrayList<String>();

        boolean checkConnection=MainActivity.isOnline(c);

//        if(checkConnection) {

        doGetRequest();

        doGetProfileRequest();

//        }
//        else {
//            Toast.makeText(c, "Отсутствует интернет соединение!",
//                    Toast.LENGTH_SHORT).show();
//        }

        if(settings.contains(APP_PREFERENCES_NAMES_CARDS)){
            child=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
        }

        child.add("Новая карта");
        groups.add(child);

        if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
            children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
        }

        children2.add("Новая карта");
        groups.add(children2);
        //Создаем адаптер и передаем context и список с данными
        adapter = new ExpListAdapter(c, groups);
        listView.setAdapter(adapter);

        updateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = settings.edit();
                editor.remove(APP_PREFERENCES_NAMES_CARDS);
                editor.remove(APP_PREFERENCES_CARDS);
                editor.remove(APP_PREFERENCES_VIRTUAL_CARDS);
                editor.apply();

                children1.clear();
                children2.clear();

                Toast.makeText(c,"Обновление",Toast.LENGTH_SHORT).show();

                doGetRequest();

            }
        });

        return view;
    }

    public void doGetRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("card")
                .addPathSegment("list")
                .addQueryParameter("token", settings.getString(APP_PREFERENCES_TOKEN, ""))
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method("GET", null)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONArray jsonArray = new JSONArray(jsonData);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject Jobject = jsonArray.getJSONObject(i);

                                Log.d(TAG, Jobject.getString("card_id"));
                                Log.d(TAG, Jobject.getString("name"));
                                Log.d(TAG, Jobject.getString("code"));

                                children1.add(Jobject.getString("name"));
                                codes.add(Jobject.getString("code"));

                            }

                            MainActivity.saveArrayList(children1, APP_PREFERENCES_NAMES_CARDS, settings);
                            MainActivity.saveArrayList(codes, APP_PREFERENCES_CARDS, settings);

                            groups.clear();
                            if(settings.contains(APP_PREFERENCES_NAMES_CARDS)){
                                children1=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
                            }

                            children1.add("Новая карта");
                            groups.add(children1);

                            if(settings.contains(APP_PREFERENCES_VIRTUAL_CARDS)){
                                children2=MainActivity.getArrayList(APP_PREFERENCES_VIRTUAL_CARDS,settings);
                            }

                            children2.remove("Новая карта");
                            children2.add("Новая карта");
                            groups.add(children2);
                            //Создаем адаптер и передаем context и список с данными
                            adapter = new ExpListAdapter(c, groups);
                            listView.setAdapter(adapter);


                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

    public void doGetProfileRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("user")
                .addPathSegment("get_info")
                .addQueryParameter("token", settings.getString(APP_PREFERENCES_TOKEN, ""))
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .method("GET", null)
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject parentObject = new JSONObject(jsonData);
                            JSONObject Jobject = parentObject.getJSONObject("user");

                            Log.d(TAG, Jobject.getString("name"));
                            Log.d(TAG, Jobject.getString("email"));
                            Log.d(TAG, Jobject.getString("phone"));
                            Log.d(TAG, Jobject.getString("birthday"));

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(APP_PREFERENCES_NAME, Jobject.getString("name"));
                            editor.putString(APP_PREFERENCES_MAIL, Jobject.getString("email"));
                            editor.putString(APP_PREFERENCES_NUMBER, Jobject.getString("phone"));
                            editor.putString(APP_PREFERENCES_DATE_BIRTHDAY, Jobject.getString("birthday"));
                            editor.apply();

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}
