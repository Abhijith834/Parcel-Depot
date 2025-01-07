package model;

/**
 * Worker class calculates fees and may perform additional processing steps.
 */
public class Worker {

    /**
     * Processes a customer's parcel (placeholder for extra logic if needed).
     */
    public void processCustomer(Customer customer, ParcelMap parcelMap) {
        // Additional processing can be placed here if required
    }

    /**
     * Fee calculation rules:
     * 1) baseFee = (L * W * H) * weight
     * 2) daysInDepot => +1%/day => finalFee = baseFee * (1 + days/100)
     * 3) If ID starts with 'C' => 20% discount => finalFee *= 0.8
     */
    public double calculateFee(Parcel p) {
        double baseFee = (p.getLength() * p.getWidth() * p.getHeight()) * p.getWeight();
        System.out.println("Base Fee for Parcel " + p.getParcelID() + ": " + baseFee);

        double dayFactor = 1.0 + (p.getDaysInDepot() / 100.0);
        double dayAdjustedFee = baseFee * dayFactor;
        System.out.println("Day Adjusted Fee for Parcel " + p.getParcelID() + ": " + dayAdjustedFee);

        if (p.getParcelID().startsWith("C")) {
            dayAdjustedFee *= 0.8;
            System.out.println("Applied 20% discount for Parcel " + p.getParcelID() + ": " + dayAdjustedFee);
        }
        return dayAdjustedFee;
    }
}
