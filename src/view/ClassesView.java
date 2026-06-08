package view;

import controller.CursosController;
import controller.DisciplinaController;
import model.Cursos;
import model.Disciplina;
import model.Fila;
import model.Lista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de CRUD para Disciplinas.
 * Consulta vem de uma Fila populada a partir do arquivo disciplinas.csv.
 */
public class ClassesView extends JDialog {

    private final DisciplinaController controller = new DisciplinaController();
    private final CursosController cursosCtrl = new CursosController();

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JComboBox<String> cbDiaSemana;
    private JTextField txtHorario;
    private JSpinner spnHoras;
    private JComboBox<String> cbCurso;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // Mapa auxiliar: índice → código do curso
    private Lista<Cursos> listaCursos = new Lista<>();

    public ClassesView(Frame pai) {
        super(pai, "Gerenciamento de Disciplinas", true);
        setSize(820, 560);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarCombosCursos();
        carregarTabela();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Formulário ────────────────────────────────────────────────────
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados da Disciplina"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; pForm.add(new JLabel("Código:"), g);
        g.gridx=1; txtCodigo = new JTextField(6); pForm.add(txtCodigo, g);

        g.gridx=2; pForm.add(new JLabel("Nome:"), g);
        g.gridx=3; g.fill=GridBagConstraints.HORIZONTAL; g.weightx=1;
        txtNome = new JTextField(22); pForm.add(txtNome, g);
        g.fill=GridBagConstraints.NONE; g.weightx=0;

        g.gridx=0; g.gridy=1; pForm.add(new JLabel("Dia da Semana:"), g);
        g.gridx=1; cbDiaSemana = new JComboBox<>(new String[]{
            "Segunda","Terça","Quarta","Quinta","Sexta","Sábado"});
        pForm.add(cbDiaSemana, g);

        g.gridx=2; pForm.add(new JLabel("Horário Inicial (HH:mm):"), g);
        g.gridx=3; txtHorario = new JTextField(8); pForm.add(txtHorario, g);

        g.gridx=0; g.gridy=2; pForm.add(new JLabel("Qtd. Horas/Dia:"), g);
        g.gridx=1; spnHoras = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        pForm.add(spnHoras, g);

        g.gridx=2; pForm.add(new JLabel("Curso:"), g);
        g.gridx=3; cbCurso = new JComboBox<>(); pForm.add(cbCurso, g);

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
        String[] cols = {"Código","Nome","Dia","Horário","Horas/Dia","Cód.Curso"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Disciplinas Cadastradas"));

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

    private void carregarCombosCursos() {
        try {
            listaCursos = cursosCtrl.consultarLista();
            cbCurso.removeAllItems();
            for (int i = 0; i < listaCursos.getTamanho(); i++) {
                Cursos c = listaCursos.obter(i);
                cbCurso.addItem(c.getCodigo() + " - " + c.getNome());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar cursos: " + ex.getMessage());
        }
    }

    private void inserir() {
        try {
            Disciplina d = lerForm();
            controller.inserir(d);
            JOptionPane.showMessageDialog(this, "Disciplina inserida!");
            limpar(); carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        try {
            Disciplina d = lerForm();
            boolean ok = controller.atualizar(d);
            if (ok) { JOptionPane.showMessageDialog(this, "Disciplina atualizada!"); limpar(); carregarTabela(); }
            else JOptionPane.showMessageDialog(this, "Disciplina não encontrada.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void remover() {
        try {
            int cod = Integer.parseInt(txtCodigo.getText().trim());
            int conf = JOptionPane.showConfirmDialog(this,
                "Remover disciplina " + cod + "? Todas as inscrições associadas também serão removidas.",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (conf == JOptionPane.YES_OPTION) {
                boolean ok = controller.remover(cod);
                if (ok) { JOptionPane.showMessageDialog(this, "Disciplina e inscrições removidas!"); limpar(); carregarTabela(); }
                else JOptionPane.showMessageDialog(this, "Disciplina não encontrada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        try {
            modeloTabela.setRowCount(0);
            Fila<Disciplina> fila = controller.consultarFila();
            while (!fila.estaVazia()) {
                Disciplina d = fila.desenfileirar();
                modeloTabela.addRow(new Object[]{
                    d.getCodigo(), d.getNome(), d.getDiaSemana(),
                    d.getHorarioInicial(), d.getQtdHorasDiarias(), d.getCodigoCurso()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherForm() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        txtCodigo.setText(modeloTabela.getValueAt(row, 0).toString());
        txtNome.setText(modeloTabela.getValueAt(row, 1).toString());
        cbDiaSemana.setSelectedItem(modeloTabela.getValueAt(row, 2).toString());
        txtHorario.setText(modeloTabela.getValueAt(row, 3).toString());
        spnHoras.setValue(Integer.parseInt(modeloTabela.getValueAt(row, 4).toString()));
        int codCurso = Integer.parseInt(modeloTabela.getValueAt(row, 5).toString());
        for (int i = 0; i < listaCursos.getTamanho(); i++) {
            if (listaCursos.obter(i).getCodigo() == codCurso) { cbCurso.setSelectedIndex(i); break; }
        }
    }

    private Disciplina lerForm() {
        String codStr = txtCodigo.getText().trim();
        String nome   = txtNome.getText().trim();
        String horario= txtHorario.getText().trim();
        if (codStr.isEmpty() || nome.isEmpty() || horario.isEmpty())
            throw new IllegalArgumentException("Preencha todos os campos.");
        if (cbCurso.getSelectedIndex() < 0 || listaCursos.getTamanho() == 0)
            throw new IllegalArgumentException("Nenhum curso disponível. Cadastre um curso primeiro.");
        int codigoCurso = listaCursos.obter(cbCurso.getSelectedIndex()).getCodigo();
        return new Disciplina(
            Integer.parseInt(codStr), nome,
            cbDiaSemana.getSelectedItem().toString(),
            horario, (int)spnHoras.getValue(), codigoCurso
        );
    }

    private void limpar() {
        txtCodigo.setText(""); txtNome.setText(""); txtHorario.setText("");
        spnHoras.setValue(2); cbDiaSemana.setSelectedIndex(0); cbCurso.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private void estilizar(JButton btn, Color cor) {
        btn.setBackground(cor); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
