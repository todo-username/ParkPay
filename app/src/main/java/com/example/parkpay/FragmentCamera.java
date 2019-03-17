package com.example.parkpay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentCamera extends Fragment {
    TextView serial;
    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    IntentFilter mFilters[];
    Context c;
    String[][] mTechLists;
    private static final String TAG = "myLogs";
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_СARD ="Card";
    SharedPreferences settings;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera,container,false);
        c=getContext();
        settings= Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        scanQR(view,getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Запускаемм сканер штрих кода:
    public void scanBar(View v) {
        try {

            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(getActivity(), "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // Запуск сканера qr-кода:
    public static void scanQR(View v,Activity activity) {
        try {

            // Запускаем переход на com.google.zxing.client.android.SCAN с помощью intent:
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            activity.startActivityForResult(intent, 0);

        } catch (ActivityNotFoundException anfe) {

            // Предлагаем загрузить с Play Market:
            showDialog(activity, "Сканнер не найден", "Установить сканер с Play Market?", "Да", "Нет").show();
        }
    }

    // alert dialog для перехода к загрузке приложения сканера:
    private static AlertDialog showDialog(final Activity act, CharSequence title,
                                          CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

                // Ссылка поискового запроса для загрузки приложения:
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    // Обрабатываем результат, полученный от приложения сканера:
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                // Получаем данные после работы сканера и выводим их в Toast сообщении:
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(APP_PREFERENCES_СARD,contents);
                editor.apply();
               // numberCard.setText(contents);
//                Toast toast = Toast.makeText(this, "Содержание: " + contents + " Формат: " + format, Toast.LENGTH_LONG);
//                toast.show();

            }
        }
    }

}
