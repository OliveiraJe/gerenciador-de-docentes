package view;

import controller.DisciplinaController;
import controller.InscricoesController;
import model.Disciplina;
import model.Lista;
import model.Professor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de consulta de inscritos por disciplina.
 * Exibe todos os dados do professor, ordenados por pontuação (Insertion Sort).
 */
public class InscritosView extends JDialog {

    private final InscricoesController controller = new InscricoesController();
    private final DisciplinaController discCtrl   = new DisciplinaController();

    private JComboBox<String> cbDisciplina;
    private JTextField txtProcesso;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblTotal;

    private Lista<Disciplina> listaDisciplinas = new Lista<>();

    public InscritosView(Frame pai) {
        super(pai, "Inscritos por Disciplina", true);
        setSize(800, 520);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarComboDisciplinas();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Filtro ────────────────────────────────────────────────────────
        JPanel pFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pFiltro.setBorder(BorderFactory.createTitledBorder("Filtro"));

        pFiltro.add(new JLabel("Disciplina:"));
        cbDisciplina = new JComboBox<>();
        cbDisciplina.setPreferredSize(new Dimension(280, 26));
        pFiltro.add(cbDisciplina);

        pFiltro.add(new JLabel("Código do Processo:"));
        txtProcesso = new JTextField(12);
        pFiltro.add(txtProcesso);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(41, 128, 185));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pFiltro.add(btnBuscar);

        lblTotal = new JLabel("Total de inscritos: 0");
        lblTotal.setFont(new Font("SansSerif", Font.ITALIC, 12));
        pFiltro.add(lblTotal);

        // ── Tabela ────────────────────────────────────────────────────────
        String[] cols = {"Posição","CPF","Nome","Área","Pontuação"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        // Coluna Posição com tamanho fixo
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getColumnModel().getColumn(4).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder(
            "Professores Inscritos"));

        add(pFiltro, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscar());
    }

    private void carregarComboDisciplinas() {
        try {
            listaDisciplinas = discCtrl.consultarLista();
            cbDisciplina.removeAllItems();
            for (int i = 0; i < listaDisciplinas.getTamanho(); i++) {
                Disciplina d = listaDisciplinas.obter(i);
                cbDisciplina.addItem(d.getCodigo() + " - " + d.getNome());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar disciplinas: " + ex.getMessage());
        }
    }

    private void buscar() {
        try {
            int idxDisc = cbDisciplina.getSelectedIndex();
            if (idxDisc < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma disciplina.");
                return;
            }
            String proc = txtProcesso.getText().trim();
            if (proc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe o código do processo.");
                return;
            }

            int codDisc = listaDisciplinas.obter(idxDisc).getCodigo();
            Lista<Professor> lista = controller.consultarInscritosPorDisciplina(codDisc, proc);

            modeloTabela.setRowCount(0);
            for (int i = 0; i < lista.getTamanho(); i++) {
                Professor p = lista.obter(i);
                modeloTabela.addRow(new Object[]{i + 1, p.getCpf(), p.getNome(), p.getArea(), p.getPontos()});
            }
            lblTotal.setText("Total de inscritos: " + lista.getTamanho());

            if (lista.getTamanho() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Nenhum inscrito encontrado para esta disciplina/processo.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
