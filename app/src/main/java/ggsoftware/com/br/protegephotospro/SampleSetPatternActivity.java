package ggsoftware.com.br.protegephotospro;

import android.content.Intent;

import java.util.List;

import me.zhanghai.android.patternlock.PatternUtils;
import me.zhanghai.android.patternlock.PatternView;
import me.zhanghai.android.patternlock.SetPatternActivity;

/**
 * Created by gomes on 31/07/17.
 */

public class SampleSetPatternActivity extends SetPatternActivity {

    @Override
    protected void onSetPattern(List<PatternView.Cell> pattern) {
        String patternSha1 = PatternUtils.patternToSha1String(pattern);

        int idPasta = SetPatternActivity.idPasta;
        String nomePasta = SetPatternActivity.nomePasta;

        Intent intent = new Intent();
        intent.putExtra("idPasta", idPasta);
        intent.putExtra("pattern", patternSha1);
        intent.putExtra("nomePasta", nomePasta);

        setResult(RESULT_OK, intent);
        finish();//finishing activity

    }
}