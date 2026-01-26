package br.com.cfobras.integracao.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;

public class CFObrasSaldosPage {
    private static final String URL = "https://job.eng.br/index.html";

    private static final By CARD_SALDOS = By.cssSelector(".home-card[data-modulo=\"saldos\"]");
    private static final By LOGOUT_HOME = By.id("btnLogoutHome");

    private static final By BTN_VOLTAR_HOME = By.id("btnVoltarHome");
    private static final By BTN_IMPORTAR_PEDIDOS = By.id("btnImportarPedidos");
    private static final By BTN_IMPORTAR_CONTRATOS = By.id("btnImportarContratos");

    private static final By IFRAME_SALDOS = By.id("iframe-saldos");
    private static final By TITULO_SALDOS = By.xpath("//h1[contains(., 'SALDOS DE PEDIDOS E CONTRATOS')]");

    private static final By INPUT_ARQUIVO_CONTRATOS = By.id("inputArquivoContratos");
    private static final By INPUT_ARQUIVO_PEDIDOS = By.id("inputArquivoPedidos");

    private static final String DIR = System.getProperty("user.dir") + File.separator + "downloads";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CFObrasSaldosPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void importarTodosRelatorios(){
        importarTodosPedidos();
        importarTodosContratos();
    }

    private boolean estaNaHome() {
        boolean logoutHome = driver.findElements(LOGOUT_HOME).stream().anyMatch(WebElement::isDisplayed);
        boolean homePage = driver.findElements(CARD_SALDOS).stream().anyMatch(WebElement::isDisplayed);

        System.out.println("LOG: Verificando se está na Home: " + homePage);

        return homePage && logoutHome;
    }

