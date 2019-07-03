package ggsoftware.com.br.protegephotospro.activitys;

import android.content.Intent;
import android.text.TextUtils;

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

            List<PastaVO> pastas = pastaDAO.listarPastas();

            int contPastasMesmasSenha = 0;
            List<PastaVO> pastasMesmaSenha = new ArrayList<>();
            for (PastaVO pasta :
                    pastas) {

                if (padrao.equals(pasta.getSenhaPasta())) {
                    contPastasMesmasSenha++;
                    pastasMesmaSenha.add(pasta);


                }

            }
            if (contPastasMesmasSenha >= 2) {
                Intent it = new Intent(ConfirmarPadraoActivity.this, EscolherPastaActivity.class);
                it.putExtra("isEmpate", true);
                it.putExtra("padrao", padrao);
                startActivity(it);
                return true;
            } else {
                if (contPastasMesmasSenha == 1) {
                    Intent it = new Intent(ConfirmarPadraoActivity.this, GaleriaActivity.class);
                    it.putExtra("nomePasta", getNomePasta());
                    startActivity(it);
                    return true;
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