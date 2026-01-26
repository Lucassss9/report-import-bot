package br.com.cfobras.integracao.utils;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class KeyboardHelper {

   private final WebDriver driver;

   public KeyboardHelper(WebDriver driver) {
        this.driver = driver;
   }

    public void pressionarEsc() {
       try {
           Actions actions = new Actions(driver);

           actions.sendKeys(Keys.ESCAPE).perform();
           Thread.sleep(150);

           actions.sendKeys(Keys.ESCAPE).perform();
       } catch (Exception e) {
           System.out.println("Falha ao pressionar ESC: \n" + e.getMessage());
       }

    }
}
