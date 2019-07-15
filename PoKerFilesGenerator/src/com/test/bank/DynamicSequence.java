package com.test.bank;

public class DynamicSequence {
    private String seq;
    private String type;
    private String seat;
    private String volume;
    private String low;
    private String pot;


    public DynamicSequence(String seq, String type) {
        this.seq = seq;
        this.type = type;
    }


    public DynamicSequence(String seq, String type, String seat) {
        this.seq = seq;
        this.type = type;
        if(seat.equals("3")) {
            this.seat = "4";
        }
        else if(seat.equals("4")) {
            this.seat = "6";
        }
        else if(seat.equals("5")) {
            this.seat = "7";
        }
        else if(seat.equals("6")) {
            this.seat = "9";
        }
        else
        {
            this.seat=seat;
        }
    }

    public DynamicSequence(String seq, String type, String seat, String volume) {
        this.seq = seq;
        this.type = type;
        if(seat.equals("3")) {
            this.seat = "4";
        }
        else if(seat.equals("4")) {
            this.seat = "6";
        }
        else if(seat.equals("5")) {
            this.seat = "7";
        }
        else if(seat.equals("6")) {
            this.seat = "9";
        }
        else
        {
            this.seat=seat;
        }
        if(volume.length()<3) {
            this.volume = volume + ".00";
        }
        else
        {
            this.volume = volume;
        }
    }

    public DynamicSequence(String seq, String type, String seat, String volume, String low, String pot) {
        this.seq = seq;
        this.type = type;
        if(seat.equals("3")) {
            this.seat = "4";
        }
        else if(seat.equals("4")) {
            this.seat = "6";
        }
        else if(seat.equals("5")) {
            this.seat = "7";
        }
        else if(seat.equals("6")) {
            this.seat = "9";
        }
        else
        {
            this.seat=seat;
        }
        if(volume.length()<3) {
            this.volume = volume + ".00";
        }
        else
        {
            this.volume = volume;
        }        this.low = low;
        this.pot = pot;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getPot() {
        return pot;
    }

    public void setPot(String pot) {
        this.pot = pot;
    }
}
