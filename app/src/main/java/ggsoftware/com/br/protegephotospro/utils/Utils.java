package ggsoftware.com.br.protegephotospro.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public  static void toast(Context context, String mensagem){
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }
}
