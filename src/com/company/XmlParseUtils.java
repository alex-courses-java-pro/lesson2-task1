package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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


    private static Document parseXml(String xmlPath)
            throws IOException, SAXException, ParserConfigurationException {

        File trainSchedule = new File(xmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(trainSchedule);
        doc.getDocumentElement().normalize();

        return doc;
    }

    public static List<Train> parseTrainScheduleXml(String xmlPath) {
        List<Train> trainsList = new ArrayList<>();

        try {
            File trainSchedule = new File(xmlPath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(trainSchedule);
            doc.getDocumentElement().normalize();

            NodeList trainsNL = doc.getElementsByTagName(TRAIN_NODE_TAG);
            for (int i = 0; i < trainsNL.getLength(); i++) {
                Node trainNode = trainsNL.item(i);
                if (trainNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element trainE = (Element) trainNode;

                    int trainId = Integer.parseInt(trainE.getAttribute(ID_ATTRIBUTE));
                    String departure_city = trainE.getElementsByTagName(DEPARTURE_CITY_TAG)
                            .item(0).getTextContent();
                    String arrival_city = trainE.getElementsByTagName(ARRIVAL_CITY_TAG)
                            .item(0).getTextContent();
                    String departure_date = trainE.getElementsByTagName(DEPARTURE_DATE_TAG)
                            .item(0).getTextContent();
                    String departure_time = trainE.getElementsByTagName(DEPARTURE_TIME_TAG)
                            .item(0).getTextContent();

                    Train train = new Train();
                    train.setTrainId(trainId);
                    train.setDepartureCity(departure_city);
                    train.setArrivalCity(arrival_city);
                    train.setDepartureDate(departure_date);
                    train.setDepartureTime(departure_time);

                    trainsList.add(train);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return trainsList;
    }

    @SuppressWarnings("unused") //part of task
    public static void addTrainToTrainScheduleXml(Train train, String pathToXml) {
        try {
            File trainSchedule = new File(pathToXml);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(trainSchedule);

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

            newTrainE.setAttribute(ID_ATTRIBUTE,Integer.toString(train.getTrainId()));
            newTrainE.appendChild(departureCity);
            newTrainE.appendChild(arrivalCity);
            newTrainE.appendChild(departureDate);
            newTrainE.appendChild(departureTime);

            root.appendChild(newTrainE);

            DOMSource source = new DOMSource(doc);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(pathToXml);
            transformer.transform(source, result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
