package ru.forceofshit.parser;

import ru.forceofshit.domain.Offer;

public interface ProfitOfferChecker {
    boolean isProfitOffer(Offer offer);
}
