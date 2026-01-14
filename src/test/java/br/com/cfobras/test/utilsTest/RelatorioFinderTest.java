package br.com.cfobras.test.utilsTest;

import br.com.cfobras.integracao.utils.RelatorioFinder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class RelatorioFinderTest {
    @Test
    void deveListarRelariosSemErro(){
        RelatorioFinder relatorioFinder = new RelatorioFinder();

        List<File> relatorios = relatorioFinder.listarRelatoriosParaImportar();

        Assertions.assertNotNull(relatorios);

    }
}
