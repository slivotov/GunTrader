package ru.kupchagagroup;

import org.apache.log4j.Logger;
import ru.kupchagagroup.config.external.TradeConfig;
import ru.kupchagagroup.config.internal.OpskinHeaders;

import static ru.kupchagagroup.GunTrader.getConfig;
import static ru.kupchagagroup.Utils.CONFIG_FILE_LOCATION;

public class RunnerWithSavedGet {

    private static Logger log = Logger.getLogger(RunnerWithSavedGet.class.getName());

    public static void main(String... args) {
        TradeConfig config = getConfig(CONFIG_FILE_LOCATION);
        RunnerWithSavedGet.log.info("Using configuration : \n" + config);
        OpskinHeaders opskinHeaders = HeadersUtil.loadHeaders();
        if (opskinHeaders == null) {
            System.out.println("Perform init before scanning!");
        } else {
            PurchaseScanner purchaseScanner = new PurchaseScanner(opskinHeaders, config);
            purchaseScanner.startProfitPurchaseScanning(null);
        }
    }
}
