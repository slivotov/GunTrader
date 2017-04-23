package ru.forceofshit.parser;

import org.apache.log4j.Logger;
import ru.forceofshit.config.external.TradeConfig;
import ru.forceofshit.config.external.Whitelist;
import ru.forceofshit.config.external.WhitelistItem;
import ru.forceofshit.domain.Offer;

public class DefaultProfitOfferChecker implements ProfitOfferChecker {
    static Logger log = Logger.getLogger(ProfitOfferChecker.class.getName());

    private TradeConfig tradeConfig;

    public DefaultProfitOfferChecker(TradeConfig tradeConfig) {
        this.tradeConfig = tradeConfig;
    }

    @Override
    public boolean isProfitOffer(Offer offer) {
        Whitelist whitelist = tradeConfig.getWhitelist();
        WhitelistItem itemFromWhitelist = null;
        if(whitelist != null) {
            itemFromWhitelist =
                    whitelist.getItemByNameAndQuality(offer.getName(), offer.getQuality());
        }
        if(itemFromWhitelist != null && offer.getPrice() < itemFromWhitelist.getMinPrice()) {
            log.info("Item from whitelist found : " + offer);
            return true;
        }
        boolean maxPriceIsSetInConfig = tradeConfig.getMaxPrice() > 0.0d;
        if (offer.getDiscount() < tradeConfig.getMinDiscount() || offer.getPrice() < tradeConfig.getMinPrice() || (
                maxPriceIsSetInConfig && offer.getPrice() > tradeConfig.getMaxPrice()) || !isValidOfferByBlacklist(
                offer)) {
            return false;
        }
        return true;
    }

    private boolean isValidOfferByBlacklist(Offer offer) {
        return tradeConfig.getBlacklist() == null || tradeConfig.getBlacklist().getBlacklist().isEmpty() || !tradeConfig
                .getBlacklist().getBlacklist().contains(offer.getName());
    }
}
