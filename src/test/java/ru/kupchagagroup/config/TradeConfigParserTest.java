package ru.kupchagagroup.config;

import org.junit.Assert;
import org.junit.Test;
import ru.kupchagagroup.config.external.Blacklist;
import ru.kupchagagroup.config.external.TradeConfig;
import ru.kupchagagroup.config.external.Whitelist;
import ru.kupchagagroup.config.external.WhitelistItem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TradeConfigParserTest {
    @Test
    public void testUnmarshaling() throws Exception {
        File file = new File("src/test/resources/testTradeConfig.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(TradeConfig.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        TradeConfig actualConfig = (TradeConfig) jaxbUnmarshaller.unmarshal(file);

        TradeConfig expectedConfig = new TradeConfig();
        expectedConfig.setMinDiscount(50);
        expectedConfig.setMaxPrice(1.7);
        expectedConfig.setLogin("vovan");
        expectedConfig.setPassword("bozan");
        expectedConfig.setMinPrice(0.1);
        Set<WhitelistItem> whitelist = new HashSet<>();
        whitelist.add(new WhitelistItem("★ Flip Knife | Boreal Forest", "Field-Tested", 10.0));
        whitelist.add(new WhitelistItem("StatTrak™ M4A1-S | Guardian", "Factory New", 5.5));
        whitelist.add(new WhitelistItem("StatTrak™ Puha | Guardian", null, 2.2));
        expectedConfig.setWhitelist(new Whitelist(whitelist));

        ArrayList<String> blacklist = new ArrayList<>();
        blacklist.add("M 16 special edition");
        blacklist.add("super mega knife");
        expectedConfig.setBlacklist(new Blacklist(blacklist));


        Assert.assertEquals(expectedConfig, actualConfig);
        Assert.assertEquals(expectedConfig.getLogin(), actualConfig.getLogin());
        Assert.assertEquals(expectedConfig.getPassword(), actualConfig.getPassword());
        Assert.assertEquals(expectedConfig.getMinDiscount(), actualConfig.getMinDiscount());
        Assert.assertEquals(expectedConfig.getMinPrice(), actualConfig.getMinPrice(), 0.0001);
        Assert.assertEquals(expectedConfig.getMaxPrice(), actualConfig.getMaxPrice(), 0.0001);
        Assert.assertEquals(expectedConfig.getBlacklist(), actualConfig.getBlacklist());
        Assert.assertEquals(expectedConfig.getWhitelist(), actualConfig.getWhitelist());
        Assert.assertEquals(actualConfig.getWhitelist().getWhitelistItems().size(),
                expectedConfig.getWhitelist().getWhitelistItems().size());
        Assert.assertTrue(actualConfig.getWhitelist().getWhitelistItems()
                .containsAll(expectedConfig.getWhitelist().getWhitelistItems()));
    }
}
