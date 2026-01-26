package br.com.cfobras.integracao.login;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SiengeLogin {

    private static final String URL = "https://curyempreendimentos.sienge.com.br/sienge/8/index.html";

    private final WebDriver driver;
    private final WebDriverWait waitLong;

    public SiengeLogin(WebDriver driver) {
        this.driver = driver;
        this.waitLong = new WebDriverWait(driver, Duration.ofSeconds(120));
    }

    public void login() {
        boolean loginSucesso = false;
        int tentativa = 0;

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        while (!loginSucesso && tentativa < 5) {
            tentativa++;
            System.out.println(">>> Tentativa de Login: " + tentativa + "...");

            try {
                abrirPaginaComRecuperacao();

                WebDriverWait waitRapido = new WebDriverWait(driver, Duration.ofSeconds(12));

                clicarSeExistir(waitRapido,
                        By.xpath("//button[contains(., 'Entrar com') and contains(., 'Sienge ID')]"),
                        "Botão 'Entrar com Sienge ID'"
                );

                clicarSeExistir(waitRapido,
                        By.xpath("//*[contains(text(), 'lucas.gabriel@cury.net')]"),
                        "Usuário salvo"
                );

                clicarSeExistir(waitRapido,
                        By.xpath("//a[contains(text(), 'Prosseguir')]"),
                        "Botão 'Prosseguir'"
                );

                System.out.println("-> Verificando acesso...");
                waitLong.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("button[aria-label='Favoritos']")
                ));

                System.out.println("Login Confirmado!");
                loginSucesso = true;

            } catch (Exception e) {
                System.out.println("!!! Falha no login: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                tentarRefreshCurto();
            }
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(300));

        if (!loginSucesso) {
            throw new RuntimeException("Não foi possível logar no Sienge após 5 tentativas.");
        }
    }

    private void abrirPaginaComRecuperacao() {
        try {
            driver.get(URL);
        } catch (TimeoutException e) {
            System.out.println("AVISO: pageLoadTimeout atingido. Refresh...");
            tentarRefreshCurto();
        }

        if (!aguardarPaginaSaudavel(15)) {
            System.out.println("AVISO: Página parece travada/sem elementos. Refresh...");
            tentarRefreshCurto();
        }
    }

    private boolean aguardarPaginaSaudavel(int timeoutSec) {
        long end = System.currentTimeMillis() + timeoutSec * 1000L;

        while (System.currentTimeMillis() < end) {
            try {
                if (driver.findElements(By.cssSelector("button[aria-label='Favoritos']")).size() > 0) return true;

                boolean temLogin =
                        driver.findElements(By.xpath("//button[contains(., 'Entrar com') and contains(., 'Sienge ID')]")).size() > 0
                                || driver.findElements(By.xpath("//*[contains(text(), 'lucas.gabriel@cury.net')]")).size() > 0;

                if (temLogin) return true;

                try {
                    ((JavascriptExecutor) driver).executeScript("return document.readyState");
                } catch (Exception ignored) {}

            } catch (Exception ignored) {}

            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        return false;
    }

    private void clicarSeExistir(WebDriverWait wait, By by, String nome) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
            try {
                el.click();
            } catch (Exception clickErro) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
        } catch (TimeoutException e) {
            System.out.println("[INFO] Não apareceu: " + nome);
        }
    }

    private void tentarRefreshCurto() {
        try {
            driver.navigate().refresh();
            Thread.sleep(2500);
        } catch (Exception ignored) {}
    }
}
