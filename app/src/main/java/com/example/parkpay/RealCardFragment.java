package com.example.parkpay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RealCardFragment extends Fragment {

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
    public static final String APP_PREFERENCES_MONEY_CHILD ="moneyChild";
    public static final String APP_PREFERENCES_BONUS_CHILD ="bonusChild";
    public static final String APP_PREFERENCES_QUANTITY_VISITS ="quantityVisits";
    public static final String APP_PREFERENCES_CARD_ID ="cardId";
    private static final String TAG = "myLogs";

    ArrayList<String> child;
    ArrayList<String> children2;
    ArrayList<String> moneyChild;
    ArrayList<String> bonusChild;
    ArrayList<String> idCard;

    ArrayList<String> children1;
    ArrayList<String> codes;
    ArrayList<String> money;
    ArrayList<String> bonus;
    ArrayList<String> cardId;

    ListView simpleList;
    CardAdapter cardAdapter;

    SharedPreferences settings;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_real_card,container,false);

        if (container != null) {
            c = container.getContext();
        }

        simpleList = (ListView)view.findViewById(R.id.realCard);


        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //Создаем набор данных для адаптера
        child = new ArrayList<String>();
        children2 = new ArrayList<String>();
        moneyChild = new ArrayList<String>();
        bonusChild = new ArrayList<String>();
        idCard = new ArrayList<String>();

        children1 = new ArrayList<String>();
        codes = new ArrayList<String>();
        money = new ArrayList<String>();
        bonus = new ArrayList<String>();
        cardId = new ArrayList<String>();


        simpleList.invalidateViews();

        StrictMode.ThreadPolicy mypolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(mypolicy);

        boolean checkConnection=MainActivity.isOnline(c);

//        if(checkConnection) {

        doGetRequest();

        doGetProfileRequest();

        getVisits();

//        }
//        else {
//            Toast.makeText(c, "Отсутствует интернет соединение!",
//                    Toast.LENGTH_SHORT).show();
//        }

        if(settings.contains(APP_PREFERENCES_NAMES_CARDS)){

            child=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
        }

        if(settings.contains(APP_PREFERENCES_CARDS)){

            children2=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
        }

        if(settings.contains(APP_PREFERENCES_MONEY_CHILD)){

            moneyChild=MainActivity.getArrayList(APP_PREFERENCES_MONEY_CHILD,settings);
        }

        if(settings.contains(APP_PREFERENCES_BONUS_CHILD)){

            bonusChild=MainActivity.getArrayList(APP_PREFERENCES_BONUS_CHILD,settings);
        }

        if(settings.contains(APP_PREFERENCES_CARD_ID)){

            idCard=MainActivity.getArrayList(APP_PREFERENCES_CARD_ID,settings);
        }

        child.add("Новая карта");
        children2.add("Новая карта");
        moneyChild.add("Новая карта");
        bonusChild.add("Новая карта");
        idCard.add("Новая карта");

        child.add("Новая карта");
        children2.add("Новая карта");
        moneyChild.add("Новая карта");
        bonusChild.add("Новая карта");
        idCard.add("Новая карта");

        child.add("Новая карта");
        children2.add("Новая карта");
        moneyChild.add("Новая карта");
        bonusChild.add("Новая карта");
        idCard.add("Новая карта");

        //if(child) {
        cardAdapter = new CardAdapter(c, child, children2,moneyChild,bonusChild,idCard);
        simpleList.setAdapter(cardAdapter);
        //}
        //else{
        //    simpleList.setVisibility(View.INVISIBLE);
        //}

        return view;
    }

    public void doGetRequest(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("card")
                .addPathSegment("list")
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
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
                                Log.d(TAG, Jobject.getString("balance_money"));
                                Log.d(TAG, Jobject.getString("balance_bonus"));

                                children1.add(Jobject.getString("name"));
                                codes.add(Jobject.getString("code"));
                                money.add(Jobject.getString("balance_money"));
                                bonus.add(Jobject.getString("balance_bonus"));
                                cardId.add(Jobject.getString("card_id"));

                            }

                            MainActivity.saveArrayList(children1, APP_PREFERENCES_NAMES_CARDS, settings);
                            MainActivity.saveArrayList(codes, APP_PREFERENCES_CARDS, settings);
                            MainActivity.saveArrayList(money, APP_PREFERENCES_MONEY_CHILD, settings);
                            MainActivity.saveArrayList(bonus, APP_PREFERENCES_BONUS_CHILD, settings);
                            MainActivity.saveArrayList(cardId, APP_PREFERENCES_CARD_ID, settings);

                            children1.clear();
                            codes.clear();
                            money.clear();
                            bonus.clear();
                            cardId.clear();



                            if(settings.contains(APP_PREFERENCES_NAMES_CARDS)){
                                children1=MainActivity.getArrayList(APP_PREFERENCES_NAMES_CARDS,settings);
                            }

                            if(settings.contains(APP_PREFERENCES_CARDS)){
                                codes=MainActivity.getArrayList(APP_PREFERENCES_CARDS,settings);
                            }

                            if(settings.contains(APP_PREFERENCES_MONEY_CHILD)){
                                money=MainActivity.getArrayList(APP_PREFERENCES_MONEY_CHILD,settings);
                            }

                            if(settings.contains(APP_PREFERENCES_BONUS_CHILD)){
                                bonus=MainActivity.getArrayList(APP_PREFERENCES_BONUS_CHILD,settings);
                            }

                            if(settings.contains(APP_PREFERENCES_CARD_ID)){
                                cardId=MainActivity.getArrayList(APP_PREFERENCES_CARD_ID,settings);
                            }

                            children1.add("Новая карта");
                            codes.add("Новая карта");
                            money.add("Новая карта");
                            bonus.add("Новая карта");
                            cardId.add("Новая карта");

                            children1.add("Новая карта");
                            codes.add("Новая карта");
                            money.add("Новая карта");
                            bonus.add("Новая карта");
                            cardId.add("Новая карта");

                            children1.add("Новая карта");
                            codes.add("Новая карта");
                            money.add("Новая карта");
                            bonus.add("Новая карта");
                            cardId.add("Новая карта");


                            cardAdapter = new CardAdapter(c, children1,codes,money,bonus,cardId);
                            simpleList.setAdapter(cardAdapter);


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
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
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

    public void getVisits(){

        OkHttpClient client = new OkHttpClient();

        HttpUrl mySearchUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("192.168.252.199")
                .addPathSegment("user")
                .addPathSegment("visits")
                .build();

        Log.d(TAG,mySearchUrl.toString());

        final Request request = new Request.Builder()
                .url(mySearchUrl)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
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
                    getActivity().runOnUiThread(() -> {
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

                            //JSONObject Jobject = new JSONObject(jsonData);

                            Log.d(TAG,jsonData);
                            if (jsonData != null) {
                                if (!jsonData.contains("<"))
                                {
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString(APP_PREFERENCES_QUANTITY_VISITS,jsonData);
                                    editor.apply();
                                }
                            }


                        } catch (IOException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}
