package br.com.cfobras.integracao.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHelper {
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "downloads";

    public static void renomearUltimoArquivoBaixado(String nomeBaseArquivo) {
        File pasta = new File(DOWNLOAD_DIR);
        if (!pasta.exists()) pasta.mkdirs();

        File arquivoBaixado = null;
        long tempoLimite = System.currentTimeMillis() + 30000;

        while (System.currentTimeMillis() < tempoLimite) {
            File[] arquivos = pasta.listFiles();
            if (arquivos != null) {
                for (File f : arquivos) {
                    if (!f.isDirectory() && !f.getName().endsWith(".tmp") && !f.getName().endsWith(".crdownload")) {
                        if (System.currentTimeMillis() - f.lastModified() < 20000) {
                            if (arquivoBaixado == null || f.lastModified() > arquivoBaixado.lastModified()) {
                                arquivoBaixado = f;
                            }
                        }
                    }
                }
            }
            if (arquivoBaixado != null) break;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        if (arquivoBaixado != null) {
            try {
                String nomeOriginal = arquivoBaixado.getName();
                String extensao = nomeOriginal.contains(".") ? nomeOriginal.substring(nomeOriginal.lastIndexOf(".")) : "";

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_yyyyMMdd_HHmmss");
                String dataHora = LocalDateTime.now().format(formatter);

                String novoNomeCompleto = nomeBaseArquivo + dataHora + extensao;

                File novoArquivo = new File(pasta, novoNomeCompleto);
                Files.move(arquivoBaixado.toPath(), novoArquivo.toPath(), StandardCopyOption.REPLACE_EXISTING);

                System.out.println(novoArquivo.getAbsolutePath());
            } catch (Exception ignored) {}
        } else {
            System.out.println("ERRO: O arquivo não apareceu na pasta " + DOWNLOAD_DIR);
        }
    }

    public static void limparPastaDownloads() {
        File pasta = new File(DOWNLOAD_DIR);
        if (!pasta.exists()) {
            pasta.mkdirs();
            return;
        }
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File f : arquivos) {
                try { f.delete(); } catch (Exception ignored) {}
            }
        }
        System.out.println("Pasta de downloads limpa.");
    }
}
