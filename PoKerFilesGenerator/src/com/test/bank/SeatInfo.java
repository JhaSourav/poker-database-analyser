package com.test.bank;

public class SeatInfo {
    String seatNo;
    String playerName;
    String stackSize;
    String cards;

    public SeatInfo(String seatNo, String playerName, String stackSize,String cards) {
        if(seatNo.equals("3")) {
            this.seatNo = "4";
        }
        else if(seatNo.equals("4")) {
            this.seatNo = "6";
        }
       else if(seatNo.equals("5")) {
            this.seatNo = "7";
        }
        else if(seatNo.equals("6")) {
            this.seatNo = "9";
        }
        else
        {
            this.seatNo=seatNo;
        }
        this.playerName = playerName;
        this.stackSize = stackSize;
        this.cards=cards;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getStackSize() {
        return stackSize;
    }

    public void setStackSize(String stackSize) {
        this.stackSize = stackSize;
    }

    public String getCards() {
        return cards;
    }

    public void setCards(String cards) {
        this.cards = cards;
    }
}
