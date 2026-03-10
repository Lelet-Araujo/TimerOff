package com.example.demo;

import com.example.demo.view.Timeroff;

import javax.swing.*;

public class BotaoDesligarApplication {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Timeroff().iniciar();
        });
    }
}