package model;

public class Disciplina {
    private int codigo;
    private String nome;
    private String diaSemana;
    private String horarioInicial;
    private int qtdHorasDiarias;
    private int codigoCurso;

    public Disciplina(int codigo, String nome, String diaSemana, String horarioInicial,
                      int qtdHorasDiarias, int codigoCurso) {
        this.codigo = codigo;
        this.nome = nome;
        this.diaSemana = diaSemana;
        this.horarioInicial = horarioInicial;
        this.qtdHorasDiarias = qtdHorasDiarias;
        this.codigoCurso = codigoCurso;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHorarioInicial() { return horarioInicial; }
    public void setHorarioInicial(String horarioInicial) { this.horarioInicial = horarioInicial; }

    public int getQtdHorasDiarias() { return qtdHorasDiarias; }
    public void setQtdHorasDiarias(int qtdHorasDiarias) { this.qtdHorasDiarias = qtdHorasDiarias; }

    public int getCodigoCurso() { return codigoCurso; }
    public void setCodigoCurso(int codigoCurso) { this.codigoCurso = codigoCurso; }

    @Override
    public String toString() {
        return codigo + "," + nome + "," + diaSemana + "," + horarioInicial + ","
                + qtdHorasDiarias + "," + codigoCurso;
    }
}
