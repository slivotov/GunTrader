package ru.forceofshit;

import org.apache.log4j.Logger;
import ru.forceofshit.config.external.TradeConfig;
import ru.forceofshit.config.internal.OpskinHeaders;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class GunTrader {

    private static Logger log = Logger.getLogger(GunTrader.class.getName());

    public static void main(String[] args) {
        if (args.length == 0) {
            writeInstruction();
            System.exit(0);
        } else {
            String configFileLocation = args[0];
            TradeConfig config = getConfig(configFileLocation);
            log.info("Using configuration : \n" + config);
                while(true){
                OpskinHeaders opskinHeaders = HeadersUtil.initHeaders(config);
                System.out.println("Required headers were sucessfully initialised and persisted");
                PurchaseScanner purchaseScanner = new PurchaseScanner(opskinHeaders, config);
                purchaseScanner.startProfitPurchaseScanning(HeadersUtil.getDriver());
            }
        }
    }

    private static String getCommand(String[] args) {
        if (args.length >= 2) {
            return args[1];
        }
        return null;
    }

    private static void writeInstruction() {
        System.out.println("Provide following arguments : ");
        System.out.println("1 argument: config file location");
    }

    private static TradeConfig getConfig(String configFileLocation) {
        if (configFileLocation == null) {
            log.error("No config file is provided. Closing program. ");
            System.exit(0);
        }
        File file = new File(configFileLocation);
        if (!file.exists()) {
            log.error("No config file found by provided location : " + configFileLocation);
            System.exit(0);
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TradeConfig.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TradeConfig unmarshaledConfig = (TradeConfig) jaxbUnmarshaller.unmarshal(file);
            if (validateConfig(unmarshaledConfig)) {
                return unmarshaledConfig;
            } else {
                log.error("Config file is inValid : \n" + unmarshaledConfig);
                System.exit(0);
            }
        } catch (JAXBException e) {
            log.error("Config file parsing exception :  : " + configFileLocation, e);
            System.exit(0);
        }
        //unreachable statement
        return null;
    }

    //todo move to xsd validation
    private static boolean validateConfig(TradeConfig tradeConfig) {
        if (tradeConfig.getMinDiscount() <= 0 || tradeConfig.getMinDiscount() >= 100
                || tradeConfig.getMaxPrice() <= 0) {
            return false;
        }
        return true;
    }
}
