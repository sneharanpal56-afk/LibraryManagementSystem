import java.util.*;

// Main Library class
public class LibraryManagementSystem {

    // ====================== ABSTRACT CLASS ======================
    public static abstract class Book {
        private final String id;
        private final String title;
        private final String author;
        private boolean borrowed;

        public Book(String id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.borrowed = false;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public boolean isBorrowed() { return borrowed; }
        public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

        // Abstract method for polymorphism
        public abstract double calculateLateFee(int daysLate);

        @Override
        public String toString() {
            return String.format("ID: %s | Title: %s | Author: %s | Borrowed: %s",
                    id, title, author, borrowed ? "Yes" : "No");
        }
    }

    // ====================== SUBCLASS: EBook ======================
    public static class EBook extends Book {
        private final String downloadUrl;

        public EBook(String id, String title, String author, String downloadUrl) {
            super(id, title, author);
            this.downloadUrl = downloadUrl;
        }

        @Override
        public double calculateLateFee(int daysLate) {
            return Math.max(0, daysLate) * 0.10; // $0.10 per day
        }

        @Override
        public String toString() {
            return super.toString() + " | Type: EBook | URL: " + downloadUrl;
        }
    }

    // ====================== SUBCLASS: PrintedBook ======================
    public static class PrintedBook extends Book {
        private final int numberOfPages;

        public PrintedBook(String id, String title, String author, int numberOfPages) {
            super(id, title, author);
            this.numberOfPages = numberOfPages;
        }

        @Override
        public double calculateLateFee(int daysLate) {
            return Math.max(0, daysLate) * 0.50; // $0.50 per day
        }

        @Override
        public String toString() {
            return super.toString() + " | Type: Printed Book | Pages: " + numberOfPages;
        }
    }

    // ====================== INTERFACE: Payment ======================
    public interface Payment {
        boolean pay(double amount);
    }

    // ====================== IMPLEMENTATION: CardPayment ======================
    public static class CardPayment implements Payment {
        private final String cardNumber;

        public CardPayment(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        @Override
        public boolean pay(double amount) {
            System.out.printf("Processing card payment of $%.2f using card ending with %s%n",
                    amount, cardNumber.substring(cardNumber.length() - 4));
            System.out.println("Payment Successful ✅");
            return true;
        }
    }

    // ====================== IMPLEMENTATION: CashPayment ======================
    public static class CashPayment implements Payment {
        @Override
        public boolean pay(double amount) {
            System.out.printf("Processing cash payment of $%.2f%n", amount);
            System.out.println("Payment Successful ✅");
            return true;
        }
    }

    // ====================== INTERFACE: BookRepository ======================
    public interface BookRepository {
        void save(Book book);
        Book findById(String id);
        List<Book> findAll();
    }

    // ====================== CLASS: InMemoryBookRepository ======================
    public static class InMemoryBookRepository implements BookRepository {
        private final Map<String, Book> storage = new HashMap<>();

        @Override
        public void save(Book book) {
            storage.put(book.getId(), book);
        }

        @Override
        public Book findById(String id) {
            return storage.get(id);
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(storage.values());
        }
    }

    // ====================== CLASS: LibraryService ======================
    public static class LibraryService {
        private final BookRepository repository;

        public LibraryService(BookRepository repository) {
            this.repository = repository;
        }

        public void addBook(Book book) {
            repository.save(book);
        }

        public void listAllBooks() {
            System.out.println("\n📚 Library Books:");
            for (Book book : repository.findAll()) {
                System.out.println(book);
            }
            System.out.println();
        }

        public Book borrowBook(String id) {
            Book book = repository.findById(id);
            if (book == null) {
                System.out.println("❌ Book not found!");
                return null;
            }
            if (book.isBorrowed()) {
                System.out.println("⚠️ Book is already borrowed!");
                return null;
            }
            book.setBorrowed(true);
            System.out.println("✅ You borrowed: " + book.getTitle());
            return book;
        }

        public void returnBook(String id, int daysLate, Payment payment) {
            Book book = repository.findById(id);
            if (book == null) {
                System.out.println("❌ Book not found!");
                return;
            }
            if (!book.isBorrowed()) {
                System.out.println("⚠️ This book was not borrowed.");
                return;
            }
            double fee = book.calculateLateFee(daysLate);
            if (payment.pay(fee)) {
                book.setBorrowed(false);
                System.out.printf("✅ Book returned successfully. Late fee: $%.2f%n", fee);
            } else {
                System.out.println("❌ Payment failed!");
            }
        }
    }

    // ====================== MAIN MENU ======================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LibraryService library = new LibraryService(new InMemoryBookRepository());

        while (true) {
            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("1. Add Printed Book");
            System.out.println("2. Add EBook");
            System.out.println("3. List All Books");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Book ID: ");
                    String id = sc.nextLine();
                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter Number of Pages: ");
                    int pages = sc.nextInt(); sc.nextLine();
                    library.addBook(new PrintedBook(id, title, author, pages));
                    System.out.println("✅ Printed Book added successfully!");
                }

                case 2 -> {
                    System.out.print("Enter Book ID: ");
                    String id = sc.nextLine();
                    System.out.print("Enter Title: ");
                    String title = sc.nextLine();
                    System.out.print("Enter Author: ");
                    String author = sc.nextLine();
                    System.out.print("Enter Download URL: ");
                    String url = sc.nextLine();
                    library.addBook(new EBook(id, title, author, url));
                    System.out.println("✅ EBook added successfully!");
                }

                case 3 -> library.listAllBooks();

                case 4 -> {
                    System.out.print("Enter Book ID to borrow: ");
                    String id = sc.nextLine();
                    library.borrowBook(id);
                }

                case 5 -> {
                    System.out.print("Enter Book ID to return: ");
                    String id = sc.nextLine();
                    System.out.print("Enter days late: ");
                    int days = sc.nextInt(); sc.nextLine();
                    System.out.print("Select Payment Method (1 = Cash, 2 = Card): ");
                    int payChoice = sc.nextInt(); sc.nextLine();

                    Payment payment;
                    if (payChoice == 2) {
                        System.out.print("Enter Card Number: ");
                        String card = sc.nextLine();
                        payment = new CardPayment(card);
                    } else {
                        payment = new CashPayment();
                    }

                    library.returnBook(id, days, payment);
                }

                case 6 -> {
                    System.out.println("👋 Exiting... Thank you for using the Library System!");
                    sc.close();
                    return;
                }

                default -> System.out.println("❌ Invalid choice! Try again.");
            }
        }
    }
}
