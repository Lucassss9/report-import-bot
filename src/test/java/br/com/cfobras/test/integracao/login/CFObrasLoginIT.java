package br.com.cfobras.test.integracao.login;

import br.com.cfobras.integracao.driver.DriverFactory;
import br.com.cfobras.integracao.login.CFObrasLogin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CFObrasLoginIT {

    private WebDriver driver;

    @Test
    void deveFazerLoginNoCfObras() {
        driver = DriverFactory.create();

        CFObrasLogin login = new CFObrasLogin(driver);
        login.login();

        boolean estaLogado = driver.findElements(By.id("btnLogoutHome")).size() > 0;
        Assertions.assertTrue(estaLogado, "Era esperado estar logado (btnLogoutHome visível).");
    }

}
