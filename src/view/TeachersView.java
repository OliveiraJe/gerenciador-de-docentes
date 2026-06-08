package view;

import controller.ProfessorController;
import model.Fila;
import model.Professor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de CRUD para Professores.
 * Consulta vem de uma Fila populada a partir do arquivo professor.csv.
 */
public class TeachersView extends JDialog {

    private final ProfessorController controller = new ProfessorController();

    private JTextField txtCpf;
    private JTextField txtNome;
    private JTextField txtArea;
    private JSpinner   spnPontos;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public TeachersView(Frame pai) {
        super(pai, "Gerenciamento de Professores", true);
        setSize(760, 520);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarTabela();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────────────
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados do Professor"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("CPF:"), g);
        g.gridx=1; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=0.4;
        txtCpf = new JTextField(14); pForm.add(txtCpf, g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        g.gridx=2; pForm.add(new JLabel("Nome:"), g);
        g.gridx=3; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        txtNome = new JTextField(22); pForm.add(txtNome, g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Área:"), g);
        g.gridx=1; g.gridwidth=2; g.fill=GridBagConstraints.HORIZONTAL;
        txtArea = new JTextField(22); pForm.add(txtArea, g);
        g.gridwidth=1; g.fill=GridBagConstraints.NONE;

        g.gridx=3; pForm.add(new JLabel("Pontuação:"), g);
        g.gridx=4; spnPontos = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        pForm.add(spnPontos, g);

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
        String[] cols = {"CPF","Nome","Área","Pontuação"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Professores Cadastrados"));

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

    private void inserir() {
        try {
            Professor p = lerForm();
            controller.inserir(p);
            JOptionPane.showMessageDialog(this, "Professor inserido com sucesso!");
            limpar(); carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            Professor p = lerForm();
            boolean ok = controller.atualizar(p);
            if (ok) { JOptionPane.showMessageDialog(this, "Professor atualizado!"); limpar(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Professor não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void remover() {
        try {
            String cpf = txtCpf.getText().trim();
            if (cpf.isEmpty()) throw new IllegalArgumentException("Informe o CPF.");
            int conf = JOptionPane.showConfirmDialog(this, "Confirma remoção do professor " + cpf + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = controller.remover(cpf);
                if (ok) { JOptionPane.showMessageDialog(this, "Professor removido!"); limpar(); carregarTabela(); }
                else JOptionPane.showMessageDialog(this, "Professor não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0);
            Fila<Professor> fila = controller.consultarFila();
            while (!fila.estaVazia()) {
                Professor p = fila.desenfileirar();
                modeloTabela.addRow(new Object[]{p.getCpf(), p.getNome(), p.getArea(), p.getPontos()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherForm() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        txtCpf.setText(modeloTabela.getValueAt(row, 0).toString());
        txtNome.setText(modeloTabela.getValueAt(row, 1).toString());
        txtArea.setText(modeloTabela.getValueAt(row, 2).toString());
        spnPontos.setValue(Integer.parseInt(modeloTabela.getValueAt(row, 3).toString()));
    }

    private Professor lerForm() {
        String cpf  = txtCpf.getText().trim();
        String nome = txtNome.getText().trim();
        String area = txtArea.getText().trim();
        if (cpf.isEmpty() || nome.isEmpty() || area.isEmpty())
            throw new IllegalArgumentException("Preencha todos os campos.");
        return new Professor(cpf, nome, area, (int)spnPontos.getValue());
    }

    private void limpar() {
        txtCpf.setText(""); txtNome.setText(""); txtArea.setText(""); spnPontos.setValue(0);
        tabela.clearSelection();
    }

    private void estilizar(JButton btn, Color cor) {
        btn.setBackground(cor); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
