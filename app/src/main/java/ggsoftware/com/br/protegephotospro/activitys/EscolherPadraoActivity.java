package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import ggsoftware.com.br.protegephotospro.R;
import ggsoftware.com.br.protegephotospro.components.pattern.PatternUtils;
import ggsoftware.com.br.protegephotospro.components.pattern.PatternView;
import ggsoftware.com.br.protegephotospro.components.pattern.SetPatternActivity;
import ggsoftware.com.br.protegephotospro.dao.PastaDAO;

/**
 * Created by gomes on 31/07/17.
 */

public class EscolherPadraoActivity extends SetPatternActivity {

    PastaDAO pastaDAO;

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);

        Bundle extras = getIntent().getExtras();


        if (getIdPasta() == 0) {
            criarNovaPasta(patternSha1);
        }else{

        }


    }

    private void criarNovaPasta(String pattern) {


        String nomePasta = getNomePasta();

        if (nomePasta == null || nomePasta.isEmpty()) {
            nomePasta = getString(R.string.txt_pasta_principal);
        }

        pastaDAO = new PastaDAO(EscolherPadraoActivity.this);
        boolean sucesso = pastaDAO.salvarPasta(nomePasta, pattern, isPastaVisivel());

        if (sucesso) {
            Toast.makeText(EscolherPadraoActivity.this, getString(R.string.msg_sucesso_criar_pasta), Toast.LENGTH_SHORT).show();

            Intent it = new Intent(EscolherPadraoActivity.this, GaleriaActivity.class);
            it.putExtra("nomePasta", nomePasta);

            finish();
            startActivity(it);

        } else {
            Toast.makeText(EscolherPadraoActivity.this, getString(R.string.msg_erro_criar_pasta), Toast.LENGTH_SHORT).show();
        }


    }
    private void alterarSenhaPasta(int idPasta, String pattern, String nomePasta) {


    }
}