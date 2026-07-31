package parking_lot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    private static ParkingLot instance;
    private final List<ParkingFloor> floors = new ArrayList<>();
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
    private ParkingStrategy strategy;
    private static final double HOURLY_RATE = 10.0;

    private ParkingLot() {
        this.strategy = new NearestFirstStrategy();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addFloor(ParkingFloor floor) { floors.add(floor); }
    public void setStrategy(ParkingStrategy strategy) { this.strategy = strategy; }

    public synchronized Ticket parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = strategy.findSpot(floors, vehicle);
        if (spot != null && spot.park(vehicle)) {
            String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8);
            Ticket ticket = new Ticket(ticketId, vehicle.getLicensePlate(), spot);
            activeTickets.put(ticketId, ticket);
            return ticket;
        }
        throw new RuntimeException("No available spot for vehicle type: " + vehicle.getType());
    }

    public synchronized double exitVehicle(String ticketId) {
        Ticket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid Ticket ID");
        }

        // Unpark vehicle
        ticket.getSpot().unpark();

        // Calculate fee
        long durationMillis = System.currentTimeMillis() - ticket.getEntryTime();
        double hours = Math.max(1.0, durationMillis / (1000.0 * 60 * 60)); // Minimum 1 hour charge
        return hours * HOURLY_RATE;
    }
}
