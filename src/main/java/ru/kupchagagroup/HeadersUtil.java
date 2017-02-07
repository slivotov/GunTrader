package ru.kupchagagroup;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.kupchagagroup.config.external.TradeConfig;
import ru.kupchagagroup.config.internal.OpskinHeaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import static ru.kupchagagroup.GunTrader.NEW_SCAN_PAGE_URL;
import static ru.kupchagagroup.GunTrader.SCAN_PAGE_URL;

public class HeadersUtil {
    public static final String USER_AGENT_HEADER_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:47.0) Gecko/20100101 Firefox/47.0";

    private static final String COOKIES_PERSIST_PARAM = "cookiesHeaderValue";
    private static final String XCSR_PERSIST_PARAM = "xcsrHeaderValu";
    private static final String STEAM_ID_PPERSIST_PARAM = "steamId";
    private static final String HEADERS_FILE_NAME = "header.values";
    private static Logger log = Logger.getLogger(HeadersUtil.class.getName());

    public static OpskinHeaders initHeaders(TradeConfig config) {
        WebDriver driver = initDriver(config);
        String xcsrHeaderValue = getXcsrfHeaderValue(driver);
        String cookiesHeaderValue = getCookieHeaderValue(driver);
        String steamId = getSteamId(driver.getPageSource());
        driver.close();
        OpskinHeaders opskinHeaders = new OpskinHeaders(cookiesHeaderValue, xcsrHeaderValue, steamId);
        persistHeaders(opskinHeaders);
        return opskinHeaders;
    }

    public static OpskinHeaders loadHeaders() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            File headersFile = new File(HEADERS_FILE_NAME);
            if(!headersFile.exists()) {
                return null;
            }
            input = new FileInputStream(HEADERS_FILE_NAME);

            prop.load(input);
            String cookies = prop.getProperty(COOKIES_PERSIST_PARAM);
            String xcsr = prop.getProperty(XCSR_PERSIST_PARAM);
            String steamId = prop.getProperty(STEAM_ID_PPERSIST_PARAM);
            return new OpskinHeaders(cookies, xcsr, steamId);

        } catch (IOException io) {
            log.error("Exception during loading headers!", io);
            throw new RuntimeException("Exception during loading headers!", io);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Exception during loading headers!", e);
                    throw new RuntimeException("Exception during loading headers!", e);
                }
            }
        }
    }

    private static void persistHeaders(OpskinHeaders opskinHeaders) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(HEADERS_FILE_NAME);

            prop.setProperty(COOKIES_PERSIST_PARAM, opskinHeaders.getCookiesHeaderValue());
            prop.setProperty(XCSR_PERSIST_PARAM, opskinHeaders.getXcsrHeaderValue());
            prop.setProperty(STEAM_ID_PPERSIST_PARAM, opskinHeaders.getSteamId());

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            log.error("Exception during persisting headers!", io);
            throw new RuntimeException("Exception during persisting headers!", io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("Exception during persisting headers!", e);
                    throw new RuntimeException("Exception during persisting headers!", e);
                }
            }

        }
    }

    private static WebDriver initDriver(TradeConfig config) {
        FirefoxProfile ffp = new FirefoxProfile();
        ffp.setPreference("general.useragent.override", HeadersUtil.USER_AGENT_HEADER_VALUE);
        WebDriver driver = new FirefoxDriver(ffp);
        //WebDriver driver = new HtmlUnitDriver(true);
        driver.get(SCAN_PAGE_URL);
        long initialTime = System.currentTimeMillis();
        WebElement myDynamicElement = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='sc_carousel']")));
        long timeInSecondsSpentOnBotProtection = (System.currentTimeMillis() - initialTime) / 1000;
        log.info(timeInSecondsSpentOnBotProtection + " seconds spent on bot protection.");
        if (myDynamicElement != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            driver.findElement(By.className("navbar-bran")).click();
            boolean credentialsArePresent =
                    config.getLogin() != null && !config.getLogin().isEmpty() && config.getPassword() != null && !config
                            .getPassword().isEmpty();
            if (credentialsArePresent) {
                driver.findElement(By.id("steamAccountName")).sendKeys(config.getLogin());
                driver.findElement(By.id("steamPassword")).sendKeys(config.getPassword());
                driver.findElement(By.id("imageLogin")).click();
            }
            if (credentialsArePresent) {
                System.out.print("Enter phone code in browser if needed and press proceed. Press enter");
            } else {
                System.out.print("Login in browser and press enter");
            }
            try {
                br.readLine();
            } catch (IOException e) {
                log.error("Wtf?!?", e);
                throw new RuntimeException();
            }
            driver.get(NEW_SCAN_PAGE_URL);
            return driver;
        }
        log.error("Unable to get market offers page!");
        throw new RuntimeException("Unable to get market offers page!");
    }

    private static String getXcsrfHeaderValue(WebDriver driver) {
        for (Cookie cookie : driver.manage().getCookies()) {
            if ("opskins_csrf".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private static String getCookieHeaderValue(WebDriver driver) {
        //I don't want guava in dependency for such small project
        String result = "";
        boolean isFirst = true;
        for (Cookie cookie : driver.manage().getCookies()) {
            if (isFirst) {
                isFirst = false;
            } else {
                result = result + ";";
            }
            result = result + cookie.getName() + "=" + cookie.getValue();
        }
        return result;
    }

    public static String getSteamId(String html) {
        String startOfSteamIdDeclaration = "var g_SteamID = \"";
        int beginIndex = html.indexOf(startOfSteamIdDeclaration) + startOfSteamIdDeclaration.length();
        return html.substring(beginIndex, html.indexOf("\"", beginIndex));
    }
}