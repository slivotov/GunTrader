package ru.kupchagagroup;

import ru.kupchagagroup.config.external.TradeConfig;
import ru.kupchagagroup.config.internal.OpskinHeaders;

import static ru.kupchagagroup.GunTrader.getConfig;

public class RetriverHeaders {

    public static void main(String...args){
        String configFileLocation = "src/main/resources/tradeconfig.xml";
        TradeConfig config = getConfig(configFileLocation);
        OpskinHeaders opskinHeaders = HeadersUtil.initHeaders(config);
        System.out.println("Required headers were sucessfully initialised and persisted");
    }
}
