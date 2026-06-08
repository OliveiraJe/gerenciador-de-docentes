package model;

public class Cursos {
    private int codigo;
    private String nome;
    private String areaConhecimento;

    public Cursos(int codigo, String nome, String areaConhecimento) {
        this.codigo = codigo;
        this.nome = nome;
        this.areaConhecimento = areaConhecimento;
    }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getAreaConhecimento() { return areaConhecimento; }
    public void setAreaConhecimento(String areaConhecimento) { this.areaConhecimento = areaConhecimento; }

    @Override
    public String toString() {
        return codigo + "," + nome + "," + areaConhecimento;
    }
}
