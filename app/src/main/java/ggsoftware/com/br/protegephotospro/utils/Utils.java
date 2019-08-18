package ggsoftware.com.br.protegephotospro.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import ggsoftware.com.br.protegephotospro.R;

public class Utils {

    public  static void toast(Context context, String mensagem){
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    public static void notificar(View view, String mensagem){


     Snackbar snackbar =    Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG)
                .setAction("Action", null);

        snackbar.setTextColor(Color.WHITE);

        snackbar.show();
    }
}
