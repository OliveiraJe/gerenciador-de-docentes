package view;

import controller.DisciplinaController;
import controller.InscricoesController;
import controller.ProfessorController;
import model.Disciplina;
import model.Inscricoes;
import model.Lista;
import model.Professor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de CRUD para Inscrições em Processos Seletivos.
 * Opera sobre inscricoes.csv via Lista Encadeada.
 */
public class SubscriptionsView extends JDialog {

    private final InscricoesController controller   = new InscricoesController();
    private final DisciplinaController  discCtrl    = new DisciplinaController();
    private final ProfessorController   profCtrl    = new ProfessorController();

    private JComboBox<String> cbProfessor;
    private JComboBox<String> cbDisciplina;
    private JTextField txtProcesso;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private Lista<Professor> listaProfessores = new Lista<>();
    private Lista<Disciplina> listaDisciplinas = new Lista<>();

    // Valores originais para atualização
    private String cpfOriginal;
    private int    codDiscOriginal;
    private String procOriginal;

    public SubscriptionsView(Frame pai) {
        super(pai, "Inscrições em Processos Seletivos", true);
        setSize(820, 540);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarCombos();
        carregarTabela();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────────────
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados da Inscrição"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("Professor:"), g);
        g.gridx=1; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        cbProfessor = new JComboBox<>(); pForm.add(cbProfessor, g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Disciplina:"), g);
        g.gridx=1; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        cbDisciplina = new JComboBox<>(); pForm.add(cbDisciplina, g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        g.gridx=0; g.gridy=2; pForm.add(new JLabel("Código do Processo:"), g);
        g.gridx=1; g.fill=GridBagConstraints.HORIZONTAL;
        txtProcesso = new JTextField(16); pForm.add(txtProcesso, g);
        g.fill=GridBagConstraints.NONE;

        // ── Botões ────────────────────────────────────────────────────────
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        JButton btnInserir   = new JButton("Inserir");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover   = new JButton("Remover");
        JButton btnConsultar = new JButton("Consultar");
        JButton btnLimpar    = new JButton("Limpar");
        estilizar(btnInserir,   new Color(39,174,96));
        estilizar(btnAtualizar, new Color(41,128,185));
        estilizar(btnRemover,   new Color(231,76,60));
        estilizar(btnConsultar, new Color(142,68,173));
        estilizar(btnLimpar,    Color.GRAY);
        pBotoes.add(btnInserir); pBotoes.add(btnAtualizar); pBotoes.add(btnRemover);
        pBotoes.add(btnConsultar); pBotoes.add(btnLimpar);

        JPanel pSup = new JPanel(new BorderLayout());
        pSup.add(pForm, BorderLayout.CENTER);
        pSup.add(pBotoes, BorderLayout.SOUTH);

        // ── Tabela ────────────────────────────────────────────────────────
        String[] cols = {"CPF Professor","Nome Professor","Disciplina","Processo"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Inscrições Ativas"));

        add(pSup, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnInserir.addActionListener(e -> inserir());
        btnAtualizar.addActionListener(e -> atualizar());
        btnRemover.addActionListener(e -> remover());
        btnConsultar.addActionListener(e -> carregarTabela());
        btnLimpar.addActionListener(e -> limpar());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherForm();
        });
    }

    private void carregarCombos() {
        try {
            listaProfessores = profCtrl.consultarLista();
            cbProfessor.removeAllItems();
            for (int i = 0; i < listaProfessores.getTamanho(); i++) {
                Professor p = listaProfessores.obter(i);
                cbProfessor.addItem(p.getCpf() + " - " + p.getNome());
            }

            listaDisciplinas = discCtrl.consultarLista();
            cbDisciplina.removeAllItems();
            for (int i = 0; i < listaDisciplinas.getTamanho(); i++) {
                Disciplina d = listaDisciplinas.obter(i);
                cbDisciplina.addItem(d.getCodigo() + " - " + d.getNome());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar combos: " + ex.getMessage());
        }
    }

    private void inserir() {
        try {
            Inscricoes ins = lerForm();
            controller.inserir(ins);
            JOptionPane.showMessageDialog(this, "Inscrição realizada com sucesso!");
            limpar(); carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            if (cpfOriginal == null) throw new IllegalStateException("Selecione uma inscrição para atualizar.");
            Inscricoes nova = lerForm();
            boolean ok = controller.atualizar(cpfOriginal, codDiscOriginal, procOriginal, nova);
            if (ok) { JOptionPane.showMessageDialog(this, "Inscrição atualizada!"); limpar(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Inscrição não encontrada.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void remover() {
        try {
            if (cpfOriginal == null) throw new IllegalStateException("Selecione uma inscrição para remover.");
            int conf = JOptionPane.showConfirmDialog(this, "Confirma remoção desta inscrição?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = controller.remover(cpfOriginal, codDiscOriginal, procOriginal);
                if (ok) { JOptionPane.showMessageDialog(this, "Inscrição removida!"); limpar(); carregarTabela(); }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0);
            Lista<Inscricoes> lista = controller.consultarLista();
            for (int i = 0; i < lista.getTamanho(); i++) {
                Inscricoes ins = lista.obter(i);
                Professor p = profCtrl.buscarPorCpf(ins.getCpfProfessor());
                Disciplina d = discCtrl.buscarPorCodigo(ins.getCodigoDisciplina());
                String nomePr = p != null ? p.getNome() : "N/A";
                String nomeDisc = d != null ? d.getNome() : "N/A";
                modeloTabela.addRow(new Object[]{
                    ins.getCpfProfessor(), nomePr, nomeDisc, ins.getCodigoProcesso()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherForm() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        cpfOriginal    = modeloTabela.getValueAt(row, 0).toString();
        String nomeDisc = modeloTabela.getValueAt(row, 2).toString();
        procOriginal   = modeloTabela.getValueAt(row, 3).toString();

        // Seleciona professor no combo
        for (int i = 0; i < listaProfessores.getTamanho(); i++) {
            if (listaProfessores.obter(i).getCpf().equals(cpfOriginal)) {
                cbProfessor.setSelectedIndex(i); break;
            }
        }
        // Seleciona disciplina
        for (int i = 0; i < listaDisciplinas.getTamanho(); i++) {
            if (listaDisciplinas.obter(i).getNome().equals(nomeDisc)) {
                cbDisciplina.setSelectedIndex(i);
                codDiscOriginal = listaDisciplinas.obter(i).getCodigo();
                break;
            }
        }
        txtProcesso.setText(procOriginal);
    }

    private Inscricoes lerForm() {
        int idxProf = cbProfessor.getSelectedIndex();
        int idxDisc = cbDisciplina.getSelectedIndex();
        String proc = txtProcesso.getText().trim();
        if (listaProfessores.getTamanho() == 0)
            throw new IllegalArgumentException("Nenhum professor cadastrado. Cadastre um professor primeiro.");
        if (listaDisciplinas.getTamanho() == 0)
            throw new IllegalArgumentException("Nenhuma disciplina cadastrada. Cadastre uma disciplina primeiro.");
        if (idxProf < 0 || idxDisc < 0 || proc.isEmpty())
            throw new IllegalArgumentException("Preencha todos os campos.");
        String cpf = listaProfessores.obter(idxProf).getCpf();
        int codDisc = listaDisciplinas.obter(idxDisc).getCodigo();
        return new Inscricoes(cpf, codDisc, proc);
    }

    private void limpar() {
        cpfOriginal = null; codDiscOriginal = 0; procOriginal = null;
        cbProfessor.setSelectedIndex(0);
        cbDisciplina.setSelectedIndex(0);
        txtProcesso.setText("");
        tabela.clearSelection();
    }

    private void estilizar(JButton btn, Color cor) {
        btn.setBackground(cor); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
