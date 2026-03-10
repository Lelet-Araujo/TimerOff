package Service;

import java.io.IOException;

public class ShutdownService {

    public int converterParaSegundos(String tempoInput) {
        if (tempoInput == null || tempoInput.isEmpty()) {
            throw new IllegalArgumentException("Digite um valor válido.");
        }

        tempoInput = tempoInput.trim().toLowerCase();
        int horas = 0;
        int minutos = 0;

        // Formatos "1h 30m" ou "1:30"
        if (tempoInput.contains("h")) {
            String[] partes = tempoInput.split("h");
            horas = Integer.parseInt(partes[0].trim());
            if (partes.length > 1 && !partes[1].trim().isEmpty()) {
                minutos = Integer.parseInt(partes[1].replace("m", "").trim());
            }
        } else if (tempoInput.contains(":")) {
            String[] partes = tempoInput.split(":");
            horas = Integer.parseInt(partes[0].trim());
            minutos = Integer.parseInt(partes[1].trim());
        } else {
            // Apenas minutos
            minutos = Integer.parseInt(tempoInput.trim());
        }

        if (horas < 0 || minutos < 0) throw new IllegalArgumentException("Valores não podem ser negativos.");
        return horas * 3600 + minutos * 60;
    }

    public void agendar(int segundos) throws IOException {
        Runtime.getRuntime().exec("shutdown -s -t " + segundos);
    }

    public void cancelar() throws IOException {
        Runtime.getRuntime().exec("shutdown -a");
    }
}