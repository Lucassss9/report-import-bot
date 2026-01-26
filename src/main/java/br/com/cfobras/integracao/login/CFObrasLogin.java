package br.com.cfobras.integracao.login;

import br.com.cfobras.integracao.config.AppConfig;
import br.com.cfobras.integracao.utils.KeyboardHelper;
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

        try {
            Thread.sleep(1500);

            String alertPopUp = driver.switchTo().alert().getText();
            System.out.println("Aviso Pop Up: " + alertPopUp);

            if (alertPopUp.contains("não tem permissão")) {
                fecharAlertSeExistir();
            } else {
                System.out.println("Tentado fazer login...");
            }
        } catch (NoAlertPresentException e) {
            System.out.println("Sem alerta...");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        boolean loginSucesso = false;
        int tentativa = 0;

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        while (!loginSucesso && tentativa < 5){
            fecharAlertSeExistir();

            new KeyboardHelper(driver).pressionarEsc();
            new KeyboardHelper(driver).pressionarEsc();

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
                new KeyboardHelper(driver).pressionarEsc();
                System.out.println("Fazendo Login no CF Obras...");

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginEmail")));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginSenha")));

                driver.findElement(By.id("loginEmail")).clear();
                driver.findElement(By.id("loginSenha")).clear();

                driver.findElement(By.id("loginEmail")).sendKeys(appConfig.getEmail());
                driver.findElement(By.id("loginSenha")).sendKeys(appConfig.getSenha());

                driver.findElement(By.className("btn-login")).click();

                new KeyboardHelper(driver).pressionarEsc();

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