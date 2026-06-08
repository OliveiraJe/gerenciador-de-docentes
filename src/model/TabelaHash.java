package model;

/**
 * Tabela de Espalhamento (Hash Table) com função hash customizada.
 * Utilizada para consulta de disciplinas com processos abertos.
 * Colisões resolvidas por encadeamento (chaining).
 */
public class TabelaHash {

    private static final int TAMANHO = 101; // número primo para melhor distribuição

    private class Entrada {
        String chave;   // codigoProcesso
        Disciplina disciplina;
        Entrada proximo;

        Entrada(String chave, Disciplina disciplina) {
            this.chave = chave;
            this.disciplina = disciplina;
            this.proximo = null;
        }
    }

    private Entrada[] tabela;
    private int quantidade;

    public TabelaHash() {
        tabela = new Entrada[TAMANHO];
        quantidade = 0;
    }

    /**
     * Função hash customizada: soma ponderada dos caracteres da chave
     * com multiplicador primo 31 (semelhante ao Java hashCode padrão,
     * mas implementada manualmente conforme exigido pelo trabalho).
     */
    private int hash(String chave) {
        int h = 0;
        for (int i = 0; i < chave.length(); i++) {
            h = (31 * h + chave.charAt(i)) % TAMANHO;
        }
        return Math.abs(h) % TAMANHO;
    }

    /** Insere uma disciplina com o código de processo como chave */
    public void inserir(String codigoProcesso, Disciplina disciplina) {
        int pos = hash(codigoProcesso);
        Entrada nova = new Entrada(codigoProcesso, disciplina);

        if (tabela[pos] == null) {
            tabela[pos] = nova;
        } else {
            // Encadeamento para colisão
            Entrada atual = tabela[pos];
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = nova;
        }
        quantidade++;
    }

    /** Busca disciplina pelo código de processo */
    public Disciplina buscar(String codigoProcesso) {
        int pos = hash(codigoProcesso);
        Entrada atual = tabela[pos];
        while (atual != null) {
            if (atual.chave.equals(codigoProcesso)) {
                return atual.disciplina;
            }
            atual = atual.proximo;
        }
        return null;
    }

    /** Retorna todas as disciplinas presentes na tabela (com processos ativos) */
    public Lista<Disciplina> listarTodas() {
        Lista<Disciplina> lista = new Lista<>();
        for (int i = 0; i < TAMANHO; i++) {
            Entrada atual = tabela[i];
            while (atual != null) {
                lista.inserir(atual.disciplina);
                atual = atual.proximo;
            }
        }
        return lista;
    }

    /** Remove entrada pelo código de processo */
    public boolean remover(String codigoProcesso) {
        int pos = hash(codigoProcesso);
        if (tabela[pos] == null) return false;

        if (tabela[pos].chave.equals(codigoProcesso)) {
            tabela[pos] = tabela[pos].proximo;
            quantidade--;
            return true;
        }

        Entrada atual = tabela[pos];
        while (atual.proximo != null) {
            if (atual.proximo.chave.equals(codigoProcesso)) {
                atual.proximo = atual.proximo.proximo;
                quantidade--;
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    public int getQuantidade() { return quantidade; }
}
