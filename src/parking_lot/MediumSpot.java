package parking_lot;

public class MediumSpot extends ParkingSpot{
    public MediumSpot(String spotId)
    {
        super(spotId, SpotType.MEDIUM);
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        // Medium spots can accommodate Cars and Motorcycles
        return vehicle.getType() == VehicleType.CAR || vehicle.getType() == VehicleType.MOTORCYCLE;
    }
}
