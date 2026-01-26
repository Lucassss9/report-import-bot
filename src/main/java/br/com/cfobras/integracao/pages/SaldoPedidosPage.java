package br.com.cfobras.integracao.pages;

import br.com.cfobras.integracao.utils.FrameHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

public class SaldoPedidosPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By INPUT_OBRA = By.id("filter.obra.empreend.cdEmpreendView");

    public SaldoPedidosPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void focarNoFrame() {
        boolean ok = FrameHelper.switchToFrameContaining(driver, INPUT_OBRA);
        if (!ok) throw new RuntimeException("Não encontrou o frame que contém o input de Obra (Pedidos).");
    }

    public void aguardarTelaPronta() {
        focarNoFrame();
        wait.until(ExpectedConditions.visibilityOfElementLocated(INPUT_OBRA));
    }

    public void preencherObra(String codigoObra) {
        aguardarTelaPronta();
        WebElement inputObra = wait.until(ExpectedConditions.elementToBeClickable(INPUT_OBRA));
        inputObra.clear();
        inputObra.sendKeys(codigoObra);
        inputObra.sendKeys(Keys.ENTER);
    }

    public void preencherDatas() {
        WebElement ini = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter.dtInicioPedido")));
        ini.clear();
        ini.sendKeys("01012000");

        WebElement fim = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter.dtFimPedido")));
        fim.clear();
        fim.sendKeys("31122500");
    }

    public void gerarRelatorioEFecharAba() {
        String janelaPrincipal = driver.getWindowHandle();

        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.name("btFiltrar")));
        try { btn.click(); }
        catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }

        wait.until(d -> d.getWindowHandles().size() > 1);

        Set<String> janelas = driver.getWindowHandles();
        for (String janela : janelas) {
            if (!janela.equals(janelaPrincipal)) {
                driver.switchTo().window(janela);
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                driver.close();
            }
        }

        driver.switchTo().window(janelaPrincipal);
        driver.switchTo().defaultContent();
    }
}
