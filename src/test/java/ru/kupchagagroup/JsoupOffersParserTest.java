package ru.kupchagagroup;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.kupchagagroup.domain.Offer;
import ru.kupchagagroup.parser.JsoupOffersParser;
import ru.kupchagagroup.parser.OffersParser;
import ru.kupchagagroup.parser.ProfitOfferChecker;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JsoupOffersParserTest {
    @Test
    public void testParse() throws Exception {
        File testHtml = new File("src/test/resources/extracted_page.html");
        String testHtmlString = FileUtils.readFileToString(testHtml);
        assertEquals("12345", HeadersUtil.getSteamId(testHtmlString));
        OffersParser offersParser = new JsoupOffersParser();
        ProfitOfferChecker alwaysProfitChecker = new ProfitOfferChecker() {
            public boolean isProfitOffer(Offer offer) {
                return true;
            }
        };
        List<Offer> offers = offersParser.parse(testHtmlString, alwaysProfitChecker);
        assertEquals(94, offers.size());
        Offer firstOffer = offers.get(0);
        assertEquals(10, firstOffer.getDiscount());
        assertEquals("★ Shadow Daggers | Boreal Forest", firstOffer.getName());
        assertEquals("Field-Tested", firstOffer.getQuality());
        assertEquals(35d, firstOffer.getPrice(), 0.01d);
        assertEquals(30889656, firstOffer.getAddToCartId());
        Offer secondOffer = offers.get(1);
        assertEquals(52, secondOffer.getDiscount());
        assertEquals("Factory New", secondOffer.getQuality());
        assertEquals(143.99d, secondOffer.getPrice(), 0.01d);
        assertEquals(30889394, secondOffer.getAddToCartId());
    }

    @Test
    public void testNewParse() throws Exception {
        File testHtml = new File("src/test/resources/extractedPageV2.html");
        String testHtmlString = FileUtils.readFileToString(testHtml);
        assertEquals("76561198321141249", HeadersUtil.getSteamId(testHtmlString));
        OffersParser offersParser = new JsoupOffersParser();
        ProfitOfferChecker alwaysProfitChecker = new ProfitOfferChecker() {
            public boolean isProfitOffer(Offer offer) {
                return true;
            }
        };
        Set<Offer> offers = offersParser.parseNewPage(testHtmlString, alwaysProfitChecker);
        assertEquals(20, offers.size());
        Offer karambit = new Offer("★ Karambit | Damascus Steel", "Field-Tested", 24, 155d, 32327436);
        Assert.assertTrue(offers.contains(karambit));

        Offer ddpat = new Offer("AWP | Pink DDPAT", "Minimal Wear", 23, 12.8d, 32327437);
        Assert.assertTrue(offers.contains(ddpat));
    }
}
