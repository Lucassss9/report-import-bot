package br.com.cfobras.integracao.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RelatorioFinder {
        private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "downloads";

    public List<File> listarRelatoriosParaImportar(){
        File pasta = new File(DOWNLOAD_DIR);

        List<File> relatorios = new ArrayList<>();

        if (!pasta.exists()){
            pasta.mkdirs();
            return relatorios;
        } else{
          File[] arquivos = pasta.listFiles();
          if(arquivos == null){
              return relatorios;
          }
          for (File arquivo : arquivos) {
              if (!arquivo.isDirectory()) {
                  String arquivoName = arquivo.getName();
                  if (arquivoName.endsWith(".crdownload") || arquivoName.endsWith(".tmp")) {
                      continue;
                  } else if (arquivoName.startsWith("Relatorio_Contratos_") || arquivoName.startsWith("Relatorio_Pedidos_")) {
                      relatorios.add(arquivo);
                  }
              }
          }
          relatorios.sort(Comparator.comparing(File::getName));
        }
        return relatorios;
    }
}
