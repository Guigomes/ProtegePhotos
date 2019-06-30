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
        if (true) {
            List<PastaVO> pastasVisiveis = pastaDAO.listarPastas(false);
            List<PastaVO> pastas = pastaDAO.listarPastas(true);

            pastas.addAll(pastasVisiveis);
            int contPastasMesmasSenha = 0;
            List<PastaVO> pastasMesmaSenha = new ArrayList<>();
            for (PastaVO pasta :
                    pastas) {

                if (padrao.equals(pasta.getSenhaPasta())) {
                    contPastasMesmasSenha++;
                    pastasMesmaSenha.add(pasta);
                    ConfirmarPadraoActivity.pastaVO = pasta;

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
                    startActivity(it);
                    return true;
                } else {
                    return false;
                }
            }

        } else {
            String patternSha1 = ConfirmPatternActivity.pastaVO.getSenhaPasta();
            if (TextUtils.equals(PatternUtils.patternToSha1String(pattern), patternSha1)) {
                Intent it = new Intent(ConfirmarPadraoActivity.this, GaleriaActivity.class);

                finish();


                startActivity(it);
            } else {
                return false;
            }

        }
        return false;

    }


    @Override
    protected void onForgotPassword() {

        //startActivity(new Intent(this, YourResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }
}