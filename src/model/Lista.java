package model;

/**
 * Lista Encadeada Simples genérica.
 * Utilizada nas operações de atualização e remoção conforme requisito do trabalho.
 */
public class Lista<T> {

    private class No {
        T dado;
        No proximo;

        No(T dado) {
            this.dado = dado;
            this.proximo = null;
        }
    }

    private No inicio;
    private int tamanho;

    public Lista() {
        this.inicio = null;
        this.tamanho = 0;
    }

    /** Insere elemento no final da lista */
    public void inserir(T dado) {
        No novo = new No(dado);
        if (inicio == null) {
            inicio = novo;
        } else {
            No atual = inicio;
            while (atual.proximo != null) {
                atual = atual.proximo;
            }
            atual.proximo = novo;
        }
        tamanho++;
    }

    /** Remove o primeiro elemento com dado igual (usa equals) */
    public boolean remover(T dado) {
        if (inicio == null) return false;

        if (inicio.dado.equals(dado)) {
            inicio = inicio.proximo;
            tamanho--;
            return true;
        }

        No atual = inicio;
        while (atual.proximo != null) {
            if (atual.proximo.dado.equals(dado)) {
                atual.proximo = atual.proximo.proximo;
                tamanho--;
                return true;
            }
            atual = atual.proximo;
        }
        return false;
    }

    /** Retorna elemento na posição index (base 0) */
    public T obter(int index) {
        if (index < 0 || index >= tamanho) throw new IndexOutOfBoundsException("Índice inválido: " + index);
        No atual = inicio;
        for (int i = 0; i < index; i++) {
            atual = atual.proximo;
        }
        return atual.dado;
    }

    /** Atualiza elemento na posição index */
    public void atualizar(int index, T dado) {
        if (index < 0 || index >= tamanho) throw new IndexOutOfBoundsException("Índice inválido: " + index);
        No atual = inicio;
        for (int i = 0; i < index; i++) {
            atual = atual.proximo;
        }
        atual.dado = dado;
    }

    public int getTamanho() { return tamanho; }

    public boolean estaVazia() { return tamanho == 0; }

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
