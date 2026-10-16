import java.util.*;
import java.io.*;

class Room {
    int roomId;
    String roomType;
    double price;
    boolean isAvailable = true;
    int guestId;
    String guestName = "";

    Room(int roomId, String roomType, double price) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.price = price;
    }
}

public class HotelBooking {

    static Scanner sc = new Scanner(System.in);
    static Room[] rooms = new Room[100];
    static int roomCount = 0;

    static void addRoom() {
        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (findRoom(id) != null) {
            System.out.println("Room ID already exists!");
            return;
        }

        System.out.print("Enter Room Type: ");
        String type = sc.nextLine();

        System.out.print("Enter Price Per Night: ");
        double price = sc.nextDouble();

        if (price <= 0) {
            System.out.println("Price must be positive!");
            return;
        }

        rooms[roomCount++] = new Room(id, type, price);
        saveRooms();
        System.out.println("Room added successfully!");
    }

    static Room findRoom(int id) {
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].roomId == id) {
                return rooms[i];
            }
        }
        return null;
    }

    static void bookRoom() {
        System.out.print("Enter Guest ID: ");
        int guestId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Guest Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();

        Room room = findRoom(roomId);

        if (room == null) {
            System.out.println("Room not found!");
        } else if (!room.isAvailable) {
            System.out.println("Room is already occupied!");
        } else {
            room.isAvailable = false;
            room.guestId = guestId;
            room.guestName = name;

            saveRooms();

            System.out.println("\n--- BOOKING CONFIRMATION ---");
            System.out.println("Guest ID: " + guestId);
            System.out.println("Guest Name: " + name);
            System.out.println("Room ID: " + roomId);
            System.out.println("Room Type: " + room.roomType);
            System.out.printf("Price Per Night: Rs. %.2f%n",
                              room.price);
            System.out.println("Booking successful!");
        }
    }

    static void checkOut() {
        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();

        Room room = findRoom(id);

        if (room == null) {
            System.out.println("Room not found!");
        } else if (room.isAvailable) {
            System.out.println("Room is already available!");
        } else {
            room.isAvailable = true;
            room.guestId = 0;
            room.guestName = "";

            saveRooms();
            System.out.println("Check-out successful!");
        }
    }

    static void searchRoom() {
        System.out.print("Enter Room ID: ");
        int id = sc.nextInt();

        Room room = findRoom(id);

        if (room == null) {
            System.out.println("Room not found!");
        } else {
            System.out.println("\n--- ROOM DETAILS ---");
            System.out.println("Room ID: " + room.roomId);
            System.out.println("Room Type: " + room.roomType);
            System.out.printf("Price: Rs. %.2f%n", room.price);
            System.out.println("Status: " +
                (room.isAvailable ? "Available" : "Occupied"));

            if (!room.isAvailable) {
                System.out.println("Guest ID: " + room.guestId);
                System.out.println("Guest Name: " + room.guestName);
            }
        }
    }

    static void occupancyReport() {
        int occupied = 0;
        double revenue = 0;

        System.out.println("\n--- OCCUPANCY REPORT ---");

        for (int i = 0; i < roomCount; i++) {
            if (!rooms[i].isAvailable) {
                occupied++;
                revenue += rooms[i].price;
            }
        }

        int available = roomCount - occupied;
        double occupiedPercent = roomCount == 0 ? 0 :
                occupied * 100.0 / roomCount;
        double availablePercent = roomCount == 0 ? 0 :
                available * 100.0 / roomCount;

        System.out.println("Total Rooms: " + roomCount);
        System.out.println("Occupied Rooms: " + occupied);
        System.out.println("Available Rooms: " + available);
        System.out.printf("Occupancy: %.2f%%%n", occupiedPercent);
        System.out.printf("Availability: %.2f%%%n", availablePercent);
        System.out.printf("Estimated One-Night Revenue: Rs. %.2f%n",
                          revenue);

        System.out.println("\n--- AVAILABLE ROOMS ---");
        for (int i = 0; i < roomCount; i++) {
            if (rooms[i].isAvailable) {
                System.out.println(rooms[i].roomId + " - " +
                    rooms[i].roomType + " - Rs. " + rooms[i].price);
            }
        }
    }

    static void saveRooms() {
        try (PrintWriter pw = new PrintWriter(
                new FileWriter("rooms.txt"))) {
            for (int i = 0; i < roomCount; i++) {
                Room r = rooms[i];
                pw.println(r.roomId + "|" + r.roomType + "|" +
                    r.price + "|" + r.isAvailable + "|" +
                    r.guestId + "|" + r.guestName.replace("|", "/"));
            }
        } catch (IOException e) {
            System.out.println("Error saving room records!");
        }
    }

    static void loadRooms() {
        File file = new File("rooms.txt");

        if (!file.exists()) {
            return;
        }

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine() && roomCount < rooms.length) {
                String[] data = reader.nextLine().split("\\|", -1);

                if (data.length == 6) {
                    Room r = new Room(
                        Integer.parseInt(data[0]),
                        data[1],
                        Double.parseDouble(data[2])
                    );

                    r.isAvailable = Boolean.parseBoolean(data[3]);
                    r.guestId = Integer.parseInt(data[4]);
                    r.guestName = data[5];

                    rooms[roomCount++] = r;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading room records!");
        }
    }

    public static void main(String[] args) {
        loadRooms();

        // Add sample rooms for the first run
        if (roomCount == 0) {
            rooms[roomCount++] = new Room(204, "Deluxe", 3500);
            rooms[roomCount++] = new Room(101, "Standard", 2000);
            rooms[roomCount++] = new Room(305, "Suite", 5000);
            saveRooms();
        }

        int choice;

        do {
            System.out.println("\n===== HOTEL BOOKING AND OCCUPANCY MANAGER =====");
            System.out.println("1. Add Room");
            System.out.println("2. Book Room");
            System.out.println("3. Check Out");
            System.out.println("4. Search Room");
            System.out.println("5. Occupancy Report");
            System.out.println("6. Exit");
            System.out.print("Select: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addRoom();
                    break;
                case 2:
                    bookRoom();
                    break;
                case 3:
                    checkOut();
                    break;
                case 4:
                    searchRoom();
                    break;
                case 5:
                    occupancyReport();
                    break;
                case 6:
                    saveRooms();
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } while (choice != 6);

        sc.close();
    }
}