package com.example.demo.view;

import Service.ShutdownService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Timeroff {

    private static Timer countdownTimer;
    private static int remainingSeconds;
    private static JLabel countdownLabel;

    private final ShutdownService service = new ShutdownService();
    private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    private int selectedSeconds = 0; // tempo selecionado pelos botões
    private JButton selectedButton = null; // botão atualmente selecionado

    private final Color defaultBtnColor = new Color(50, 50, 50);
    private final Color hoverBtnColor = new Color(173, 216, 230); // mesma cor do contador

    public void iniciar() {

        JFrame frame = new JFrame("Timer de Desligamento");
        frame.setSize(600, 450); // um pouco maior para comportar tudo melhor
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(25, 25, 25));
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(35, 35, 35));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel tempoLabel = new JLabel("Selecione ou digite o tempo:");
        tempoLabel.setForeground(Color.WHITE);
        tempoLabel.setFont(new Font("SansSerif", Font.PLAIN, 18)); // maior
        tempoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(tempoLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Campo de input
        JTextField tempoField = new JTextField();
        Dimension inputSize = new Dimension(350, 45); // maior e confortável
        tempoField.setMaximumSize(inputSize);
        tempoField.setPreferredSize(inputSize);
        tempoField.setFont(new Font("SansSerif", Font.PLAIN, 20)); // fonte maior
        tempoField.setAlignmentX(Component.CENTER_ALIGNMENT);
        tempoField.setHorizontalAlignment(JTextField.CENTER);
        tempoField.setToolTipText("Digite no formato 1h 30m ou 1:30");
        mainPanel.add(tempoField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Painel de botões de tempo
        JPanel tempoPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        tempoPanel.setBackground(new Color(35, 35, 35));

        int[] temposSegundos = {
                30 * 60,       // 30 minutos
                60 * 60,       // 1 hora
                90 * 60,       // 1h30
                2 * 60 * 60,   // 2h
                150 * 60,      // 2h30
                3 * 60 * 60,   // 3h
                210 * 60,      // 3h30
                4 * 60 * 60    // 4h
        };
        String[] labels = {
                "30 min", "1h", "1h 30m", "2h",
                "2h 30m", "3h", "3h 30m", "4h"
        };

        for (int i = 0; i < temposSegundos.length; i++) {
            int segundos = temposSegundos[i];
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 16)); // fonte maior que antes
            btn.setBackground(defaultBtnColor);
            btn.setForeground(Color.WHITE);

            // Hover effect
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn != selectedButton) btn.setBackground(hoverBtnColor.darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn != selectedButton) btn.setBackground(defaultBtnColor);
                }
            });

            // Seleção do botão
            btn.addActionListener(e -> {
                selectedSeconds = segundos;

                // Resetar cor do botão anterior
                if (selectedButton != null) {
                    selectedButton.setBackground(defaultBtnColor);
                }
                selectedButton = btn;
                selectedButton.setBackground(hoverBtnColor);

                // Atualiza o contador imediatamente
                int horas = selectedSeconds / 3600;
                int minutos = (selectedSeconds % 3600) / 60;
                int segundosRest = selectedSeconds % 60;
                countdownLabel.setText(String.format("%02d:%02d:%02d", horas, minutos, segundosRest));

                // Preenche o campo de input com o valor selecionado
                tempoField.setText(horas > 0 ? (horas + "h " + minutos + "m") : (minutos + "m"));

                // Se já houver timer rodando, reinicia para o novo tempo
                if (countdownTimer != null && countdownTimer.isRunning()) {
                    remainingSeconds = selectedSeconds;
                }
            });

            tempoPanel.add(btn);
        }

        mainPanel.add(tempoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Botões Agendar / Cancelar
        JButton agendarButton = new JButton("Agendar");
        JButton cancelarButton = new JButton("Cancelar");
        Dimension buttonSize = new Dimension(130, 40); // um pouco maior
        agendarButton.setPreferredSize(buttonSize);
        agendarButton.setFont(new Font("SansSerif", Font.PLAIN, 16)); // fonte maior
        cancelarButton.setPreferredSize(buttonSize);
        cancelarButton.setFont(new Font("SansSerif", Font.PLAIN, 16)); // fonte maior

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(35, 35, 35));
        buttonPanel.add(agendarButton);
        buttonPanel.add(cancelarButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Label do contador
        countdownLabel = new JLabel("00:00:00");
        countdownLabel.setForeground(new Color(173, 216, 230));
        countdownLabel.setFont(new Font("Consolas", Font.BOLD, 80)); // mantém o tamanho
        countdownLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(countdownLabel);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Recupera o último tempo usado
        selectedSeconds = prefs.getInt("ultimoTempo", 0);

        if (selectedSeconds > 0) {
            int horas = selectedSeconds / 3600;
            int minutos = (selectedSeconds % 3600) / 60;
            int segundosRest = selectedSeconds % 60;

            // Atualiza o contador
            countdownLabel.setText(String.format("%02d:%02d:%02d", horas, minutos, segundosRest));

            // Atualiza o campo de input
            tempoField.setText(horas > 0 ? (horas + "h " + minutos + "m") : (minutos + "m"));
        }

        // Ação Agendar
        agendarButton.addActionListener(e -> {
            try {
                String input = tempoField.getText().trim();
                int segundos;
                if (!input.isEmpty()) {
                    segundos = service.converterParaSegundos(input);
                    if (selectedButton != null) {
                        selectedButton.setBackground(defaultBtnColor);
                        selectedButton = null;
                    }
                } else if (selectedSeconds > 0) {
                    segundos = selectedSeconds;
                } else {
                    JOptionPane.showMessageDialog(frame, "Selecione ou digite um tempo antes de agendar.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                prefs.putInt("ultimoTempo", segundos);

                int confirm = JOptionPane.showConfirmDialog(frame, "Confirmar desligamento?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    remainingSeconds = segundos;
                    service.agendar(segundos);

                    countdownTimer = new Timer(1000, ev -> {
                        if (remainingSeconds > 0) {
                            remainingSeconds--;
                            countdownLabel.setText(String.format("%02d:%02d:%02d",
                                    remainingSeconds / 3600,
                                    (remainingSeconds % 3600) / 60,
                                    remainingSeconds % 60));
                        } else {
                            countdownTimer.stop();
                        }
                    });
                    countdownTimer.start();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Digite apenas números e letras no formato correto (ex: 1h 30m ou 1:30).",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Ação Cancelar
        cancelarButton.addActionListener(e -> {
            try {
                service.cancelar();
                if (countdownTimer != null) countdownTimer.stop();
                countdownLabel.setText("Cancelado");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }
}