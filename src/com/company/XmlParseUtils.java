package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jdk.nashorn.internal.runtime.ParserException;

/**
 * Created by arahis on 4/16/17.
 */
public class XmlParseUtils {
    //xml tags
    private static final String TRAIN_NODE_TAG = "train";
    private static final String ID_ATTRIBUTE = "id";
    private static final String DEPARTURE_CITY_TAG = "from";
    private static final String ARRIVAL_CITY_TAG = "to";
    private static final String DEPARTURE_DATE_TAG = "date";
    private static final String DEPARTURE_TIME_TAG = "departure";

    public static List<Train> parseTrainScheduleXml(String xmlPath) {
        List<Train> trainsList = new ArrayList<>();

        Document doc = parseXml(xmlPath);

        NodeList trainsNL = doc.getElementsByTagName(TRAIN_NODE_TAG);
        for (int i = 0; i < trainsNL.getLength(); i++) {
            Node trainNode = trainsNL.item(i);
            if (trainNode.getNodeType() == Node.ELEMENT_NODE) {
                Element trainE = (Element) trainNode;
                trainsList.add(parseTrainElement(trainE));
            }
        }
        return trainsList;
    }

    @SuppressWarnings("unused") //part of task
    public static void addTrainToTrainScheduleXml(Train train, String xmlPath) {
        Document doc = parseXml(xmlPath);

        createTrainElementInDoc(train, doc);
        writeChangesToXml(doc, xmlPath);
    }

    private static void writeChangesToXml(Document doc, String xmlPath) {
        try {
            DOMSource source = new DOMSource(doc);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(xmlPath);
            transformer.transform(source, result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Document parseXml(String xmlPath) {
        Document doc = null;
        try {
            File trainSchedule = new File(xmlPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(trainSchedule);
            doc.getDocumentElement().normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (doc == null) throw new ParserException("cannot parse: \"" + xmlPath + "\"");

        return doc;
    }

    private static Train parseTrainElement(Element trainE) {
        Train train = new Train();

        int trainId = Integer.parseInt(trainE.getAttribute(ID_ATTRIBUTE));
        String departure_city = trainE.getElementsByTagName(DEPARTURE_CITY_TAG)
                .item(0).getTextContent();
        String arrival_city = trainE.getElementsByTagName(ARRIVAL_CITY_TAG)
                .item(0).getTextContent();
        String departure_date = trainE.getElementsByTagName(DEPARTURE_DATE_TAG)
                .item(0).getTextContent();
        String departure_time = trainE.getElementsByTagName(DEPARTURE_TIME_TAG)
                .item(0).getTextContent();

        train.setTrainId(trainId);
        train.setDepartureCity(departure_city);
        train.setArrivalCity(arrival_city);
        train.setDepartureDate(departure_date);
        train.setDepartureTime(departure_time);

        return train;
    }

    private static void createTrainElementInDoc(Train train, Document doc) {
        Element root = doc.getDocumentElement();
        Element newTrainE = doc.createElement(TRAIN_NODE_TAG);
        Element departureCity = doc.createElement(DEPARTURE_CITY_TAG);
        Element arrivalCity = doc.createElement(ARRIVAL_CITY_TAG);
        Element departureDate = doc.createElement(DEPARTURE_DATE_TAG);
        Element departureTime = doc.createElement(DEPARTURE_TIME_TAG);

        departureCity.appendChild(doc.createTextNode(train.getDepartureCity()));
        arrivalCity.appendChild(doc.createTextNode(train.getArrivalCity()));
        departureDate.appendChild(doc.createTextNode(train.getDepartureDate()));
        departureTime.appendChild(doc.createTextNode(train.getDepartureTime()));

        newTrainE.setAttribute(ID_ATTRIBUTE, Integer.toString(train.getTrainId()));
        newTrainE.appendChild(departureCity);
        newTrainE.appendChild(arrivalCity);
        newTrainE.appendChild(departureDate);
        newTrainE.appendChild(departureTime);

        root.appendChild(newTrainE);
    }
}
