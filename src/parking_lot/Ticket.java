package parking_lot;

public class Ticket {
    private final String ticketId;
    private final String licensePlate;
    private final ParkingSpot spot;
    private final long entryTime;

    public Ticket(String ticketId, String licensePlate, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.licensePlate = licensePlate;
        this.spot = spot;
        this.entryTime = System.currentTimeMillis();
    }

    public String getTicketId() { return ticketId; }
    public ParkingSpot getSpot() { return spot; }
    public long getEntryTime() { return entryTime; }
}
