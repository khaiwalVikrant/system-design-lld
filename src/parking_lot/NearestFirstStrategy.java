package parking_lot;

import java.util.List;

class NearestFirstStrategy implements ParkingStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (!spot.isOccupied() && spot.canFitVehicle(vehicle)) {
                    return spot; // Returns first matching available spot
                }
            }
        }
        return null; // Lot is full
    }
}
