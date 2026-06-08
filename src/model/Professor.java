package model;

public class Professor {
    private String cpf;
    private String nome;
    private String area;
    private int pontos;

    public Professor(String cpf, String nome, String area, int pontos) {
        this.cpf = cpf;
        this.nome = nome;
        this.area = area;
        this.pontos = pontos;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    @Override
    public String toString() {
        return cpf + "," + nome + "," + area + "," + pontos;
    }
}
