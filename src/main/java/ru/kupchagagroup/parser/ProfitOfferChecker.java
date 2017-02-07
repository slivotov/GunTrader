package ru.kupchagagroup.parser;

import ru.kupchagagroup.domain.Offer;

public interface ProfitOfferChecker {
    boolean isProfitOffer(Offer offer);
}
