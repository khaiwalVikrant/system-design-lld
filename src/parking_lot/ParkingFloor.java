package parking_lot;

import java.util.List;

public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;

    public ParkingFloor(int floorNumber, List<ParkingSpot> spots) {
        this.floorNumber = floorNumber;
        this.spots = spots;
    }

    public List<ParkingSpot> getSpots() { return spots; }
    public int getFloorNumber() { return floorNumber; }
}
