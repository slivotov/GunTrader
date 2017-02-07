package ru.kupchagagroup.parser;

import ru.kupchagagroup.domain.Offer;

import java.util.List;
import java.util.Set;

public interface OffersParser {
    List<Offer> parse(String html, ProfitOfferChecker profitOfferChecker);

    Set<Offer> parseNewPage(String html, ProfitOfferChecker profitOfferChecker);
}
