package br.com.cfobras.integracao.menu;

import br.com.cfobras.integracao.utils.FrameHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SiengeMenu {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By INPUT_OBRA = By.id("filter.obra.empreend.cdEmpreendView");

    public SiengeMenu(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void acessarSaldoDeContratos() {
        abrirRelatorio("Saldos de Contratos");
    }

    public void acessarSaldoDePedidos() {
        abrirRelatorio("Saldos de Pedidos");
    }

    private void abrirRelatorio(String nomeLink) {
        int tentativas = 0;

        while (tentativas < 4) {
            tentativas++;

            try {
                abrirFavoritos();

                WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(text(), '" + nomeLink + "')]")
                ));
                link.click();

                boolean ok = FrameHelper.waitUntilFrameContains(driver, INPUT_OBRA, Duration.ofSeconds(25));
                if (ok) return;

                driver.navigate().refresh();
                Thread.sleep(4000);

            } catch (Exception e) {
                try {
                    driver.navigate().refresh();
                    Thread.sleep(4000);
                } catch (Exception ignored) {}
            }
        }

        throw new RuntimeException("Não foi possível abrir a tela '" + nomeLink + "' (input de Obra não apareceu).");
    }

    private void abrirFavoritos() {
        try {
            if (driver.findElements(By.cssSelector("button[aria-label='Favoritos']")).size() > 0) {
                WebElement btnFavoritos = driver.findElement(By.cssSelector("button[aria-label='Favoritos']"));
                btnFavoritos.click();
            }
        } catch (Exception ignored) {}
    }
}
