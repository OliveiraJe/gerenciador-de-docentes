package view;

import controller.CursosController;
import model.Cursos;
import model.Fila;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de CRUD para Cursos.
 * Consulta vem de uma Fila populada a partir do arquivo cursos.csv.
 */
public class CoursesView extends JDialog {

    private final CursosController controller = new CursosController();

    // Campos do formulário
    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtArea;

    // Tabela
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public CoursesView(Frame pai) {
        super(pai, "Gerenciamento de Cursos", true);
        setSize(720, 520);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarTabela();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────────────
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Curso"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; txtCodigo = new JTextField(8); painelForm.add(txtCodigo, gbc);

        gbc.gridx = 2; painelForm.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        txtNome = new JTextField(20); painelForm.add(txtNome, gbc);
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Área do Conhecimento:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtArea = new JTextField(30); painelForm.add(txtArea, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // ── Botões ────────────────────────────────────────────────────────
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        JButton btnInserir    = new JButton("Inserir");
        JButton btnAtualizar  = new JButton("Atualizar");
        JButton btnRemover    = new JButton("Remover");
        JButton btnConsultar  = new JButton("Consultar");
        JButton btnLimpar     = new JButton("Limpar");

        estilizarBotao(btnInserir,   new Color(39, 174, 96));
        estilizarBotao(btnAtualizar, new Color(41, 128, 185));
        estilizarBotao(btnRemover,   new Color(231, 76, 60));
        estilizarBotao(btnConsultar, new Color(142, 68, 173));
        estilizarBotao(btnLimpar,    Color.GRAY);

        painelBotoes.add(btnInserir);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnConsultar);
        painelBotoes.add(btnLimpar);

        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelForm, BorderLayout.CENTER);
        painelSuperior.add(painelBotoes, BorderLayout.SOUTH);

        // ── Tabela ────────────────────────────────────────────────────────
        String[] colunas = {"Código", "Nome", "Área do Conhecimento"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Cursos Cadastrados"));

        add(painelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // ── Listeners ────────────────────────────────────────────────────
        btnInserir.addActionListener(e -> inserir());
        btnAtualizar.addActionListener(e -> atualizar());
        btnRemover.addActionListener(e -> remover());
        btnConsultar.addActionListener(e -> carregarTabela());
        btnLimpar.addActionListener(e -> limpar());

        // Clique na tabela preenche o formulário
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormularioTabela();
        });
    }

    private void inserir() {
        try {
            Cursos c = lerFormulario();
            controller.inserir(c);
            JOptionPane.showMessageDialog(this, "Curso inserido com sucesso!");
            limpar();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            Cursos c = lerFormulario();
            boolean ok = controller.atualizar(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Curso atualizado com sucesso!");
                limpar();
                carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Curso não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void remover() {
        try {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            int conf = JOptionPane.showConfirmDialog(this, "Confirma remoção do curso " + codigo + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = controller.remover(codigo);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Curso removido com sucesso!");
                    limpar();
                    carregarTabela();
                } else {
                    JOptionPane.showMessageDialog(this, "Curso não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0);
            Fila<Cursos> fila = controller.consultarFila();
            while (!fila.estaVazia()) {
                Cursos c = fila.desenfileirar();
                modeloTabela.addRow(new Object[]{c.getCodigo(), c.getNome(), c.getAreaConhecimento()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormularioTabela() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        txtCodigo.setText(modeloTabela.getValueAt(row, 0).toString());
        txtNome.setText(modeloTabela.getValueAt(row, 1).toString());
        txtArea.setText(modeloTabela.getValueAt(row, 2).toString());
    }

    private Cursos lerFormulario() {
        String codStr = txtCodigo.getText().trim();
        String nome   = txtNome.getText().trim();
        String area   = txtArea.getText().trim();
        if (codStr.isEmpty() || nome.isEmpty() || area.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }
        return new Cursos(Integer.parseInt(codStr), nome, area);
    }

    private void limpar() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtArea.setText("");
        tabela.clearSelection();
    }

    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
