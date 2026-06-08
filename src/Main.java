import view.MainWindow;

import javax.swing.*;

/**
 * Ponto de entrada do sistema Gerenciador de Docentes.
 * FATEC Zona Leste – Estrutura de Dados
 */
public class Main {
    public static void main(String[] args) {
        // Look and Feel do sistema operacional para melhor usabilidade
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Inicializa a interface na thread de eventos do Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            MainWindow janela = new MainWindow();
            janela.setVisible(true);
        });
    }
}
