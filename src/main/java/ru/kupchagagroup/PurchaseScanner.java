package ru.kupchagagroup;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import ru.kupchagagroup.config.external.TradeConfig;
import ru.kupchagagroup.config.internal.OpskinHeaders;
import ru.kupchagagroup.domain.Offer;
import ru.kupchagagroup.heartrbeat.RefreshStatistics;
import ru.kupchagagroup.parser.DefaultProfitOfferChecker;
import ru.kupchagagroup.parser.JsoupOffersParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static ru.kupchagagroup.Utils.NEW_SCAN_PAGE_URL;

public class PurchaseScanner {
    private static Logger log = Logger.getLogger(PurchaseScanner.class.getName());
    private final CookieStore cookieStore = new BasicCookieStore();

    private HttpClient httpClient;
    private OpskinHeaders opskinHeaders;
    private DefaultProfitOfferChecker discountOfferChecker;
    private JsoupOffersParser offersParser;

    public PurchaseScanner(OpskinHeaders opskinHeaders, TradeConfig tradeConfig) {
        this.opskinHeaders = opskinHeaders;
        RequestConfig requestConfig =
                RequestConfig.custom().setConnectionRequestTimeout(30).build();
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCookieStore(cookieStore)
//                .setProxy(new HttpHost("localhost", 8888))
                .build();
        this.offersParser = new JsoupOffersParser();
        this.discountOfferChecker = new DefaultProfitOfferChecker(tradeConfig);
    }

    public void startProfitPurchaseScanning(WebDriver driver) {
        RefreshStatistics statistics = new RefreshStatistics();
        while (true) {
            try {
                String offersPageSource = getPageSource(NEW_SCAN_PAGE_URL, statistics, driver);
                if (!offersPageSource.contains("OPSkins Bot Detection")) {
                    if (offersPageSource
                            .contains("In order to perform a custom search you must first log in. (1003)")) {
                        log.error("Not logged??");
                        System.out.println("Not logged. Try to restart scanning process. ");
                        return;
                    } else {
                        Collection<Offer> offers = offersParser.parseNewPage(offersPageSource, discountOfferChecker);
                        if (offers.size() > 0) {
                            log.trace("new offers found: " + offers);
                            //purchaseExecutor.buyItems(offers, offersPageSource);
                        } else {
                            log.trace("No new offers found");
                        }
                    }
                } else {
                    log.error("Bot detection!");
                    return;
                }
            } catch (IOException e) {
                log.error("IOException happened during scanning or purchasing", e);
                throw new RuntimeException("IOException happened during scanning or purchasing", e);
            }
        }
    }

    private String getPageSource(String scanPageUrl, RefreshStatistics statistics, WebDriver driver) throws IOException {
        long refreshStartTime = System.currentTimeMillis();
        HttpGet get = new HttpGet(scanPageUrl);
        cookieStore.clear();
        setGetHeaders(get, scanPageUrl);
        InputStream content = null;
        String pageSource;
        try {
            log.trace("Sending GET...");

            HttpResponse offersPage = httpClient.execute(get);
            content = offersPage.getEntity().getContent();
            pageSource = IOUtils.toString(content);

        } finally {
            if (content != null) {
                content.close();
            }
            get.releaseConnection();
        }
//        driver.get(scanPageUrl);
//        String pageSourceFromWebDriver = driver.getPageSource();
        statistics.updateRefreshTime(System.currentTimeMillis() - refreshStartTime);
        log.debug((float) (System.currentTimeMillis() - refreshStartTime) / 1000 + " seconds spent on refresh");
        return pageSource;
    }

    private void setGetHeaders(HttpRequestBase post, String referer) {
        //post.setHeader("host", "opskins.com");
        post.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("accept-encoding", "gzip, deflate, br");
        post.setHeader("accept-language", "en-US,en;q=0.5");
        post.setHeader("cache-control", "max-age=0");
        post.setHeader("cookie", opskinHeaders.getCookiesHeaderValue());
        post.setHeader("referer", referer);
        //post.setHeader("upgrade-insecure-requests", "1");
        post.setHeader("user-agent",
                HeadersUtil.USER_AGENT_HEADER_VALUE);
        post.setHeader("connection", "keep-alive");
        post.setHeader("Cache-Control", "max-age=0");
    }
}
