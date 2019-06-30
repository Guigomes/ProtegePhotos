package ggsoftware.com.br.protegephotospro.dao;

/**
 * Created by f3861879 on 15/08/17.
 */

public class PastaVO {

    private int id;

    private String nomePasta;

    private String timestampCriacaoPasta;

    private String senhaPasta;

    private int invisivel;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomePasta() {
        return nomePasta;
    }

    public void setNomePasta(String nomePasta) {
        this.nomePasta = nomePasta;
    }

    public String getTimestampCriacaoPasta() {
        return timestampCriacaoPasta;
    }

    public void setTimestampCriacaoPasta(String timestampCriacaoPasta) {
        this.timestampCriacaoPasta = timestampCriacaoPasta;
    }

    public String getSenhaPasta() {
        return senhaPasta;
    }

    public void setSenhaPasta(String senhaPasta) {
        this.senhaPasta = senhaPasta;
    }

    public int getInvisivel() {
        return invisivel;
    }

    public void setInvisivel(int invisivel) {
        this.invisivel = invisivel;
    }
}
