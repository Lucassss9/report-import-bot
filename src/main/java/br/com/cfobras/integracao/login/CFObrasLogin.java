package br.com.cfobras.integracao.login;

import br.com.cfobras.integracao.config.AppConfig;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CFObrasLogin {

    AppConfig appConfig = new AppConfig();

    private static final String URL = "https://job.eng.br/index.html";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CFObrasLogin(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void login() {
        driver.get(URL);

        boolean loginSucesso = false;
        int tentativa = 0;

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        while (!loginSucesso && tentativa < 5){
            tentativa++;
            System.out.println(">>> Tentativa de Login: " + tentativa + "...");

            boolean emailExiste = driver.findElements(By.id("loginEmail")).size() > 0;
            boolean senhaExiste = driver.findElements(By.id("loginSenha")).size() > 0;

            boolean estaNaTelaLogin = emailExiste && senhaExiste;

            boolean estaLogado = driver.findElements(By.cssSelector(".home-card[data-modulo=\"saldos\"]")).size() > 0;

            if (estaLogado == true){
                System.out.println("Login efetuado com sucesso!!");
                loginSucesso = true;

                break;
            } else if (estaNaTelaLogin) {
                System.out.println("Fazendo Login no CF Obras...");

                driver.findElement(By.id("loginEmail")).clear();
                driver.findElement(By.id("loginSenha")).clear();

                driver.findElement(By.id("loginEmail")).sendKeys(appConfig.getEmail());
                driver.findElement(By.id("loginSenha")).sendKeys(appConfig.getSenha());

                driver.findElement(By.className("btn-login")).click();

                fecharAlertSeExistir();
                fecharAlertSeExistir();

                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnLogoutHome")));
                    loginSucesso = true;
                    System.out.println("Login efetuado com sucesso!");
                } catch (TimeoutException e) {
                    System.out.println("Login não confirmou a tempo. Tentando novamente...");
                }
            } else {
                pararCarregamento();

                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
                    System.out.println("Tela de login apareceu após parar carregamento!");
                } catch (TimeoutException e) {
                    driver.get(URL);
                }
            }
        }
    }

    private void fecharAlertSeExistir() {
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {}
    }

    private void pararCarregamento() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.stop();");
    }

}