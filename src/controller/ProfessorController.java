package controller;

import model.Fila;
import model.Lista;
import model.Professor;

import java.io.*;

/**
 * Controller responsável pelas operações de CRUD de Professores.
 * Leitura/escrita em professor.csv. Atualização/remoção via Lista Encadeada.
 */
public class ProfessorController {

    private static final String ARQUIVO = "src/main/resource/data/professor.csv";
    private static final String CABECALHO = "cpf,nome,area,pontos";

    public void inserir(Professor professor) throws IOException {
        Lista<Professor> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCpf().equals(professor.getCpf())) {
                throw new IllegalArgumentException("Já existe professor com CPF " + professor.getCpf());
            }
        }
        lista.inserir(professor);
        salvar(lista);
    }

    public Fila<Professor> consultarFila() throws IOException {
        Fila<Professor> fila = new Fila<>();
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return fila;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    fila.enfileirar(parseLinha(linha));
                }
            }
        }
        return fila;
    }

    public Lista<Professor> consultarLista() throws IOException {
        return carregarLista();
    }

    public Professor buscarPorCpf(String cpf) throws IOException {
        Lista<Professor> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCpf().equals(cpf)) return lista.obter(i);
        }
        return null;
    }

    public boolean atualizar(Professor professorAtualizado) throws IOException {
        Lista<Professor> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCpf().equals(professorAtualizado.getCpf())) {
                lista.atualizar(i, professorAtualizado);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    public boolean remover(String cpf) throws IOException {
        Lista<Professor> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCpf().equals(cpf)) {
                lista.remover(lista.obter(i));
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    Lista<Professor> carregarLista() throws IOException {
        Lista<Professor> lista = new Lista<>();
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine(); // pula cabeçalho
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    lista.inserir(parseLinha(linha));
                }
            }
        }
        return lista;
    }

    private void salvar(Lista<Professor> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO, false))) {
            pw.println(CABECALHO);
            for (int i = 0; i < lista.getTamanho(); i++) {
                pw.println(lista.obter(i).toString());
            }
        }
    }

    private Professor parseLinha(String linha) {
        String[] p = linha.split(",", -1);
        return new Professor(
            p[0].trim(),
            p[1].trim(),
            p[2].trim(),
            Integer.parseInt(p[3].trim())
        );
    }
}
