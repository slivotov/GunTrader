package ru.forceofshit;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.forceofshit.domain.Offer;
import ru.forceofshit.parser.JsoupOffersParser;
import ru.forceofshit.parser.OffersParser;
import ru.forceofshit.parser.ProfitOfferChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JsoupOffersParserTest {
    @Test
    public void testParse() throws Exception {
        File testHtml = new File("src/test/resources/extractedPageV3.html");
        String testHtmlString = FileUtils.readFileToString(testHtml);
        assertEquals("753", HeadersUtil.getSteamId(testHtmlString));
        OffersParser offersParser = new JsoupOffersParser();
        ProfitOfferChecker alwaysProfitChecker = offer -> true;
        Set<Offer> offerSet = offersParser.parseNewPage(testHtmlString, alwaysProfitChecker);
        List<Offer> offers = new ArrayList<>(offerSet);
        assertEquals(20, offers.size());
        Offer firstOffer = offers.get(0);
        assertEquals(33, firstOffer.getDiscount());
        assertEquals("AUG | Contractor", firstOffer.getName());
        assertEquals("Field-Tested", firstOffer.getQuality());
        assertEquals(0.02, firstOffer.getPrice(), 0.01d);
        assertEquals("93316170, 730, 2", firstOffer.getAddToCartId());
        Offer secondOffer = offers.get(16);
        assertEquals(33, secondOffer.getDiscount());
        assertEquals("Minimal Wear", secondOffer.getQuality());
        assertEquals(0.02, secondOffer.getPrice(), 0.01d);
        assertEquals("93316169, 730, 2", secondOffer.getAddToCartId());
    }

    @Test
    public void testNewParse() throws Exception {
        File testHtml = new File("src/test/resources/extractedPageV3.html");
        String testHtmlString = FileUtils.readFileToString(testHtml);
        assertEquals("753", HeadersUtil.getSteamId(testHtmlString));
        OffersParser offersParser = new JsoupOffersParser();
        ProfitOfferChecker alwaysProfitChecker = offer -> true;
        Set<Offer> offers = offersParser.parseNewPage(testHtmlString, alwaysProfitChecker);
        assertEquals(20, offers.size());
        Offer offer1 = new Offer("Glove Case", "", 44, 0.05, "93316177, 730, 5");
        Assert.assertTrue(offers.contains(offer1));

        Offer offer2 = new Offer("SCAR-20 | Contractor", "Minimal Wear", 33, 0.02, "93316169, 730, 2");
        Assert.assertTrue(offers.contains(offer2));
    }
}
