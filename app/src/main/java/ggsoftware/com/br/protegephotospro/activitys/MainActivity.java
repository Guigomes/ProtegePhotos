package ggsoftware.com.br.protegephotospro.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;
import ggsoftware.com.br.protegephotospro.utils.Utils;


public class MainActivity extends AppCompatActivity {


    private PastaDAO pastaDAO;
private LinearLayout container;

private boolean isNovaPastaVisivel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isNovaPastaVisivel = true;
         ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        pastaDAO = new PastaDAO(MainActivity.this);


        List<PastaVO> listaPastasVisiveis = pastaDAO.listarPastas(false);

        if (listaPastasVisiveis.isEmpty()) {

            List<PastaVO> listaPastasInvisiveis = pastaDAO.listarPastas(true);



            if(listaPastasInvisiveis.isEmpty()) {

                /*
                Intent it = new Intent(MainActivity.this,
                        EscolherPadraoActivity.class);


                startActivity(it);
                finish();*/
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

public  void criarNovaPasta(View v){
    final androidx.appcompat.app.AlertDialog dialog;

    final View alertDialogView = LayoutInflater.from(MainActivity.this).inflate
            (R.layout.dialog_nova_pasta, null);

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this)
            .setTitle(getString(R.string.title_criar_nova_pasta))
            .setView(alertDialogView)
            .setPositiveButton(R.string.btn_criar_pasta, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
              String      nomePasta = ((EditText) alertDialogView.findViewById(R.id.edtNomeNovaPasta)).getText().toString();


                    if (nomePasta != null && !nomePasta.isEmpty()) {
                        Intent it = new Intent(MainActivity.this, EscolherPadraoActivity.class);

                        it.putExtra("idPasta", -1);
                        it.putExtra("nomePasta", nomePasta);
                        it.putExtra("isPastaVisivel", isNovaPastaVisivel);


                        startActivity(it);
                        finish();
                        dialog.dismiss();
                    } else {


                        Toast.makeText(getApplicationContext(), getString( R.string.msg_erro_criar_pasta_sem_nome) ,Toast.LENGTH_SHORT).show();



                    }
                }
            })
            .setNeutralButton(R.string.btn_cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            })
            .setCancelable(true);

    dialog = builder.create();

    dialog.show();


}
    public void abrirDialogInfoInvisivel(View v) {
        final androidx.appcompat.app.AlertDialog dialog;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle(getString(R.string.title_criar_nova_pasta_informacoes))
                .setMessage(getString(R.string.txt_pasta_invisivel))
                .setPositiveButton(R.string.btn_entendi, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                })

                .setCancelable(true);

        dialog = builder.create();

        dialog.show();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdb_visivel:
                if (checked)
                    isNovaPastaVisivel = true;

                break;
            case R.id.rdb_invisivel:
                if (checked)
                    isNovaPastaVisivel = false;

                break;
        }
    }

}
