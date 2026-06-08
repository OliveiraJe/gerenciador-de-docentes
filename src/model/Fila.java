package model;

/**
 * Fila genérica (FIFO) implementada com lista encadeada.
 * Utilizada para consultas de disciplinas, cursos e professores
 * conforme requisito do trabalho.
 */
public class Fila<T> {

    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No inicio;
    private No fim;
    private int tamanho;

    public Fila() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    /** Enfileira um elemento */
    public void enfileirar(T dado) {
        No novo = new No(dado);
        if (fim == null) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
        tamanho++;
    }

    /** Desenfileira e retorna o primeiro elemento */
    public T desenfileirar() {
        if (estaVazia()) throw new RuntimeException("Fila vazia");
        T dado = inicio.dado;
        inicio = inicio.proximo;
        if (inicio == null) fim = null;
        tamanho--;
        return dado;
    }

    /** Retorna o primeiro elemento sem remover */
    public T frente() {
        if (estaVazia()) throw new RuntimeException("Fila vazia");
        return inicio.dado;
    }

    public boolean estaVazia() { return tamanho == 0; }

    public int getTamanho() { return tamanho; }

    /** Converte para array de Object para uso em JTable */
    public Object[] paraArray() {
        Object[] arr = new Object[tamanho];
        No atual = inicio;
        for (int i = 0; i < tamanho; i++) {
            arr[i] = atual.dado;
            atual = atual.proximo;
        }
        return arr;
    }
}
