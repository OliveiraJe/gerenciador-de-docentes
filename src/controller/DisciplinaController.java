package controller;

import model.Disciplina;
import model.Fila;
import model.Lista;

import java.io.*;

/**
 * Controller responsável pelas operações de CRUD de Disciplinas.
 * Leitura/escrita em disciplinas.csv. Atualização/remoção via Lista Encadeada.
 * Ao remover disciplina, aciona InscricoesController para remover inscrições relacionadas.
 */
public class DisciplinaController {

    private static final String ARQUIVO = "src/main/resource/data/disciplinas.csv";
    private static final String CABECALHO = "codigo,nome,diaSemana,horarioInicial,qtdHorasDiarias,codigoCurso";

    public void inserir(Disciplina disciplina) throws IOException {
        Lista<Disciplina> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == disciplina.getCodigo()) {
                throw new IllegalArgumentException("Já existe uma disciplina com o código " + disciplina.getCodigo());
            }
        }
        lista.inserir(disciplina);
        salvar(lista);
    }

    public Fila<Disciplina> consultarFila() throws IOException {
        Fila<Disciplina> fila = new Fila<>();
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return fila;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
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

    public Lista<Disciplina> consultarLista() throws IOException {
        return carregarLista();
    }

    public Disciplina buscarPorCodigo(int codigo) throws IOException {
        Lista<Disciplina> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == codigo) return lista.obter(i);
        }
        return null;
    }

    public boolean atualizar(Disciplina disciplinaAtualizada) throws IOException {
        Lista<Disciplina> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == disciplinaAtualizada.getCodigo()) {
                lista.atualizar(i, disciplinaAtualizada);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    public boolean remover(int codigo) throws IOException {
        Lista<Disciplina> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigo() == codigo) {
                lista.remover(lista.obter(i));
                salvar(lista);
                // Cascata: remove inscrições
                InscricoesController ic = new InscricoesController();
                ic.removerPorDisciplina(codigo);
                return true;
            }
        }
        return false;
    }

    Lista<Disciplina> carregarLista() throws IOException {
        Lista<Disciplina> lista = new Lista<>();
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

    private void salvar(Lista<Disciplina> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO, false))) {
            pw.println(CABECALHO);
            for (int i = 0; i < lista.getTamanho(); i++) {
                pw.println(lista.obter(i).toString());
            }
        }
    }

    private Disciplina parseLinha(String linha) {
        String[] p = linha.split(",", -1);
        return new Disciplina(
            Integer.parseInt(p[0].trim()),
            p[1].trim(),
            p[2].trim(),
            p[3].trim(),
            Integer.parseInt(p[4].trim()),
            Integer.parseInt(p[5].trim())
        );
    }
}
