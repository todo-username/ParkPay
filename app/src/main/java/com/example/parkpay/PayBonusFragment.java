package com.example.parkpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ru.yandex.money.android.sdk.Amount;
import ru.yandex.money.android.sdk.Checkout;
import ru.yandex.money.android.sdk.ColorScheme;
import ru.yandex.money.android.sdk.MockConfiguration;
import ru.yandex.money.android.sdk.PaymentParameters;
import ru.yandex.money.android.sdk.TestParameters;
import ru.yandex.money.android.sdk.TokenizationResult;
import ru.yandex.money.android.sdk.UiParameters;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class PayBonusFragment extends Fragment {

    private EditText amountPay;

    private String sum;

    private Context c;

    private static final String TAG = "myLogs";

    private static final int REQUEST_CODE_TOKENIZE = 33;

    private SharedPreferences settings;

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TOKEN ="Token";
    private static final String APP_PREFERENCES_CARD_DELETE ="cardDelete";
    private static final String APP_PREFERENCES_CARD_CODE ="cardCode";
    private static final String APP_PREFERENCES_CARD_NAME ="cardName";
    private static final String APP_PREFERENCES_STATUS ="Status";
    private static final String APP_PREFERENCES_MSG ="Message";

    private TokenizationResult result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pay_bonus, container, false);

        if (container != null) {
            c = container.getContext();
        }

        amountPay = view.findViewById(R.id.amountPay);
        AppCompatButton buttonPay = view.findViewById(R.id.buttonPay);
        AppCompatTextView titlePay = view.findViewById(R.id.titlePay);
        AppCompatTextView numberPay = view.findViewById(R.id.numberPay);
        AppCompatImageView imPay = view.findViewById(R.id.imPay);


        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Glide.with(c)
                .load(R.drawable.pay)
                .thumbnail(0.5f)
                .dontAnimate()
                .into(imPay);

        String name = settings.getString(APP_PREFERENCES_CARD_NAME, "") + "";
        String number = settings.getString(APP_PREFERENCES_CARD_CODE, "") + "";

//        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("____.__");
//        MaskImpl mask = MaskImpl.createTerminated(slots);
//        mask.setForbidInputWhenFilled(true);
//        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
//        formatWatcher.installOn(amountPay);

        titlePay.setText(name);
        numberPay.setText(number);

        buttonPay.setOnClickListener(v -> {

            if (amountPay.getText().toString().equals("") || amountPay.getText().length() == 0) {
                Toast.makeText(c, "Заполните все поля ввода!",
                        Toast.LENGTH_SHORT).show();
            } else {
                sum = amountPay.getText().toString();

                payBonus("https://api.mobile.goldinnfish.com/card/add_bonus");
            }
        });

        return view;
    }


    private void payBonus(String url){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("card_id",settings.getString(APP_PREFERENCES_CARD_DELETE,""));
            json.put("value",sum);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonString = json.toString();
        Log.d(TAG,json.toString());
        RequestBody body = RequestBody.create(JSON, jsonString);
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .post(body)
                .addHeader("Authorization","Bearer "+
                        Objects.requireNonNull(settings.getString(APP_PREFERENCES_TOKEN, "")))
                .url(url)
                .build();
        Log.d(TAG,request.toString());
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                if(call.request().body()!=null)
                {
                    Log.d(TAG, Objects.requireNonNull(call.request().body()).toString());
                }

                if (getActivity() != null) {
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {

                            String jsonData = null;
                            if (response.body() != null) {
                                jsonData = response.body().string();
                            }

                            JSONObject Jobject = new JSONObject(jsonData);

                            Log.d(TAG, Jobject.getString("status"));
                            Log.d(TAG, Jobject.getString("msg"));

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(APP_PREFERENCES_STATUS, Jobject.getString("status"));
                            editor.apply();

                            if(settings.contains(APP_PREFERENCES_STATUS)){
                                if(Objects.equals(settings.getString(APP_PREFERENCES_STATUS, ""), "1")){

                                    Toast.makeText(c,"Успешно!",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(c,
                                            MainActivity.class);
                                    startActivity(intent);
                                }
                                else {

                                    Toast
                                            .makeText(c,Jobject.getString("msg"),Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }

                        } catch (IOException | JSONException e) {
                            Log.d(TAG, "Ошибка " + e);
                        }
                    });
                }
            }
        });
    }

}