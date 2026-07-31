package parking_lot;

import java.util.List;

public interface ParkingStrategy {
    ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}
