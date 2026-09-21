package cl.iplacex.automatizacion;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integracion (UI) con Selenium. Se ejecuta en fase 'verify' via failsafe.
 */
class BusquedaWebIT {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @Test
    void cargaPaginaYVerificaTitulo() {
        driver.get("https://www.example.com");
        assertTrue(driver.getTitle().toLowerCase().contains("example"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
