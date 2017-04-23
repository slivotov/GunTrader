package ru.forceofshit.price.statistics;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.forceofshit.domain.Item;

import java.io.File;
import java.util.HashMap;

public class PriceParserTest {
    @Test
    public void testParse() throws Exception {
        File testHtml = new File("src/test/resources/systemAnalystPricesPage.html");
        String testHtmlString = FileUtils.readFileToString(testHtml);
        PriceParser parser = new PriceParser();
        HashMap<Item, Double> pagePrices = parser.getPagePrices(testHtmlString);
        Assert.assertEquals(50, pagePrices.size());
        Item firstItem = new Item("StatTrak™ UMP-45 | Labyrinth", "Minimal Wear");
        Assert.assertTrue(pagePrices.containsKey(firstItem));
        Assert.assertEquals(0.36d, pagePrices.get(firstItem), 0.0001d);

        Item someItemWithoutQuality = new Item("Music Kit | Daniel Sadowski, Total Domination", "");
        Assert.assertTrue(pagePrices.containsKey(someItemWithoutQuality));
        Assert.assertEquals(2.74d, pagePrices.get(someItemWithoutQuality), 0.0001d);


    }

}
