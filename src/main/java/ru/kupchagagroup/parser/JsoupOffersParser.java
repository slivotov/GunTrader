package ru.kupchagagroup.parser;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.kupchagagroup.domain.Offer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsoupOffersParser implements OffersParser {
    public static final String OFFERS_CONTAINER_TAG_ID = "scroll";

    private static Logger log = Logger.getLogger(JsoupOffersParser.class.getName());

    private int parseCount = 0;
    private long totalParseTime = 0;

    public List<Offer> parse(String html, ProfitOfferChecker profitOfferChecker) {
        long initialTime = System.currentTimeMillis();
        List<Offer> offers = new ArrayList<Offer>();
        Document doc = Jsoup.parse(html);
        Elements discounts = doc.getElementsByAttributeValue("class", "label label-success");
        for (Element discount : discounts) {
            String discountString = discount.text();
            if (StringUtils.isNotEmpty(discountString)) {
                Element offer = discount.parent().parent();
                String offerName = offer.getElementsByAttributeValue("class", "market-name market-link").text();
                String quality = getQuality(offer);
                int discountPercent = Integer.parseInt(discountString.substring(0, discountString.indexOf("%")));
                double price = getPrice(offer);
                int addToCardId = getAddToCartId(offer);
                Offer candidateOffer = new Offer(offerName, quality, discountPercent, price, addToCardId);
                if (profitOfferChecker.isProfitOffer(candidateOffer)) {
                    offers.add(candidateOffer);
                }
            }
        }
        parseCount++;
        totalParseTime = totalParseTime + (System.currentTimeMillis() - initialTime);
        return offers;
    }

    public Set<Offer> parseNewPage(String html, ProfitOfferChecker profitOfferChecker) {
        long initialTime = System.currentTimeMillis();
        Set<Offer> offers = new HashSet<>();
        Document doc = Jsoup.parse(html);
        Elements scrollElements = doc.getElementsByAttributeValue("id", OFFERS_CONTAINER_TAG_ID);
        for (Element scrollElement : scrollElements) {
            for (Element offer : scrollElement.children()) {
                try {
                    String offerName = offer.getElementsByAttributeValue("class", "market-name market-link").text();
                    String quality = getQuality(offer);
                    double price = getPrice(offer);
                    double suggestedPrice = getSuggestedPrice(offer);
                    int discountPercent = 100 - (int) Math.rint((price / suggestedPrice) * 100);
                    int addToCardId = getAddToCartId(offer);
                    Offer candidateOffer = new Offer(offerName, quality, discountPercent, price, addToCardId);
                    if (profitOfferChecker.isProfitOffer(candidateOffer)) {
                        offers.add(candidateOffer);
                    }
                } catch (Exception e) {
                    log.error("Some required tag was not found!", e);
                    log.info("Invalid html : " + html);
                }
            }
        }
        parseCount++;
        totalParseTime = totalParseTime + (System.currentTimeMillis() - initialTime);
        return offers;
    }

    private double getSuggestedPrice(Element offer) {
        String suggestedPrice = offer.getElementsByAttributeValue("class", "suggested-price").iterator().next().text();
        if("No Market Price".equals(suggestedPrice)) {
            return 0.0001d;
        }
        suggestedPrice = suggestedPrice.substring(1);
        suggestedPrice = suggestedPrice.replaceAll(",", "");
        return Double.parseDouble(suggestedPrice);
    }

    private String getQuality(Element offer) {
        Element itemDesc = offer.getElementsByAttributeValue("class", "item-desc").iterator().next();
        return itemDesc.getElementsByAttributeValue("class", "text-muted").iterator().next().text();
    }

    private double getPrice(Element offer) {
        String priceString = offer.getElementsByAttributeValue("class", "item-amount").text();
        String priceWithoutDollarPrefix = priceString.substring(1);
        String priceWithoutColumn = priceWithoutDollarPrefix.replaceAll(",", "");
        return Double.parseDouble(priceWithoutColumn);
    }

    private int getAddToCartId(Element offer) {
        Elements addToCardElements = offer.getElementsByAttributeValue("class", "btn btn-orange");
        int addToCardId = 0;
        for (Element addToCardElement : addToCardElements) {
            if ("Add to Cart".equals(addToCardElement.text())) {
                String onClickValue = addToCardElement.attr("onclick");
                addToCardId = Integer.parseInt(
                        onClickValue.substring(onClickValue.indexOf("(") + 1, onClickValue.length() - 1));
            }
        }
        return addToCardId;
    }

    public double getAverageParseTime() {
        return ((double) totalParseTime / 1000) / parseCount;
    }
}
