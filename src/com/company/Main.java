package com.company;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.company.XmlParseUtils.parseTrainScheduleXml;

public class Main {
    private static final String XML_PATH = "train-schedule.xml";
    private static final String TODAYS_DATE = "19.12.2013"; // from example (train-schedule.xml)

    private static final String START_TIME_RANGE = "15:00"; // task condition
    private static final String END_TIME_RANGE = "19:00";   // task condition

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");// format for time

    public static void main(String[] args) throws ParseException {
        Date startTime = timeFormat.parse(START_TIME_RANGE);
        Date endTime = timeFormat.parse(END_TIME_RANGE);


        List<Train> trainsList = parseTrainScheduleXml(XML_PATH);

        printAllTrains(trainsList);
        printTrainsInTimeRange(trainsList, startTime, endTime);
    }

    private static void printAllTrains(List<Train> trainsList) {
        System.out.println("all trains:");
        System.out.println("--------------");
        for (Train train : trainsList) {
            System.out.println(train.toString());
        }
        System.out.println();
    }

    private static void printTrainsInTimeRange(List<Train> trainsList, Date startTime, Date endTime)
            throws ParseException {
        System.out.println(String.format("trains from %s %s to %s %s",
                TODAYS_DATE, START_TIME_RANGE, TODAYS_DATE, END_TIME_RANGE));
        System.out.println("--------------");
        for (Train train : trainsList) {
            if (train.getDepartureDate().equals(TODAYS_DATE)) {
                // to compare time we need to transform it to date 1st
                Date trainDepartureTime = timeFormat.parse(train.getDepartureTime());
                if (trainDepartureTime.after(startTime) && trainDepartureTime.before(endTime)) {
                    System.out.println(train.toString());
                }
            }
        }
    }
}
