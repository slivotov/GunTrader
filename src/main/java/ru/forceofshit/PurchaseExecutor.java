package ru.forceofshit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import ru.forceofshit.config.internal.OpskinHeaders;
import ru.forceofshit.domain.Offer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.forceofshit.HeadersUtil.getHttpClient;

class PurchaseExecutor {
    private static Logger log = Logger.getLogger(PurchaseExecutor.class.getName());

    private OpskinHeaders opskinHeaders;
    private double currentBalance;

    PurchaseExecutor(OpskinHeaders opskinHeaders) {
        this.opskinHeaders = opskinHeaders;
    }

    void buyItems(Collection<Offer> offers, String pageSource) throws IOException {
        for (Offer offer : offers) {
            buyItem(offer, pageSource);
        }
    }

    private void buyItem(Offer offer, String pageSource) throws IOException {
        //updateBalanceAndCheckNeedToContinue(pageSource);
        //if (offer.getPrice() > currentBalance) {
        //    return;
        //}
        if (addToCardItem(offer)) {
            log.info("Item added to card : " + offer);
            //driver.get("https://opskins.com/?loc=shop_checkout");
            if (buyItemFromCart(offer)) {
                log.info("Was it bought or not ? Who knows ?");
            }
        }
    }

    private boolean addToCardItem(Offer offerToBuy) throws IOException {
        HttpPost post = new HttpPost("https://opskins.com/ajax/shop_account.php");
        setHeaders(post);
        post.setEntity(getAddToCartPostBody(offerToBuy.getAddToCartId()));
        int statusCode;
        String responseEntity;
        InputStream entity;
        try {
            HttpResponse response = getHttpClient().execute(post);
            statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity().getContent();
            responseEntity = new String(IOUtils.toByteArray(entity));
        } finally {
            post.releaseConnection();
        }

        if (200 == statusCode) {
            log.info("Offer successfully added to cart : " + offerToBuy);
            return true;
        }
        log.info(statusCode + " statusCode returned. Entity : " + responseEntity);
        return false;
    }

    private boolean buyItemFromCart(Offer offer) throws IOException {
        HttpPost post = new HttpPost("https://opskins.com/ajax/shop_buy_item.php");
        setHeaders(post);
        post.setEntity(getPurchasePostBody(offer));
        int statusCode;
        String responseEntity;
        InputStream entity = null;
        log.info("Sending http request to buy from card!");
        try {
            HttpResponse response = getHttpClient().execute(post);
            statusCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity().getContent();
            responseEntity = new String(IOUtils.toByteArray(entity));
        } finally {
            if (entity != null) {
                entity.close();
            }
            post.releaseConnection();
        }
        if (200 == statusCode) {
            if (responseEntity.contains("Purchase successful! Your new item is now stored in your")) {
                log.info("Purchase successful! Bought offer. : \n" + offer);
                return true;
            } else {
                log.info("Server returned 200, but item was not bought. Response entity :\n" + responseEntity);
                return false;
            }
        }
        log.info(String.format(
                "Item added to card not bought! Response status code: " + statusCode + ".\n" + " Response " + "body: "
                        + responseEntity, statusCode, responseEntity));
        return false;
    }

    private void setHeaders(HttpRequestBase post) {
        post.setHeader("accept", "*/*");
        post.setHeader("accept-encoding", "gzip, deflate, br");
        post.setHeader("accept-language", "en-US,en;q=0.8,ru;q=0.6");
        post.setHeader("cookie", opskinHeaders.getCookiesHeaderValue());
        post.setHeader("origin", "https://opskins.com");
        post.setHeader("referer", Utils.NEW_SCAN_PAGE_URL);
        post.setHeader("user-agent",
                HeadersUtil.USER_AGENT_HEADER_VALUE);
        post.setHeader("x-csrf", opskinHeaders.getXcsrHeaderValue());
        post.setHeader("x-requested-with", "XMLHttpRequest");
        post.setHeader("x-steamid", opskinHeaders.getSteamId());
    }

    private HttpEntity getPurchasePostBody(Offer offer) throws UnsupportedEncodingException {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("accept_tos", "1"));
        urlParameters.add(new BasicNameValuePair("action", "buy"));
        urlParameters.add(new BasicNameValuePair("hidden_bal", "0"));
        urlParameters.add(new BasicNameValuePair("total", String.valueOf((int) offer.getPrice() * 100)));
        urlParameters.add(new BasicNameValuePair("type", "2"));
        return new UrlEncodedFormEntity(urlParameters);
    }

    private HttpEntity getAddToCartPostBody(String addToCartId) throws UnsupportedEncodingException {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("type", "cart"));
        urlParameters.add(new BasicNameValuePair("param", "add"));
        urlParameters.add(new BasicNameValuePair("id", addToCartId));
        return new UrlEncodedFormEntity(urlParameters);
    }

    private void updateBalanceAndCheckNeedToContinue(String pageSource) {
        currentBalance = getBalance(pageSource);
        if (currentBalance < 0.5) {
            log.error("Not enough fund left. Close program. Current balance: " + currentBalance);
            System.exit(0);
        }
    }

    private static double getBalance(String html) {
        String startOfBalanceDeclaration = "Wallet Balance $";
        int beginIndex = html.indexOf(startOfBalanceDeclaration) + startOfBalanceDeclaration.length();
        String balanceString = html.substring(beginIndex, html.indexOf("<", beginIndex));
        return Double.parseDouble(balanceString);
    }
}
