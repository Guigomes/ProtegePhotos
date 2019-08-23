package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;


public class MainActivity extends AppCompatActivity {


    private PastaDAO pastaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pastaDAO = new PastaDAO(MainActivity.this);


        List<PastaVO> listaPastasVisiveis = pastaDAO.listarPastas(false);


        if (listaPastasVisiveis.isEmpty()) {

            List<PastaVO> listaPastasInvisiveis = pastaDAO.listarPastas(true);



            if(listaPastasInvisiveis.isEmpty()) {
                Intent it = new Intent(MainActivity.this,
                        EscolherPadraoActivity.class);


                startActivity(it);
                finish();
            }else{
                Intent it = new Intent(MainActivity.this,
                        ConfirmarPadraoActivity.class);


                startActivity(it);
                finish();
            }
        } else {
            startActivity(new Intent(MainActivity.this, EscolherPastaActivity.class));
            finish();
        }


    }



}
