package controller;

import model.Cursos;
import model.Fila;
import model.Lista;

import java.io.*;
import java.util.ArrayList;

/**
 * Controller responsável pelas operações de CRUD de Cursos.
 * Leitura/escrita em cursos.csv. Atualização/remoção via Lista Encadeada.
 */
public class CursosController {

    private static final String ARQUIVO = "src/main/resource/data/cursos.csv";
    private static final String CABECALHO = "codigo,nome,areaConhecimento";

    // ─── CREATE ──────────────────────────────────────────────────────────────

    public void inserir(Cursos curso) throws IOException {
        // Verifica duplicidade de código
        Lista<Cursos> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == curso.getCodigo()) {
                throw new IllegalArgumentException("Já existe um curso com o código " + curso.getCodigo());
            }
        }
        lista.inserir(curso);
        salvar(lista);
    }

    public Fila<Cursos> consultarFila() throws IOException {
        Fila<Cursos> fila = new Fila<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha = br.readLine(); // pula cabeçalho
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    fila.enfileirar(parseLinha(linha));
                }
            }
        }
        return fila;
    }

    // Retorna todos cursos como lista (para combos, validações)
    public Lista<Cursos> consultarLista() throws IOException {
        return carregarLista();
    }

    public Cursos buscarPorCodigo(int codigo) throws IOException {
        Lista<Cursos> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == codigo) return lista.obter(i);
        }
        return null;
    }

    public boolean atualizar(Cursos cursoAtualizado) throws IOException {
        Lista<Cursos> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == cursoAtualizado.getCodigo()) {
                lista.atualizar(i, cursoAtualizado);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int codigo) throws IOException {
        Lista<Cursos> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == codigo) {
                lista.remover(lista.obter(i));
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    private Lista<Cursos> carregarLista() throws IOException {
        Lista<Cursos> lista = new Lista<>();
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

    private void salvar(Lista<Cursos> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO, false))) {
            pw.println(CABECALHO);
            for (int i = 0; i < lista.getTamanho(); i++) {
                pw.println(lista.obter(i).toString());
            }
        }
    }

    private Cursos parseLinha(String linha) {
        String[] partes = linha.split(",", -1);
        return new Cursos(
            Integer.parseInt(partes[0].trim()),
            partes[1].trim(),
            partes[2].trim()
        );
    }
}
