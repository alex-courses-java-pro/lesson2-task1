package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Main {
    private static final String XML_PATH = "train-schedule.xml";

    private static final String TODAYS_DATE = "19.12.2013"; // from example (train-schedule.xml)

    private static final String START_TIME_RANGE = "15:00"; // task condition
    private static final String END_TIME_RANGE = "19:00";   //
    //xml tags
    private static final String TRAINS_NODE_TAG = "trains";
    private static final String TRAIN_NODE_TAG = "train";
    private static final String ID_ATTRIBUTE = "id";
    private static final String DEPARTURE_CITY_TAG = "from";
    private static final String ARRIVAL_CITY_TAG = "to";
    private static final String DEPARTURE_DATE_TAG = "date";
    private static final String DEPARTURE_TIME_TAG = "departure";

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");// format for time
        Date startTime = timeFormat.parse(START_TIME_RANGE);
        Date endTime = timeFormat.parse(END_TIME_RANGE);

        List<Train> trainsList = parseTrainScheduleXml(XML_PATH);

        //print all trains
        System.out.println("all trains:");
        System.out.println("--------------");
        for (Train train : trainsList) {
            System.out.println(train.toString());
        }
        System.out.println();
        //print trains in date range
        System.out.println(String.format("trains from %s %s to %s %s",
                TODAYS_DATE, START_TIME_RANGE, TODAYS_DATE, END_TIME_RANGE));
        System.out.println("--------------");
        for (Train train : trainsList) {
            if (train.getDeparture_date().equals(TODAYS_DATE)) {
                // to compare time we need to transform it to date 1st
                Date trainDeptureTime = timeFormat.parse(train.getDeparture_time());
                if (trainDeptureTime.after(startTime) && trainDeptureTime.before(endTime)) {
                    System.out.println(train.toString());
                }
            }
        }
    }


    private static List<Train> parseTrainScheduleXml(String path) {
        List<Train> trainsList = new ArrayList<Train>();

        try {
            File trainSchedule = new File(XML_PATH);
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
                    train.setTrain_id(trainId);
                    train.setDeparture_city(departure_city);
                    train.setArrival_city(arrival_city);
                    train.setDeparture_date(departure_date);
                    train.setDeparture_time(departure_time);

                    trainsList.add(train);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return trainsList;
    }

    private static void addTrainToTrainScheduleXml(Train train) {
        try {
            File trainSchedule = new File(XML_PATH);
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

            departureCity.appendChild(doc.createTextNode(train.getDeparture_city()));
            arrivalCity.appendChild(doc.createTextNode(train.getArrival_city()));
            departureDate.appendChild(doc.createTextNode(train.getDeparture_date()));
            departureTime.appendChild(doc.createTextNode(train.getDeparture_time()));

            newTrainE.setAttribute(ID_ATTRIBUTE,Integer.toString(train.getTrain_id()));
            newTrainE.appendChild(departureCity);
            newTrainE.appendChild(arrivalCity);
            newTrainE.appendChild(departureDate);
            newTrainE.appendChild(departureTime);

            root.appendChild(newTrainE);

            DOMSource source = new DOMSource(doc);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(XML_PATH);
            transformer.transform(source, result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
