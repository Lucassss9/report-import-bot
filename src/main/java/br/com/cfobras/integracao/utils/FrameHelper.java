package br.com.cfobras.integracao.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class FrameHelper {

    public static boolean waitUntilFrameContains(WebDriver driver, By locator, Duration timeout) {
        long end = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < end) {
            if (switchToFrameContaining(driver, locator)) return true;
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        }
        driver.switchTo().defaultContent();
        return false;
    }

    public static boolean switchToFrameContaining(WebDriver driver, By locator) {
        driver.switchTo().defaultContent();
        if (exists(driver, locator)) return true;

        List<WebElement> frames = driver.findElements(By.cssSelector("frame, iframe"));
        for (int i = 0; i < frames.size(); i++) {
            driver.switchTo().defaultContent();
            try {
                driver.switchTo().frame(i);
                if (exists(driver, locator)) return true;
                if (switchToNestedFrameContaining(driver, locator)) return true;
            } catch (Exception ignored) {
            }
        }

        driver.switchTo().defaultContent();
        return false;
    }

    private static boolean switchToNestedFrameContaining(WebDriver driver, By locator) {
        List<WebElement> inner = driver.findElements(By.cssSelector("frame, iframe"));
        for (int j = 0; j < inner.size(); j++) {
            try {
                driver.switchTo().frame(j);
                if (exists(driver, locator)) return true;
                if (switchToNestedFrameContaining(driver, locator)) return true;
                driver.switchTo().parentFrame();
            } catch (Exception ignored) {
                try { driver.switchTo().parentFrame(); } catch (Exception ignored2) {}
            }
        }
        return false;
    }

    private static boolean exists(WebDriver driver, By locator) {
        try {
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
