import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;


class Hotel {
    private String name;
    private String location;
    private String roomType;
    private double price;
    private Map<LocalDate, Boolean> availability; // Доступность номеров по датам

    public Hotel(String name, String location, String roomType, double price) {
        this.name = name;
        this.location = location;
        this.roomType = roomType;
        this.price = price;
        this.availability = new HashMap<>();

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 365; i++) {
            this.availability.put(today.plusDays(i), true);
        }
    }

    public String getLocation() {
        return location;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable(LocalDate startDate, LocalDate endDate) {
        return availability.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                .allMatch(Map.Entry::getValue);
    }

    public void bookDates(LocalDate startDate, LocalDate endDate) {
        availability.entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                .forEach(entry -> entry.setValue(false));
    }

    @Override
    public String toString() {
        return "Hotel{name='" + name + "', location='" + location + "', roomType='" + roomType + "', price=" + price + '}';
    }
}





class Booking {
    private String userId;
    private Hotel hotel;
    private LocalDate startDate;
    private LocalDate endDate;

    public Booking(String userId, Hotel hotel, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Booking{userId='" + userId + "', hotel=" + hotel + ", startDate=" + startDate + ", endDate=" + endDate + '}';
    }
}





interface HotelService {
    List<Hotel> searchHotels(String location, String roomType, double maxPrice);
}

class HotelServiceImpl implements HotelService {
    private List<Hotel> hotels;

    public HotelServiceImpl() {
        this.hotels = new ArrayList<>();

    }

    @Override
    public List<Hotel> searchHotels(String location, String roomType, double maxPrice) {
        Collectors Collectors = null;
        return hotels.stream()
                .filter(hotel -> hotel.getLocation().equalsIgnoreCase(location)
                        && hotel.getRoomType().equalsIgnoreCase(roomType)
                        && hotel.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
}


interface BookingService {
    boolean bookRoom(String userId, Hotel hotel, LocalDate startDate, LocalDate endDate);
}

class BookingServiceImpl implements BookingService {
    private Map<String, List<Booking>> bookings;

    public BookingServiceImpl() {
        this.bookings = new HashMap<>();
    }

    @Override
    public boolean bookRoom(String userId, Hotel hotel, LocalDate startDate, LocalDate endDate) {
        if (hotel.isAvailable(startDate, endDate)) {
            Booking booking = new Booking(userId, hotel, startDate, endDate);
            bookings.computeIfAbsent(userId, k -> new ArrayList<>()).add(booking);
            hotel.bookDates(startDate, endDate);
            return true;
        }
        return false;
    }
}

interface PaymentService {
    boolean processPayment(String userId, double amount);
}

class PaymentServiceImpl implements PaymentService {
    @Override
    public boolean processPayment(String userId, double amount) {
        System.out.println("Processing payment for " + userId + " amount: $" + amount);
        return true;
    }
}

interface NotificationService {
    void sendNotification(String userId, String message);
}

 class NotificationServiceImpl implements NotificationService {
    @Override
    public void sendNotification(String userId, String message) {
        System.out.println("Sending notification to " + userId + ": " + message);
    }
}


interface UserManagementService {
    boolean registerUser(String username, String password);
    boolean loginUser(String username, String password);
}

class UserManagementServiceImpl implements UserManagementService {
    private Map<String, String> users = new HashMap<>();

    @Override
    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, password);
        return true;
    }

    @Override
    public boolean loginUser(String username, String password) {
        return users.getOrDefault(username, "").equals(password);
    }
}

public class Component {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        HotelService hotelService = new HotelServiceImpl();
        BookingService bookingService = new BookingServiceImpl();
        PaymentService paymentService = new PaymentServiceImpl();
        NotificationService notificationService = new NotificationServiceImpl();
        UserManagementService userService = new UserManagementServiceImpl();


        ((HotelServiceImpl) hotelService).searchHotels("", "", 0).addAll(List.of(
                new Hotel("Grand Hotel", "Paris", "Deluxe", 200.0),
                new Hotel("Beach Resort", "Malibu", "Standard", 150.0),
                new Hotel("City Inn", "New York", "Economy", 100.0)
        ));

        System.out.println("Welcome to the Hotel Booking System");
        System.out.print("Register (r) or Login (l): ");
        String choice = scanner.nextLine();

        String currentUser = null;
        if (choice.equalsIgnoreCase("r")) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (userService.registerUser(username, password)) {
                System.out.println("Registration successful!");
                currentUser = username;
            } else {
                System.out.println("Username already exists.");
            }
        } else if (choice.equalsIgnoreCase("l")) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            if (userService.loginUser(username, password)) {
                System.out.println("Login successful!");
                currentUser = username;
            } else {
                System.out.println("Invalid credentials.");
                return;
            }
        }

        if (currentUser == null) {
            System.out.println("Authentication failed. Exiting.");
            return;
        }


        System.out.println("Search hotels:");
        System.out.print("Enter location: ");
        String location = scanner.nextLine();
        System.out.print("Enter room type: ");
        String roomType = scanner.nextLine();
        System.out.print("Enter max price: ");
        double maxPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        var hotels = hotelService.searchHotels(location, roomType, maxPrice);
        if (!hotels.isEmpty()) {
            System.out.println("Available hotels:");
            for (int i = 0; i < hotels.size(); i++) {
                System.out.println((i + 1) + ". " + hotels.get(i));
            }

            System.out.print("Select a hotel (1-" + hotels.size() + "): ");
            int hotelIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            System.out.print("Enter start date (YYYY-MM-DD): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter end date (YYYY-MM-DD): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            Hotel selectedHotel = hotels.get(hotelIndex);
            if (bookingService.bookRoom(currentUser, selectedHotel, startDate, endDate)) {
                System.out.println("Room booked successfully!");
                paymentService.processPayment(currentUser, selectedHotel.getPrice());
                notificationService.sendNotification(currentUser, "Your booking is confirmed!");
            } else {
                System.out.println("Room is not available for the selected dates.");
            }
        } else {
            System.out.println("No hotels found matching the criteria.");
        }

    }
}



