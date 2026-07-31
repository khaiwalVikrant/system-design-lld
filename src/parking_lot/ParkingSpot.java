package parking_lot;

abstract class ParkingSpot {
    private final String spotId;
    private final SpotType spotType;
    private volatile boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.isOccupied = false;
    }

    public synchronized boolean park(Vehicle vehicle) {
        if (isOccupied || !canFitVehicle(vehicle)) {
            return false;
        }
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
        return true;
    }

    public synchronized void unpark() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }

    public abstract boolean canFitVehicle(Vehicle vehicle);

    public boolean isOccupied() { return isOccupied; }
    public String getSpotId() { return spotId; }
    public SpotType getSpotType() { return spotType; }
}
