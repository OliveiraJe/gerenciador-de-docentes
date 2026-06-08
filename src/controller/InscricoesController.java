package controller;

import model.Disciplina;
import model.Fila;
import model.Inscricoes;
import model.Lista;
import model.Professor;
import model.TabelaHash;

import java.io.*;

/**
 * Controller responsável pelas operações de CRUD de Inscrições.
 * Leitura/escrita em inscricoes.csv. Atualização/remoção via Lista Encadeada.
 */
public class InscricoesController {

    private static final String ARQUIVO = "src/main/resource/data/inscricoes.csv";
    private static final String CABECALHO = "cpfProfessor,codigoDisciplina,codigoProcesso";

    public void inserir(Inscricoes inscricao) throws IOException {
        Lista<Inscricoes> lista = carregarLista();
        // Impede duplicidade de CPF+Disciplina+Processo
        for (int i = 0; i < lista.getTamanho(); i++) {
            Inscricoes ins = lista.obter(i);
            if (ins.getCpfProfessor().equals(inscricao.getCpfProfessor())
                    && ins.getCodigoDisciplina() == inscricao.getCodigoDisciplina()
                    && ins.getCodigoProcesso().equals(inscricao.getCodigoProcesso())) {
                throw new IllegalArgumentException("Inscrição já cadastrada para este professor/disciplina/processo.");
            }
        }
        lista.inserir(inscricao);
        salvar(lista);
    }

    public Lista<Inscricoes> consultarLista() throws IOException {
        return carregarLista();
    }

    public boolean atualizar(String cpfAntigo, int codDisciplinaAntigo, String procAntigo,
                             Inscricoes nova) throws IOException {
        Lista<Inscricoes> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            Inscricoes ins = lista.obter(i);
            if (ins.getCpfProfessor().equals(cpfAntigo)
                    && ins.getCodigoDisciplina() == codDisciplinaAntigo
                    && ins.getCodigoProcesso().equals(procAntigo)) {
                lista.atualizar(i, nova);
                salvar(lista);
                return true;
            }
        }
        return false;
    }

    public boolean remover(String cpfProfessor, int codigoDisciplina, String codigoProcesso) throws IOException {
        Lista<Inscricoes> lista = carregarLista();
        for (int i = 0; i < lista.getTamanho(); i++) {
            Inscricoes ins = lista.obter(i);
            if (ins.getCpfProfessor().equals(cpfProfessor)
                    && ins.getCodigoDisciplina() == codigoDisciplina
                    && ins.getCodigoProcesso().equals(codigoProcesso)) {
                lista.remover(ins);
                salvar(lista);
                return true;
            }
        }
        return false;
    }
    
    // Remove todas as inscrições de uma disciplina 
    public void removerPorDisciplina(int codigoDisciplina) throws IOException {
        Lista<Inscricoes> lista = carregarLista();
        Lista<Inscricoes> novaLista = new Lista<>();
        for (int i = 0; i < lista.getTamanho(); i++) {
            if (lista.obter(i).getCodigoDisciplina() != codigoDisciplina) {
                novaLista.inserir(lista.obter(i));
            }
        }
        salvar(novaLista);
    }

    //Consulta de Inscritos por Disciplina (ordenado por pontuação)
    public Lista<Professor> consultarInscritosPorDisciplina(int codigoDisciplina, String codigoProcesso)
            throws IOException {

        Lista<Inscricoes> inscs = carregarLista();
        ProfessorController pc = new ProfessorController();
        Lista<Professor> professorList = new Lista<>();

        // Filtra inscrições da disciplina/processo
        for (int i = 0; i < inscs.getTamanho(); i++) {
            Inscricoes ins = inscs.obter(i);
            if (ins.getCodigoDisciplina() == codigoDisciplina
                    && ins.getCodigoProcesso().equals(codigoProcesso)) {
                Professor p = pc.buscarPorCpf(ins.getCpfProfessor());
                if (p != null) professorList.inserir(p);
            }
        }

        // Insertion Sort por pontuação (decrescente)
        for (int i = 1; i < professorList.getTamanho(); i++) {
            Professor chave = professorList.obter(i);
            int j = i - 1;
            while (j >= 0 && professorList.obter(j).getPontos() < chave.getPontos()) {
                professorList.atualizar(j + 1, professorList.obter(j));
                j--;
            }
            professorList.atualizar(j + 1, chave);
        }

        return professorList;
    }

    //Tabela Hash: disciplinas com processos ativos

    public TabelaHash carregarTabelaHash() throws IOException {
        TabelaHash tabela = new TabelaHash();
        Lista<Inscricoes> inscs = carregarLista();
        DisciplinaController dc = new DisciplinaController();

        for (int i = 0; i < inscs.getTamanho(); i++) {
            Inscricoes ins = inscs.obter(i);
            Disciplina d = dc.buscarPorCodigo(ins.getCodigoDisciplina());
            if (d != null) {
                // Evita duplicidade na tabela para o mesmo processo+disciplina
                if (tabela.buscar(ins.getCodigoProcesso() + "-" + ins.getCodigoDisciplina()) == null) {
                    tabela.inserir(ins.getCodigoProcesso() + "-" + ins.getCodigoDisciplina(), d);
                }
            }
        }
        return tabela;
    }

    Lista<Inscricoes> carregarLista() throws IOException {
        Lista<Inscricoes> lista = new Lista<>();
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    lista.inserir(parseLinha(linha));
                }
            }
        }
        return lista;
    }

    private void salvar(Lista<Inscricoes> lista) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARQUIVO, false))) {
            pw.println(CABECALHO);
            for (int i = 0; i < lista.getTamanho(); i++) {
                pw.println(lista.obter(i).toString());
            }
        }
    }

    private Inscricoes parseLinha(String linha) {
        String[] p = linha.split(",", -1);
        return new Inscricoes(
            p[0].trim(),
            Integer.parseInt(p[1].trim()),
            p[2].trim()
        );
    }
}
