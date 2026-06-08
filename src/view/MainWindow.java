package view;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal do sistema Gerenciador de Docentes.
 * Menu de navegação para todas as telas de CRUD e consultas.
 */
public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Gerenciador de Docentes - FATEC ZL");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        construirInterface();
    }

    private void construirInterface() {
        // ── Barra de menu ──────────────────────────────────────────────────
        JMenuBar menuBar = new JMenuBar();

        // Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        JMenuItem itemCursos       = new JMenuItem("Cursos");
        JMenuItem itemDisciplinas  = new JMenuItem("Disciplinas");
        JMenuItem itemProfessores  = new JMenuItem("Professores");
        JMenuItem itemInscricoes   = new JMenuItem("Inscrições");
        menuCadastros.add(itemCursos);
        menuCadastros.add(itemDisciplinas);
        menuCadastros.add(itemProfessores);
        menuCadastros.add(itemInscricoes);

        // Consultas
        JMenu menuConsultas = new JMenu("Consultas");
        JMenuItem itemInscritos    = new JMenuItem("Inscritos por Disciplina");
        JMenuItem itemProcessos    = new JMenuItem("Disciplinas com Processos Abertos");
        menuConsultas.add(itemInscritos);
        menuConsultas.add(itemProcessos);

        menuBar.add(menuCadastros);
        menuBar.add(menuConsultas);
        setJMenuBar(menuBar);

        // ── Painel central ─────────────────────────────────────────────────
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(new Color(240, 245, 255));

        // Logo / título central
        JLabel lblTitulo = new JLabel("GERENCIADOR DE DOCENTES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitulo.setForeground(new Color(30, 80, 160));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(60, 0, 10, 0));

        JLabel lblSubTitulo = new JLabel("FATEC Zona Leste  •  Estrutura de Dados", SwingConstants.CENTER);
        lblSubTitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubTitulo.setForeground(Color.GRAY);

        JPanel painelTopo = new JPanel(new GridLayout(2, 1));
        painelTopo.setOpaque(false);
        painelTopo.add(lblTitulo);
        painelTopo.add(lblSubTitulo);

        // Botões de atalho
        JPanel painelBotoes = new JPanel(new GridLayout(2, 3, 16, 16));
        painelBotoes.setOpaque(false);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(30, 80, 60, 80));

        JButton btnCursos      = criarBotao("Cursos",       new Color(30, 80, 160));
        JButton btnDisciplinas = criarBotao("Disciplinas",  new Color(30, 80, 160));
        JButton btnProfessores = criarBotao("Professores",  new Color(30, 80, 160));
        JButton btnInscricoes  = criarBotao("Inscrições",   new Color(30, 80, 160));
        JButton btnInscritos   = criarBotao("Inscritos p/ Disciplina", new Color(30, 80, 160));
        JButton btnProcessos   = criarBotao("Processos Abertos",       new Color(30, 80, 160));

        painelBotoes.add(btnCursos);
        painelBotoes.add(btnDisciplinas);
        painelBotoes.add(btnProfessores);
        painelBotoes.add(btnInscricoes);
        painelBotoes.add(btnInscritos);
        painelBotoes.add(btnProcessos);

        painelCentral.add(painelTopo, BorderLayout.NORTH);
        painelCentral.add(painelBotoes, BorderLayout.CENTER);
        add(painelCentral);

        // ── Ações ──────────────────────────────────────────────────────────
        itemCursos.addActionListener(e -> abrirCursos());
        btnCursos.addActionListener(e -> abrirCursos());

        itemDisciplinas.addActionListener(e -> abrirDisciplinas());
        btnDisciplinas.addActionListener(e -> abrirDisciplinas());

        itemProfessores.addActionListener(e -> abrirProfessores());
        btnProfessores.addActionListener(e -> abrirProfessores());

        itemInscricoes.addActionListener(e -> abrirInscricoes());
        btnInscricoes.addActionListener(e -> abrirInscricoes());

        itemInscritos.addActionListener(e -> abrirInscritos());
        btnInscritos.addActionListener(e -> abrirInscritos());

        itemProcessos.addActionListener(e -> abrirProcessos());
        btnProcessos.addActionListener(e -> abrirProcessos());
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void abrirCursos() {
        CoursesView v = new CoursesView(this);
        v.setVisible(true);
    }

    private void abrirDisciplinas() {
        ClassesView v = new ClassesView(this);
        v.setVisible(true);
    }

    private void abrirProfessores() {
        TeachersView v = new TeachersView(this);
        v.setVisible(true);
    }

    private void abrirInscricoes() {
        SubscriptionsView v = new SubscriptionsView(this);
        v.setVisible(true);
    }

    private void abrirInscritos() {
        InscritosView v = new InscritosView(this);
        v.setVisible(true);
    }

    private void abrirProcessos() {
        ProcessosAbertosView v = new ProcessosAbertosView(this);
        v.setVisible(true);
    }
}
