package view;

import controller.CursosController;
import controller.InscricoesController;
import model.Cursos;
import model.Disciplina;
import model.Lista;
import model.TabelaHash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Tela de consulta de todas as disciplinas com processos abertos.
 * Utiliza Tabela de Espalhamento (Hash) com função hash customizada.
 */
public class ProcessosAbertosView extends JDialog {

    private final InscricoesController controller = new InscricoesController();
    private final CursosController      cursosCtrl = new CursosController();

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblInfo;

    public ProcessosAbertosView(Frame pai) {
        super(pai, "Disciplinas com Processos Abertos", true);
        setSize(840, 540);
        setLocationRelativeTo(pai);
        construirInterface();
        carregarDados();
    }

    private void construirInterface() {
        setLayout(new BorderLayout(8, 8));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Cabeçalho ─────────────────────────────────────────────────────
        JPanel pTopo = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("  Disciplinas com Processos Ativos", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(30, 80, 160));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        lblInfo = new JLabel("Carregando...");
        lblInfo.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(new Color(41,128,185));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorderPainted(false);
        btnAtualizar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAtualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        pTopo.add(lblTitulo,  BorderLayout.WEST);
        pTopo.add(lblInfo,    BorderLayout.CENTER);
        pTopo.add(btnAtualizar, BorderLayout.EAST);

        // ── Tabela ────────────────────────────────────────────────────────
        String[] cols = {"Cód.Disciplina","Nome Disciplina","Dia","Horário","Horas/Dia","Curso","Área"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Disciplinas indexadas"));

        add(pTopo,   BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);

        btnAtualizar.addActionListener(e -> carregarDados());
    }

    private void carregarDados() {
        try {
            modeloTabela.setRowCount(0);

            TabelaHash tabHash = controller.carregarTabelaHash();
            Lista<Disciplina> lista = tabHash.listarTodas();

            for (int i = 0; i < lista.getTamanho(); i++) {
                Disciplina d = lista.obter(i);
                Cursos curso = cursosCtrl.buscarPorCodigo(d.getCodigoCurso());
                String nomeCurso = curso != null ? curso.getNome() : "N/A";
                String area      = curso != null ? curso.getAreaConhecimento() : "N/A";

                modeloTabela.addRow(new Object[]{
                    d.getCodigo(), d.getNome(), d.getDiaSemana(),
                    d.getHorarioInicial(), d.getQtdHorasDiarias(),
                    nomeCurso, area
                });
            }

            lblInfo.setText("  " + lista.getTamanho()
                + " disciplina(s) com processo(s) ativo(s)");

            if (lista.getTamanho() == 0) {
                JOptionPane.showMessageDialog(this,
                    "Nenhuma disciplina com processo ativo encontrada.",
                    "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
