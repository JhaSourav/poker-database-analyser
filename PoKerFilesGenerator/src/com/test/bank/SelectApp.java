package com.test.bank;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SelectApp {
    int count = 0;

    /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:F:\\Sourav Freelancing\\Guru\\Alex Holgate\\replay.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * select all rows in the warehouses table
     */
    public void selectAll() {
       // String sql = "SELECT xml_dump FROM hands_1 where sid=26 or sid=27";
        String sql = "SELECT xml_dump FROM hands_1";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                parseXML(rs.getString("xml_dump"));
                count++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Rows Fetched" + count);
    }

    private void parseXML(String xml) {
        DocumentBuilder db = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc = null;
        try {
            doc = db.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element root = doc.getDocumentElement();
        String gameId = root.getAttribute("id");
        String[] startingCards = root.getAttribute("pc").split(",", -2);
        String timeStamp = root.getAttribute("dt");
        Date dt = new Date(Long.parseLong(timeStamp) * 1000);
        SimpleDateFormat sfd = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
        String dateAndTime = sfd.format(dt);
        String stake = root.getAttribute("stake");
        float potSize = Float.parseFloat(root.getAttribute("pot"));
        System.out.println("pot"+potSize);
        float rakeSize = Float.parseFloat(root.getAttribute("rake"));
        System.out.println("rake"+rakeSize);
        if (stake != "") {
            String[] stakeArray = stake.split("/", -2);
            stake = "£" + stakeArray[0] + "/" + "£" + stakeArray[1];
        }

        NodeList itemCdata = doc.getElementsByTagName("title");

        String itemTitle = itemCdata.item(0).getTextContent();
        //String[] cDataArray = itemTitle.split("-",-1);
        String table = "Table "+ itemTitle.substring(3, 35)+" (Real Money)";

        NodeList buttonNode = doc.getElementsByTagName("PS");

        Element buttonEle = (Element) buttonNode.item(0);
        String button="";
        if(buttonEle.getAttribute("dealer").equals("3")) {
            button = "4";
        }
        else if(buttonEle.getAttribute("dealer").equals("4")) {
            button = "6";
        }
        else if(buttonEle.getAttribute("dealer").equals("5")) {
            button = "7";
        }
        else if(buttonEle.getAttribute("dealer").equals("6")) {
            button = "9";
        }
        else
        {
            button=buttonEle.getAttribute("dealer");
        }
        //Element player = doc.getElementById("PS");
        NodeList nPlayers = doc.getElementsByTagName("P");
        int numberOfPlayers = nPlayers.getLength();
        ArrayList<SeatInfo> seatsInfo = new ArrayList<SeatInfo>();

        for (int i = 0; i < numberOfPlayers; i++) {
            Element ele = (Element) nPlayers.item(i);
            String seatNumber = ele.getAttribute("s");
            String playerName = ele.getAttribute("name");
            String stakSize = ele.getAttribute("chips");
            String cards = ele.getAttribute("c");
            if(stakSize.equals("0")) {
                stakSize="100";
            }
            if(!cards.equals("")) {
                System.out.println("cards: "+cards);
                seatsInfo.add(new SeatInfo(seatNumber, playerName, stakSize, cards));
            }
        }
        /* Dynamic Action code*/
        NodeList actionSeq = doc.getElementsByTagName("A");
        int numberSequesnce = actionSeq.getLength();
        ArrayList<DynamicSequence> sequenceList = new ArrayList<DynamicSequence>(numberSequesnce);
        for (int i = 0; i < numberSequesnce; i++) {
            Element ele = (Element) actionSeq.item(i);
            String seqNumber = ele.getAttribute("seq");
            String seqType = ele.getAttribute("type");
            String seat = ele.getAttribute("s");
            String volume = ele.getAttribute("v");
            String low = ele.getAttribute("low");
            String pot = ele.getAttribute("pot");

            if (pot == null && low == null) {
                sequenceList.add(new DynamicSequence(seqNumber, seqType, seat, volume));
            } else if (pot == null && low == null && volume == null) {
                sequenceList.add(new DynamicSequence(seqNumber, seqType, seat));
            } else if (pot == null && low == null && volume == null && seat == null) {
                sequenceList.add(new DynamicSequence(seqNumber, seqType));
            } else {
                sequenceList.add(new DynamicSequence(seqNumber, seqType, seat, volume, low, pot));
            }

        }
        StringBuilder str = new StringBuilder("");
        str.append("#Game No : " + gameId);
        str.append(System.getProperty("line.separator"));
        str.append("***** 888poker Hand History for Game " + gameId + " *****");
        str.append(System.getProperty("line.separator"));
        str.append(stake + " Blinds No Limit Holdem - *** " + dateAndTime);
        str.append(System.getProperty("line.separator"));

        str.append(table);
        str.append(System.getProperty("line.separator"));

        str.append("Seat " + button + " is the button");
        str.append(System.getProperty("line.separator"));
        str.append("Total number of players : " + seatsInfo.size());
        str.append(System.getProperty("line.separator"));
        for (int i = 0; i < seatsInfo.size(); i++) {
            str.append("Seat " + seatsInfo.get(i).getSeatNo() + ": " + seatsInfo.get(i).getPlayerName() + " ( £" + seatsInfo.get(i).getStackSize() + " )");
            str.append(System.getProperty("line.separator"));
        }

        for (int i = 0; i < sequenceList.size(); i++) {
            DynamicSequence seq = sequenceList.get(i);
            if (seq.getType().equals("1")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " posts small blind [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));
            }
            if (seq.getType().equals("2")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " posts big blind [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("27")) {
                str.append("** Dealing down cards **");
                str.append(System.getProperty("line.separator"));
                str.append("Dealt to " + getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " [ " + getPlayerCards(seatsInfo, seq.getSeat()) + "]");
                str.append(System.getProperty("line.separator"));
            }
            if (seq.getType().equals("3")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " folds");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("4")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " calls [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("10")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " raises [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("28")) {
                str.append("** Dealing flop ** [ " + startingCards[0] + ", " + startingCards[1] + ", " + startingCards[2] + " ]");
                str.append(System.getProperty("line.separator"));
            }
            if (seq.getType().equals("7")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " bets [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));


            }
            if (seq.getType().equals("29")) {
                str.append("** Dealing turn ** [ " + startingCards[3] + " ]");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("6")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " checks");
                str.append(System.getProperty("line.separator"));


            }
            if (seq.getType().equals("30")) {
                str.append("** Dealing river ** [ " + startingCards[4] + " ]");
                str.append(System.getProperty("line.separator"));

            }
            if (seq.getType().equals("26")) {
                str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " shows [" + getPlayerCards(seatsInfo, seq.getSeat()) + "]");
                str.append(System.getProperty("line.separator"));

            }
            /*if(seq.getType().equals("17")){
                potSize= potSize+ Float.parseFloat(seq.getVolume());
            }*/
            if (seq.getType().equals("14")) {
                str.append("** Summary **");
                str.append(System.getProperty("line.separator"));
                 //DecimalFormat df = new DecimalFormat("0.00");
                 //str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " collected [£" + String.valueOf(df.format(potSize-rakeSize)) + "]");
                 str.append(getNameFromSeatNumber(seatsInfo, seq.getSeat()) + " collected [£" + seq.getVolume() + "]");
                str.append(System.getProperty("line.separator"));
                str.append(System.getProperty("line.separator"));
                str.append(System.getProperty("line.separator"));
                str.append(System.getProperty("line.separator"));
            }

        }

        File file = new File("F:\\Sourav Freelancing\\Guru\\Alex Holgate\\Output\\file.txt");
        try {

            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(file, true));
            out.write(str.toString());
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
}

    private String getNameFromSeatNumber(ArrayList<SeatInfo> seats, String seatNumber) {
        String name="";
        for(int i=0;i<seats.size();i++)
        {
            if(seats.get(i).getSeatNo().equalsIgnoreCase(seatNumber))
            {
                name = seats.get(i).getPlayerName();
            }
        }
        return name;
    }
    private String getPlayerCards(ArrayList<SeatInfo> seats, String seatNumber) {
        String cards="";
        for(int i=0;i<seats.size();i++)
        {
            if(seats.get(i).getSeatNo().equals(seatNumber))
            {
                cards = seats.get(i).getCards();
            }
        }
        return cards;
    }
    private void alterPlayerStacks(ArrayList<SeatInfo> seats, float bigB)
    {
            for (int i = 0; i < seats.size(); i++) {
                if (seats.get(i).getStackSize().equals("0")) {
                    seats.get(i).setStackSize(String.valueOf(bigB));
                }
            }

    }
}

