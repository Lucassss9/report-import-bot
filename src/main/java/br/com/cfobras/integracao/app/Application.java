package br.com.cfobras.integracao.app;

import br.com.cfobras.integracao.driver.DriverFactory;
import br.com.cfobras.integracao.login.CFObrasLogin;
import br.com.cfobras.integracao.login.SiengeLogin;
import br.com.cfobras.integracao.menu.SiengeMenu;
import br.com.cfobras.integracao.pages.CFObrasSaldosPage;
import br.com.cfobras.integracao.pages.SaldoContratosPage;
import br.com.cfobras.integracao.pages.SaldoPedidosPage;
import br.com.cfobras.integracao.utils.FileHelper;
import br.com.cfobras.integracao.utils.RelatorioFinder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static WebDriver driver;

    public static void main(String[] args) {
        System.out.println("=== INICIANDO ROBÔ CF OBRAS (ETAPA 1: GERAR RELATÓRIOS NO SIENGE) ===");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (driver != null) {
                try { driver.quit(); } catch (Exception ignored) {}
            }
        }));

        try {
            driver = DriverFactory.create();
            FileHelper.limparPastaDownloads();

            String[] listaObras = {"4372", "3550", "2996"};

            new SiengeLogin(driver).login();
            SiengeMenu menu = new SiengeMenu(driver);

            System.out.println("\n>>> INICIANDO RELATÓRIOS DE CONTRATOS <<<");
            menu.acessarSaldoDeContratos();
            SaldoContratosPage pageContratos = new SaldoContratosPage(driver);

            for (String obra : listaObras) {
                processarObraContratos(driver, pageContratos, obra);
            }

            System.out.println("\n>>> INICIANDO RELATÓRIOS DE PEDIDOS <<<");
            menu.acessarSaldoDePedidos();
            SaldoPedidosPage pagePedidos = new SaldoPedidosPage(driver);

            for (String obra : listaObras) {
                processarObraPedidos(driver, pagePedidos, obra);
            }

            System.out.println("\n================================================");
            System.out.println("RELATÓRIOS GERADOS COM SUCESSO (PRONTOS PARA IMPORTAÇÃO NO CF OBRAS)");

            RelatorioFinder relatorioFinder = new RelatorioFinder();
            List<File> relatorios = new ArrayList<>();
            relatorios = relatorioFinder.listarRelatoriosParaImportar();
            System.out.println("Total de Relatorios: " + relatorios.size());
            for (File arquivo : relatorios) {
                System.out.println("Relatorio: " + arquivo.getName() + " || Caminho: " +  arquivo.getAbsolutePath());
            }

            new CFObrasLogin(driver).login();
            new CFObrasSaldosPage(driver).importarTodosRelatorios();

        } catch (Exception e) {
            System.out.println("\n[ERRO GERAL] " + e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            if (driver != null) try { driver.quit(); } catch (Exception ignored) {}
        }
    }

    private static void processarObraContratos(WebDriver driver, SaldoContratosPage page, String obra) throws InterruptedException {
        boolean sucesso = false;
        int tentativas = 1;

        while (!sucesso && tentativas <= 3) {
            try {
                System.out.println("--- Contratos: Obra " + obra + " (Tentativa " + tentativas + ") ---");

                if (tentativas > 1) {
                    driver.navigate().refresh();
                    Thread.sleep(2000);
                }

                page.aguardarTelaPronta();
                page.preencherObra(obra);
                page.preencherDatas();
                page.desmarcarContratosAutorizados();
                page.gerarRelatorioEFecharAba();

                FileHelper.renomearUltimoArquivoBaixado("Relatorio_Contratos_" + obra);
                sucesso = true;

            } catch (WebDriverException e) {
                System.err.println("[ERRO CONTRATOS] " + e.getMessage());
                tentativas++;
            }

            Thread.sleep(500);
        }

        if (!sucesso) {
            throw new RuntimeException("Falhou ao gerar relatório de Contratos da obra " + obra);
        }
    }

    private static void processarObraPedidos(WebDriver driver, SaldoPedidosPage page, String obra) throws InterruptedException {
        boolean sucesso = false;
        int tentativas = 1;

        while (!sucesso && tentativas <= 3) {
            try {
                System.out.println("--- Pedidos: Obra " + obra + " (Tentativa " + tentativas + ") ---");

                if (tentativas > 1) {
                    driver.navigate().refresh();
                    Thread.sleep(2000);
                }

                page.aguardarTelaPronta();
                page.preencherObra(obra);
                page.preencherDatas();
                page.gerarRelatorioEFecharAba();

                FileHelper.renomearUltimoArquivoBaixado("Relatorio_Pedidos_" + obra);
                sucesso = true;

            } catch (WebDriverException e) {
                System.err.println("[ERRO PEDIDOS] " + e.getMessage());
                tentativas++;
            }

            Thread.sleep(500);
        }

        if (!sucesso) {
            throw new RuntimeException("Falhou ao gerar relatório de Pedidos da obra " + obra);
        }
    }
}
