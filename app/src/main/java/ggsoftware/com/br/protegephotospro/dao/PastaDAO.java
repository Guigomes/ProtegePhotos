package ggsoftware.com.br.protegephotospro.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ggsoftware.com.br.protegephotospro.dao.CriaBanco.TABELA_PASTA;

/**
 * Created by f3861879 on 15/08/17.
 */

public class PastaDAO {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public PastaDAO(Context context) {
        banco = new CriaBanco(context);
    }

    public boolean salvarPasta(String nomePasta, String senhaPasta, boolean isPastaVisivel) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.NOME_PASTA, nomePasta);
        valores.put(CriaBanco.TIMESTAMP_CRIACAO_PASTA, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        valores.put(CriaBanco.SENHA_PASTA, senhaPasta);


        valores.put(CriaBanco.INVISIVEL, isPastaVisivel ? 0 : 1);


        resultado = db.insert(TABELA_PASTA, null, valores);
        db.close();

        if (resultado == -1)
            return false;
        else
            return true;

    }

    public boolean updatePasta(PastaVO pastaVO) {
        ContentValues valores;
        long resultado;

        db = banco.getWritableDatabase();
        valores = new ContentValues();
        valores.put(CriaBanco.ID, pastaVO.getId());
        valores.put(CriaBanco.NOME_PASTA, pastaVO.getNomePasta());
        valores.put(CriaBanco.TIMESTAMP_CRIACAO_PASTA, pastaVO.getTimestampCriacaoPasta());
        valores.put(CriaBanco.SENHA_PASTA, pastaVO.getSenhaPasta());
        valores.put(CriaBanco.INVISIVEL, pastaVO.getInvisivel());

        String where = banco.ID + " = ?";

        String[] argumentos = {String.valueOf(pastaVO.getId())};

        resultado = db.update(TABELA_PASTA, valores, where, argumentos);

        db.close();

        if (resultado == -1)
            return false;
        else
            return true;

    }

    public List<PastaVO> listarPastas(boolean escondidas) {


        List<PastaVO> listaPastas = new ArrayList<>();
        Cursor rs;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};
        String where = banco.INVISIVEL + " = ?";

        int invisivel = escondidas ? 1 : 0;


        String[] argumentos = {String.valueOf(invisivel)};

        db = banco.getReadableDatabase();
        rs = db.query(TABELA_PASTA, campos, where, argumentos, null, null, null, null);

        while (rs.moveToNext()) {
            PastaVO pastaVO = new PastaVO();
            pastaVO.setId(rs.getInt(rs.getColumnIndex(CriaBanco.ID)));
            pastaVO.setNomePasta(rs.getString(rs.getColumnIndex(CriaBanco.NOME_PASTA)));
            pastaVO.setTimestampCriacaoPasta(rs.getString(rs.getColumnIndex(CriaBanco.TIMESTAMP_CRIACAO_PASTA)));
            pastaVO.setSenhaPasta(rs.getString(rs.getColumnIndex(CriaBanco.SENHA_PASTA)));
            pastaVO.setInvisivel(rs.getInt(rs.getColumnIndex(CriaBanco.INVISIVEL)));

            listaPastas.add(pastaVO);
        }
        rs.close();
        db.close();

        return listaPastas;
    }


    public List<PastaVO> listarPastas() {


        List<PastaVO> listaPastas = new ArrayList<>();
        Cursor rs;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};




        db = banco.getReadableDatabase();
        rs = db.query(TABELA_PASTA, campos, null, null, null, null, null, null);

        while (rs.moveToNext()) {
            PastaVO pastaVO = new PastaVO();
            pastaVO.setId(rs.getInt(rs.getColumnIndex(CriaBanco.ID)));
            pastaVO.setNomePasta(rs.getString(rs.getColumnIndex(CriaBanco.NOME_PASTA)));
            pastaVO.setTimestampCriacaoPasta(rs.getString(rs.getColumnIndex(CriaBanco.TIMESTAMP_CRIACAO_PASTA)));
            pastaVO.setSenhaPasta(rs.getString(rs.getColumnIndex(CriaBanco.SENHA_PASTA)));
            pastaVO.setInvisivel(rs.getInt(rs.getColumnIndex(CriaBanco.INVISIVEL)));

            listaPastas.add(pastaVO);
        }
        rs.close();
        db.close();

        return listaPastas;
    }


    public boolean isModoMisto() {


        List<PastaVO> listaPastas = new ArrayList<>();
        Cursor rs;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};
        String where = banco.INVISIVEL + " = ?";


        db = banco.getReadableDatabase();
        rs = db.query(TABELA_PASTA, campos, null, null, null, null, null, null);

        while (rs.moveToNext()) {
            PastaVO pastaVO = new PastaVO();
            pastaVO.setId(rs.getInt(rs.getColumnIndex(CriaBanco.ID)));
            pastaVO.setNomePasta(rs.getString(rs.getColumnIndex(CriaBanco.NOME_PASTA)));
            pastaVO.setTimestampCriacaoPasta(rs.getString(rs.getColumnIndex(CriaBanco.TIMESTAMP_CRIACAO_PASTA)));
            pastaVO.setSenhaPasta(rs.getString(rs.getColumnIndex(CriaBanco.SENHA_PASTA)));
            pastaVO.setInvisivel(rs.getInt(rs.getColumnIndex(CriaBanco.INVISIVEL)));

            listaPastas.add(pastaVO);
        }
        rs.close();
        db.close();
        boolean encontrouVisivel = false;
        boolean encontrouInvisivel = false;

        for (PastaVO pasta : listaPastas) {
            if (pasta.getInvisivel() == 0) {
                encontrouVisivel = true;
            }
            if (pasta.getInvisivel() == 1) {
                encontrouInvisivel = true;
            }
        }

        return encontrouVisivel && encontrouInvisivel;
    }

    public int excluir(int idPasta) {
        int deletou;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};
        db = banco.getReadableDatabase();

        String where = CriaBanco.ID + " = ?";
        String[] argumentos = {String.valueOf(idPasta)};
        deletou = db.delete(banco.TABELA_PASTA, where, argumentos);

        db.close();

        return deletou;
    }

    public PastaVO buscarPorId(int idPasta) {

        PastaVO pastaVO = new PastaVO();
        Cursor rs;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};
        String where = banco.ID + " = ?";
        String[] argumentos = {String.valueOf(idPasta)};
        db = banco.getReadableDatabase();
        rs = db.query(TABELA_PASTA, campos, where, argumentos, null, null, null, null);

        while (rs.moveToNext()) {

            pastaVO.setId(rs.getInt(rs.getColumnIndex(CriaBanco.ID)));
            pastaVO.setNomePasta(rs.getString(rs.getColumnIndex(CriaBanco.NOME_PASTA)));
            pastaVO.setTimestampCriacaoPasta(rs.getString(rs.getColumnIndex(CriaBanco.TIMESTAMP_CRIACAO_PASTA)));
            pastaVO.setSenhaPasta(rs.getString(rs.getColumnIndex(CriaBanco.SENHA_PASTA)));
            pastaVO.setInvisivel(rs.getInt(rs.getColumnIndex(CriaBanco.INVISIVEL)));


        }
        rs.close();
        db.close();

        return pastaVO;
    }

    public PastaVO buscarPorNome(String nomePasta) {

        PastaVO pastaVO = null;
        Cursor rs;
        String[] campos = {banco.ID, banco.NOME_PASTA, banco.TIMESTAMP_CRIACAO_PASTA, banco.SENHA_PASTA, banco.INVISIVEL};
        String where = banco.NOME_PASTA + " = ?";
        String[] argumentos = {nomePasta};
        db = banco.getReadableDatabase();
        rs = db.query(TABELA_PASTA, campos, where, argumentos, null, null, null, null);

        while (rs.moveToNext()) {
            pastaVO = new PastaVO();
            pastaVO.setId(rs.getInt(rs.getColumnIndex(CriaBanco.ID)));
            pastaVO.setNomePasta(rs.getString(rs.getColumnIndex(CriaBanco.NOME_PASTA)));
            pastaVO.setTimestampCriacaoPasta(rs.getString(rs.getColumnIndex(CriaBanco.TIMESTAMP_CRIACAO_PASTA)));
            pastaVO.setSenhaPasta(rs.getString(rs.getColumnIndex(CriaBanco.SENHA_PASTA)));
            pastaVO.setInvisivel(rs.getInt(rs.getColumnIndex(CriaBanco.INVISIVEL)));


        }
        rs.close();
        db.close();

        return pastaVO;
    }
}

