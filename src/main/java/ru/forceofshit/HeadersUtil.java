package ru.forceofshit;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.forceofshit.config.external.TradeConfig;
import ru.forceofshit.config.internal.OpskinHeaders;

import java.io.*;
import java.util.Properties;

import static ru.forceofshit.Utils.NEW_SCAN_PAGE_URL;
import static ru.forceofshit.Utils.SCAN_PAGE_URL;

public class HeadersUtil {
    static final String USER_AGENT_HEADER_VALUE = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:47.0) Gecko/20100101 Firefox/47.0";

    private static final String COOKIES_PERSIST_PARAM = "cookiesHeaderValue";
    private static final String XCSR_PERSIST_PARAM = "xcsrHeaderValue";
    private static final String STEAM_ID_PPERSIST_PARAM = "steamId";
    private static final String HEADERS_FILE_NAME = "header.values";
    private static Logger log = Logger.getLogger(HeadersUtil.class.getName());
    private static WebDriver driver;
    private static HttpClient httpClient;

    private static final CookieStore cookieStore = new BasicCookieStore();

    static {
        RequestConfig requestConfig =
                RequestConfig.custom().setConnectionRequestTimeout(30).build();
        httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
//                .setProxy(new HttpHost("localhost", 8888))
                .disableCookieManagement()
                .build();
    }

    static WebDriver getDriver() {
        return driver;
    }

    static OpskinHeaders initHeaders(TradeConfig config) {
        driver = initDriver(config);
        String xcsrHeaderValue = getXcsrfHeaderValue(driver);
        String cookiesHeaderValue = getCookieHeaderValue(driver);
        String steamId = getSteamId(driver.getPageSource());
        driver.close();
        OpskinHeaders opskinHeaders = new OpskinHeaders(cookiesHeaderValue, xcsrHeaderValue, steamId);
        persistHeaders(opskinHeaders);
        return opskinHeaders;
    }

    static OpskinHeaders loadHeaders() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            File headersFile = new File(HEADERS_FILE_NAME);
            if (!headersFile.exists()) {
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
        WebElement myDynamicElement = waitAndGetWebElement(driver, ".//img[@alt='OPSkins']");
        long timeInSecondsSpentOnBotProtection = (System.currentTimeMillis() - initialTime) / 1000;
        log.info(timeInSecondsSpentOnBotProtection + " seconds spent on bot protection.");
        if (myDynamicElement != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            driver.findElement(By.className("navbar-signin")).click();
            boolean credentialsArePresent =
                    config.getLogin() != null && !config.getLogin().isEmpty() && config.getPassword() != null && !config
                            .getPassword().isEmpty();
            String winHandBefore = driver.getWindowHandle();
            if (credentialsArePresent) {
                goThroughSteamAuth(config, driver);
            }
            if (credentialsArePresent) {
                System.out.print("Make what Steam asks and press Enter");
            } else {
                System.out.print("Login in browser and press enter");
            }
            try {
                br.readLine();
            } catch (IOException e) {
                log.error("Wtf?!?", e);
                throw new RuntimeException();
            }
            driver.switchTo().window(winHandBefore);
            driver.get(NEW_SCAN_PAGE_URL);
            return driver;
        }
        log.error("Unable to get market offers page!");
        throw new RuntimeException("Unable to get market offers page!");
    }

    private static void goThroughSteamAuth(TradeConfig config, WebDriver driver) {
        driver.findElement(By.xpath(".//img[@src='/images/steam_sign_in_sm.png']")).click();
        switchToSteamLoginPage(driver);
        driver.findElement(By.id("steamAccountName")).sendKeys(config.getLogin());
        driver.findElement(By.id("steamPassword")).sendKeys(config.getPassword());
        driver.findElement(By.id("imageLogin")).click();
        WebElement steamGuard = waitAndGetWebElement(driver, ".//div[@class='newmodal']");
        steamGuard.findElement(By.xpath(".//div[@data-modalstate='submit']")).click();
        steamGuard.findElement(By.id("authcode")).click();
    }

    private static void switchToSteamLoginPage(WebDriver driver) {
        while(driver.getCurrentUrl().equals("https://opskins.com/?loc=login")){
            switchToLastWindow(driver);
        }
    }

    private static WebElement waitAndGetWebElement(WebDriver driver, String xpath) {
        return (new WebDriverWait(driver, 30))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private static void switchToLastWindow(WebDriver driver) {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

    }

    private static String getXcsrfHeaderValue(WebDriver driver) {
        for (Cookie cookie : driver.manage().getCookies()) {
            if ("opskins_csrf_token".equals(cookie.getName())) {
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
        String startOfSteamIdDeclaration = "var g_appid_steam = ";
        int beginIndex = html.indexOf(startOfSteamIdDeclaration) + startOfSteamIdDeclaration.length();
        return html.substring(beginIndex, html.indexOf(";", beginIndex));
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

    public static CookieStore getCookieStore() {
        return cookieStore;
    }
}
