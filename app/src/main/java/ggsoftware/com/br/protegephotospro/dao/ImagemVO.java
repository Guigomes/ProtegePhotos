package ggsoftware.com.br.protegephotospro.dao;

/**
 * Created by f3861879 on 15/08/17.
 */

public class ImagemVO {

    private int id;

    private String nome;

    private String diretorio;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDiretorio() {
        return diretorio;
    }

    public void setDiretorio(String diretorio) {
        this.diretorio = diretorio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
