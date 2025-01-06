package model;

/**
 * Represents a single customer requesting a parcel.
 */
public class Customer {
    private int seqNumber;
    private String name;
    private String desiredParcelID;

    public Customer(int seqNumber, String name, String desiredParcelID) {
        this.seqNumber = seqNumber;
        this.name = name;
        this.desiredParcelID = desiredParcelID.toUpperCase();
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public String getName() {
        return name;
    }

    public String getDesiredParcelID() {
        return desiredParcelID;
    }

    @Override
    public String toString() {
        return "C" + seqNumber + " - " + name + " (Parcel: " + desiredParcelID + ")";
    }
}
