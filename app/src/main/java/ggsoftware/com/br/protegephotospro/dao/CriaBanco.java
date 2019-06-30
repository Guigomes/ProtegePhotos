package ggsoftware.com.br.protegephotospro.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by f3861879 on 15/08/17.
 */

public class CriaBanco  extends SQLiteOpenHelper {

    public static final String NOME_BANCO = "protegefotos.db";
    public static final String TABELA_IMAGEM = "imagens";
    public static final String TABELA_PASTA = "pasta";

    public static final String NOME_PASTA = "nome_pasta";

    public static final String TIMESTAMP_CRIACAO_PASTA = "timestamp_criacao_pasta";

    public static final String SENHA_PASTA = "senha_pasta";

    public static final String INVISIVEL = "invisivel";

    public static final String ID = "id";
    public static final String NOME = "nome";
    public static final String DIRETORIO = "diretorio";

    public static final int VERSAO = 1;


    public CriaBanco(Context context) {
        super(context, NOME_BANCO,null,VERSAO);    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABELA_IMAGEM+"("
                + ID + " integer primary key autoincrement,"
                + NOME + " text,"
                + DIRETORIO + " text"
                +")";


        String sql2 = "CREATE TABLE "+TABELA_PASTA+"("
                + ID + " integer primary key autoincrement,"
                + NOME_PASTA + " text,"
                + TIMESTAMP_CRIACAO_PASTA + " text,"
                + SENHA_PASTA + " text, "
                + INVISIVEL + " integer"
                +")";
        db.execSQL(sql);
        db.execSQL(sql2);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onCreate(db);

    }
}
