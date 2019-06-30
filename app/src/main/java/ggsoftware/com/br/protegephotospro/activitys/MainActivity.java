package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.widget.Toast;

import java.util.List;

import ggsoftware.com.br.protegephotospro.Constantes;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;


public class MainActivity extends AppCompatActivity {


    static SharedPreferences sharedPreferences;
    private PastaDAO pastaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        pastaDAO = new PastaDAO(MainActivity.this);


        Toast.makeText(getApplicationContext(), "MAIN", Toast.LENGTH_SHORT).show();


        List<PastaVO> listaPastas = pastaDAO.listarPastas(false);


        if (listaPastas.isEmpty()) {

            Intent it = new Intent(MainActivity.this,
                    EscolherPadraoActivity.class);

            int idPasta = 0;

            it.putExtra("idPasta", idPasta);


            startActivity(it);
        } else {
            startActivity(new Intent(MainActivity.this, EscolherPastaActivity.class));
            finish();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

       if (resultCode == RESULT_OK && requestCode == Constantes.CONFERIR_SENHA) {


            Intent it = new Intent(MainActivity.this, GaleriaActivity.class);
            startActivity(it);


        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }


}