    private void irParaSaldos() {
        driver.switchTo().defaultContent();

        System.out.println("LOG: Iniciando processo irParaSaldos...");

        boolean tituloOk = driver.findElements(TITULO_SALDOS).stream().anyMatch(WebElement::isDisplayed);
        boolean iframeOk = driver.findElements(IFRAME_SALDOS).stream().anyMatch(WebElement::isDisplayed);

        boolean saldosExternoOk = tituloOk && iframeOk;

        if(saldosExternoOk) {
            return;
        }

        if(!estaNaHome()) {
           wait.until(ExpectedConditions.visibilityOfElementLocated(BTN_VOLTAR_HOME));
           driver.findElement(BTN_VOLTAR_HOME).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_HOME));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CARD_SALDOS));
        } else {
            wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_HOME));
            wait.until(ExpectedConditions.visibilityOfElementLocated(CARD_SALDOS));
        }
        tentarIrParaSaldos();
    }

    private void abrirImportacaoContratos() {
        System.out.println("LOG: Executando abrirImportacaoContratos()...");    

        System.out.println("LOG: Esperando botão Importar Contratos ficar visível...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(BTN_IMPORTAR_CONTRATOS));

        System.out.println("LOG: Esperando botão Importar Contratos ficar clicável...");
        wait.until(ExpectedConditions.elementToBeClickable(BTN_IMPORTAR_CONTRATOS));

        driver.findElement(BTN_IMPORTAR_CONTRATOS).click();
        System.out.println("LOG: Clique em Importar Contratos OK.");
    }

    private void abrirImportacaoPedidos() {
        System.out.println("LOG: Tentando abrir Importação Pedidos...");

        System.out.println("LOG: Aguardando botão Importar Pedidos ficar visível...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(BTN_IMPORTAR_PEDIDOS));

        System.out.println("LOG: Aguardando botão Importar Pedidos ficar clicável...");
        wait.until(ExpectedConditions.elementToBeClickable(BTN_IMPORTAR_PEDIDOS));

        System.out.println("LOG: Clicando no botão BTN_IMPORTAR_PEDIDOS agora.");
        driver.findElement(BTN_IMPORTAR_PEDIDOS).click();
    }

    private void tentarIrParaSaldos() {
        driver.switchTo().defaultContent();

        wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_HOME));

        System.out.println("LOG: Aguardando CARD_SALDOS para clicar...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(CARD_SALDOS));
        wait.until(ExpectedConditions.elementToBeClickable(CARD_SALDOS));
        System.out.println("LOG: Aguardadno Aparecer...");
        driver.findElement(CARD_SALDOS).click();
        System.out.println("LOG: Card Saldos clicado.");

        aguardarPaginaSaldos();
    }

    private void aguardarPaginaSaldos() {
        driver.switchTo().defaultContent();

        boolean tituloOk = driver.findElements(TITULO_SALDOS).stream().anyMatch(WebElement::isDisplayed);
        boolean iframeOk = driver.findElements(IFRAME_SALDOS).stream().anyMatch(WebElement::isDisplayed);

        boolean saldosExternoOk = tituloOk && iframeOk;

        System.out.println("Titulo Ok: " + tituloOk);
        System.out.println("Iframe Ok: " + iframeOk);

        if (saldosExternoOk) {
            return;
        } else {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TITULO_SALDOS));
            wait.until(ExpectedConditions.visibilityOfElementLocated( IFRAME_SALDOS));
        }
    }

    private void entrarNoIframeSaldos() {
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.presenceOfElementLocated(IFRAME_SALDOS));
        System.out.println("LOG: Entrando no iframe de Saldos...");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(IFRAME_SALDOS));
        System.out.println("LOG: Dentro do iframe de Saldos.");
    }

    private void importarTodosContratos() {
        irParaSaldos();
        entrarNoIframeSaldos();

        abrirImportacaoContratos();

        wait.until(ExpectedConditions.presenceOfElementLocated(INPUT_ARQUIVO_CONTRATOS));

        while (true) {
            File arquivo = pegarProximoRelatorio("Relatorio_Contratos_");
            if (arquivo == null) {
                System.out.println("LOG: Nenhum relatório de CONTRATOS restante pra importar.");
                break;
            }

            System.out.println("LOG: Importando CONTRATOS: " + arquivo.getName());

            driver.findElement(INPUT_ARQUIVO_CONTRATOS).sendKeys(arquivo.getAbsolutePath());

            esperarEFecharAlert();

            boolean apagou = arquivo.delete();
            System.out.println("LOG: Arquivo " + (apagou ? "apagado" : "NÃO apagado") + ": " + arquivo.getName());
        }

        driver.switchTo().defaultContent();
    }

    private void importarTodosPedidos() {
        irParaSaldos();
        entrarNoIframeSaldos();

        abrirImportacaoPedidos();

        wait.until(ExpectedConditions.presenceOfElementLocated(INPUT_ARQUIVO_PEDIDOS));

        while (true) {
            File arquivo = pegarProximoRelatorio("Relatorio_Pedidos_");
            if (arquivo == null) {
                System.out.println("LOG: Nenhum relatório de PEDIDOS restante pra importar.");
                break;
            }

            System.out.println("LOG: Importando PEDIDOS: " + arquivo.getName());

            driver.findElement(INPUT_ARQUIVO_PEDIDOS).sendKeys(arquivo.getAbsolutePath());

            esperarEFecharAlert();

            boolean apagou = arquivo.delete();
            System.out.println("LOG: Arquivo " + (apagou ? "apagado" : "NÃO apagado") + ": " + arquivo.getName());
        }

        driver.switchTo().defaultContent();
    }

    private File pegarProximoRelatorio(String prefixo) {
        File pasta = new File(DIR);
        if (!pasta.exists()) pasta.mkdirs();

        File[] arquivos = pasta.listFiles(f ->
                f.isFile()
                        && !f.getName().endsWith(".crdownload")
                        && !f.getName().endsWith(".tmp")
                        && f.getName().startsWith(prefixo)
                        && (f.getName().endsWith(".xlsx") || f.getName().endsWith(".xls"))
        );

        if (arquivos == null || arquivos.length == 0) return null;

        Arrays.sort(arquivos, Comparator.comparingLong(File::lastModified));
        return arquivos[0];
    }

    private void esperarEFecharAlert() {
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("ALERT: " + alert.getText());
            alert.accept();
        } catch (TimeoutException e) {
            System.out.println("WARN: Nenhum alert apareceu após importar (talvez demorou ou não usa alert).");
        }
    }

}
