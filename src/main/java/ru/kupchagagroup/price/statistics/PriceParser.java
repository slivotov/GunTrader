package ru.kupchagagroup.price.statistics;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.kupchagagroup.domain.Item;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class PriceParser {

    HashMap<Item, Double> getPagePrices(String pricesPage) {
        pricesPage = getParseableXml(pricesPage);
        HashMap<Item, Double> result = new HashMap<>();
        try {
            //Document doc = Jsoup.parse(pricesPage);
            //Elements itemStatistics = doc.getElementsByTag("tr");
            //for (Element itemElement : itemStatistics) {
            //    String nameAndQuality = itemElement.child(0).text();
            //    Item item = getItemFromString(nameAndQuality);
            //    String avgPriceString = itemElement.child(2).text();
            //    Double avgPrice = Double.parseDouble(avgPriceString);
            //    result.put(item,avgPrice);
            //}
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(pricesPage.getBytes()));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("tr");
            for (int i = 0; i < nList.getLength(); i++) {
                Node itemNode = nList.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElem = (Element) itemNode;
                    NodeList tr = itemElem.getElementsByTagName("td");
                    String nameAndQuality = tr.item(0).getTextContent();
                    Item item = getItemFromString(nameAndQuality);
                    String avgPriceString = tr.item(2).getTextContent();
                    avgPriceString = avgPriceString.replaceAll(",", "");
                    Double avgPrice = Double.parseDouble(avgPriceString);
                    result.put(item, avgPrice);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception during price page parsing. Probably format of document was " + "changed?", e);
        }
        return result;
    }

    private String getParseableXml(String testHtmlString) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" " + "standalone=\"yes\"?><xml>" + testHtmlString + "</xml>";
    }

    private Item getItemFromString(String itemString) {
        String startOfQualityDeclaration = " (";
        int endItemNamePosition = itemString.indexOf(startOfQualityDeclaration);
        if (endItemNamePosition > 0) {
            int beginIndex = endItemNamePosition + startOfQualityDeclaration.length();
            String quality = itemString.substring(beginIndex, itemString.indexOf(")", beginIndex));
            String name = itemString.substring(0, endItemNamePosition);
            return new Item(name, quality);
        } else {
            return new Item(itemString, "");
        }
    }

}
