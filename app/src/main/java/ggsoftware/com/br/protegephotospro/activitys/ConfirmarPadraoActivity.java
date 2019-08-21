package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ggsoftware.com.br.protegephotospro.dao.PastaDAO;
import ggsoftware.com.br.protegephotospro.dao.PastaVO;
import ggsoftware.com.br.protegephotospro.components.pattern.ConfirmPatternActivity;
import ggsoftware.com.br.protegephotospro.components.pattern.PatternUtils;
import ggsoftware.com.br.protegephotospro.components.pattern.PatternView;

/**
 * Created by gomes on 31/07/17.
 */

public class ConfirmarPadraoActivity extends ConfirmPatternActivity {

    PastaDAO pastaDAO;

    @Override
    protected boolean isStealthModeEnabled() {
        // TODO: Return the value from SharedPreferences.
        return false;
    }

    @Override
    protected boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        String padrao = PatternUtils.patternToSha1String(pattern);

        pastaDAO = new PastaDAO(ConfirmarPadraoActivity.this);


        List<PastaVO> pastasInvisiveis = pastaDAO.listarPastas(true);

        PastaVO pastaSelecionada = null;

        if(getNomePasta() != null) {
          pastaSelecionada = pastaDAO.buscarPorNome(getNomePasta());
        }
        int contPastasMesmasSenha = 0;

        List<PastaVO> pastasMesmaSenha = new ArrayList<>();

        if(pastaSelecionada != null && pastaSelecionada.getSenhaPasta().equalsIgnoreCase(padrao)){

            pastasMesmaSenha.add(pastaSelecionada);
            contPastasMesmasSenha++;

        }

        for (PastaVO pasta :
                pastasInvisiveis) {
            if (padrao.equals(pasta.getSenhaPasta())) {

                contPastasMesmasSenha++;
                pastasMesmaSenha.add(pasta);


            }

        }
        if (contPastasMesmasSenha >= 2) {
            Intent it = new Intent(ConfirmarPadraoActivity.this, EscolherPastaActivity.class);
            it.putExtra("isEmpate", true);
            it.putExtra("nomePasta", pastaSelecionada.getNomePasta());
            it.putExtra("padrao", padrao);

            startActivity(it);
            return true;
        } else {
            if (contPastasMesmasSenha == 1) {
                if (pastaSelecionada != null && pastaSelecionada.getNomePasta().equalsIgnoreCase(pastasMesmaSenha.get(0).getNomePasta()) ) {
                    Intent it = new Intent(ConfirmarPadraoActivity.this, GaleriaActivity.class);
                    it.putExtra("nomePasta", pastaSelecionada.getNomePasta());
                    startActivity(it);
                    return true;
                }else if(pastasMesmaSenha.get(0).getInvisivel() == 1){
                    Intent it = new Intent(ConfirmarPadraoActivity.this, GaleriaActivity.class);
                    it.putExtra("nomePasta", pastasMesmaSenha.get(0).getNomePasta());
                    startActivity(it);
                    return true;
                }
                else {
                    return false;
                }
            } else {
                return false;
            }
        }


    }


    @Override
    protected void onForgotPassword() {

        //startActivity(new Intent(this, YourResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }
}