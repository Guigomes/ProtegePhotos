package ggsoftware.com.br.protegephotospro.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ggsoftware.com.br.protegephotospro.R;

public class Utils {


    public static void notificar(View view, String mensagem){


     Snackbar snackbar =    Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG)
                .setAction("Action", null);

        snackbar.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public static boolean writeFileExternalStorage(Context context, File internalFile) {

        String aplicationName = context.getString(R.string.app_name);

        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File picsDir = new File(dcimDir, aplicationName);
        picsDir.mkdirs(); //make if not exist
        File newFile = new File(picsDir, internalFile.getName());
        OutputStream outputStream;
        try {
            InputStream in = new FileInputStream(internalFile);

            outputStream = new FileOutputStream(newFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();

            outputStream.flush();
            outputStream.close();


return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
