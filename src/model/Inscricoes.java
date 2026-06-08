package model;

public class Inscricoes {
    private String cpfProfessor;
    private int codigoDisciplina;
    private String codigoProcesso;

    public Inscricoes(String cpfProfessor, int codigoDisciplina, String codigoProcesso) {
        this.cpfProfessor = cpfProfessor;
        this.codigoDisciplina = codigoDisciplina;
        this.codigoProcesso = codigoProcesso;
    }

    public String getCpfProfessor() { return cpfProfessor; }
    public void setCpfProfessor(String cpfProfessor) { this.cpfProfessor = cpfProfessor; }

    public int getCodigoDisciplina() { return codigoDisciplina; }
    public void setCodigoDisciplina(int codigoDisciplina) { this.codigoDisciplina = codigoDisciplina; }

    public String getCodigoProcesso() { return codigoProcesso; }
    public void setCodigoProcesso(String codigoProcesso) { this.codigoProcesso = codigoProcesso; }

    @Override
    public String toString() {
        return cpfProfessor + "," + codigoDisciplina + "," + codigoProcesso;
    }
}
