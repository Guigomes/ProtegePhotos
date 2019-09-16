package ggsoftware.com.br.protegephotospro.activitys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;
import ggsoftware.com.br.protegephotospro.components.pattern.ConfirmPatternActivity;
import ggsoftware.com.br.protegephotospro.utils.Utils;

import static android.graphics.Color.parseColor;

public class EscolherPastaActivity extends AppCompatActivity {

    private String nomePasta;

    PastaDAO pastaDAO;
    boolean isPastaVisivel = true;
    ListView listaPastas;
    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final androidx.appcompat.app.AlertDialog dialog;

            final View alertDialogView = LayoutInflater.from(EscolherPastaActivity.this).inflate
                    (R.layout.dialog_nova_pasta, null);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EscolherPastaActivity.this)
                    .setTitle(getString(R.string.title_criar_nova_pasta))
                   .setView(alertDialogView)
                    .setPositiveButton(R.string.btn_criar_pasta, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nomePasta = ((EditText) alertDialogView.findViewById(R.id.edtNomeNovaPasta)).getText().toString();


                            if (nomePasta != null && !nomePasta.isEmpty()) {
                                Intent it = new Intent(EscolherPastaActivity.this, EscolherPadraoActivity.class);

                                it.putExtra("idPasta", -1);
                                it.putExtra("nomePasta", nomePasta);
                                it.putExtra("isPastaVisivel", isPastaVisivel);


                                startActivity(it);
                                dialog.dismiss();
                            } else {

                                Utils.notificar(listaPastas,getString( R.string.msg_erro_criar_pasta_sem_nome) );


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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_pasta);
         final List<String> nomePastas = new ArrayList();




        FloatingActionButton fab =  findViewById(R.id.fab);

        fab.setOnClickListener(fabListener);

        pastaDAO = new PastaDAO(EscolherPastaActivity.this);

        Intent it = getIntent();
         listaPastas = (ListView) findViewById(R.id.lista_pastas);

        if (it.getExtras() != null && it.getExtras().get("isEmpate") != null) {

            setTitle(getString(R.string.msg_pastas_iguais));
            List<PastaVO> pastas = pastaDAO.listarPastas(true);

            String nomePastaSelecionada = it.getExtras().getString("nomePasta");
            PastaVO pastaSelecionada = pastaDAO.buscarPorNome(nomePastaSelecionada);

            String padrao = (String) it.getExtras().get("padrao");

            if (padrao.equals(pastaSelecionada.getSenhaPasta())) {
                nomePastas.add(pastaSelecionada.getNomePasta());

            }


            for (PastaVO pasta :
                    pastas) {

                if (padrao.equals(pasta.getSenhaPasta()) && !pasta.getNomePasta().equalsIgnoreCase(pastaSelecionada.getNomePasta())) {
                    nomePastas.add(pasta.getNomePasta());

                }

            }



            listaPastas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String nomePastaEscolhida = nomePastas.get(position);

                    Intent it = new Intent(EscolherPastaActivity.this, GaleriaActivity.class);
                    it.putExtra("nomePasta", nomePastaEscolhida);
                    startActivity(it);
                    finish();

                }
            });
            listaPastas.setAdapter(new ArrayAdapter<String>(
                    this, R.layout.item_list,
                    R.id.Itemname, nomePastas));
        }




    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getExtras() == null || getIntent().getExtras().get("isEmpate") == null) {


            final List<String> nomePastas = new ArrayList();

            List<PastaVO> pastas = pastaDAO.listarPastas(false);


            for (PastaVO pasta : pastas) {
                nomePastas.add(pasta.getNomePasta());
            }

            listaPastas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String nomePastaEscolhida = nomePastas.get(position);

                    Intent it = new Intent(EscolherPastaActivity.this, ConfirmarPadraoActivity.class);
                    it.putExtra("nomePasta", nomePastaEscolhida);
                    startActivity(it);
                }
            });


            listaPastas.setAdapter(new ArrayAdapter<String>(
                    this, R.layout.item_list,
                    R.id.Itemname, nomePastas));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }



    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.rdb_visivel:
                if (checked)
                    isPastaVisivel = true;

                break;
            case R.id.rdb_invisivel:
                if (checked)
                    isPastaVisivel = false;

                break;
        }
    }

    public void abrirDialogInfoInvisivel(View v){
        final androidx.appcompat.app.AlertDialog dialog;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EscolherPastaActivity.this)
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
}
