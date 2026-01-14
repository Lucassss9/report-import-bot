package br.com.cfobras.integracao.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final String BOT_PROFILE_PATH = "C:/SiengeBot/Profile";
    private static final String DOWNLOAD_PATH = System.getProperty("user.dir") + File.separator + "downloads";

    public static WebDriver create() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("user-data-dir=" + BOT_PROFILE_PATH);
        options.addArguments("profile-directory=Default");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", DOWNLOAD_PATH);
        prefs.put("download.prompt_for_download", false);
        prefs.put("plugins.always_open_pdf_externally", true);
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);


        return new ChromeDriver(options);
    }
}
