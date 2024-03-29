/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package ggsoftware.com.br.protegephotospro.components.pattern;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ggsoftware.com.br.protegephotospro.R;

public class BasePatternActivity extends AppCompatActivity {

    private static final int CLEAR_PATTERN_DELAY_MILLI = 2000;

    protected TextView mMessageText;
    protected PatternView mPatternView;
    protected LinearLayout mButtonContainer;
    protected Button mLeftButton;
    protected Button mRightButton;

    private String nomePasta;
    private int idPasta;
    private boolean isPastaVisivel;

    private boolean isAlterarSenha;

    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pl_base_pattern_activity);
        mMessageText = (TextView) findViewById(R.id.pl_message_text);
        mPatternView = (PatternView) findViewById(R.id.pl_pattern);
        mButtonContainer = (LinearLayout) findViewById(R.id.pl_button_container);
        mLeftButton = (Button) findViewById(R.id.pl_left_button);
        mRightButton = (Button) findViewById(R.id.pl_right_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            this.idPasta = extras.getInt("idPasta");
            this.nomePasta =  extras.getString("nomePasta");
            this.isPastaVisivel =  extras.getBoolean("isPastaVisivel");
            this.isAlterarSenha = extras.getBoolean("isAlterarSenha");
        }else{
            this.nomePasta = getString(R.string.txt_pasta_principal);
            this.isPastaVisivel = true;
        }

    }

    protected void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }

    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }

    public String getNomePasta() {
        return nomePasta;
    }

    public void setNomePasta(String nomePasta) {
        this.nomePasta = nomePasta;
    }

    public int getIdPasta() {
        return idPasta;
    }

    public void setIdPasta(int idPasta) {
        this.idPasta = idPasta;
    }

    public boolean isPastaVisivel() {
        return isPastaVisivel;
    }

    public void setPastaVisivel(boolean pastaVisivel) {
        isPastaVisivel = pastaVisivel;
    }

    public boolean isAlterarSenha() {
        return isAlterarSenha;
    }

    public void setAlterarSenha(boolean alterarSenha) {
        isAlterarSenha = alterarSenha;
    }
}
