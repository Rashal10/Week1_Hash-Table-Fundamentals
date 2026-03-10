import java.util.*;

class ParkingLot {

    static class Spot {
        String licensePlate;
        long entryTime;
        String status;

        Spot() {
            status = "EMPTY";
        }
    }

    private Spot[] table;
    private int size;
    private int occupied = 0;
    private int totalProbes = 0;

    public ParkingLot(int size) {
        this.size = size;
        table = new Spot[size];
        for (int i = 0; i < size; i++)
            table[i] = new Spot();
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % size;
    }

    public void parkVehicle(String plate) {
        int index = hash(plate);
        int probes = 0;

        while (!table[index].status.equals("EMPTY")) {
            index = (index + 1) % size;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";

        occupied++;
        totalProbes += probes;

        System.out.println("Vehicle " + plate + " parked at spot " + index +
                " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        for (int i = 0; i < size; i++) {

            if (table[i].status.equals("OCCUPIED") &&
                    table[i].licensePlate.equals(plate)) {

                long duration = System.currentTimeMillis() - table[i].entryTime;
                double hours = duration / 3600000.0;
                double fee = hours * 5;

                table[i].status = "DELETED";
                occupied--;

                System.out.println("Spot " + i + " freed | Fee $" + fee);
                return;
            }
        }

        System.out.println("Vehicle not found.");
    }

    public void statistics() {

        double occupancy = (occupied * 100.0) / size;
        double avgProbe = (occupied == 0) ? 0 : (double) totalProbes / occupied;

        System.out.println("Occupancy: " + occupancy + "%");
        System.out.println("Average Probes: " + avgProbe);
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC1234");
        lot.parkVehicle("ABC1235");
        lot.parkVehicle("XYZ9999");

        lot.exitVehicle("ABC1234");

        lot.statistics();
    }
}