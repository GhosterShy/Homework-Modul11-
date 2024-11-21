import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Book {
    private String title;
    private String author;
    private String ISBN;
    private Status status;

    public enum Status {
        AVAILABLE, BORROWED
    }

    public Book(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.status = Status.AVAILABLE;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getISBN() {
        return ISBN;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", status=" + status +
                '}';
    }
}


class Reader {
    private String name;
    private List<Book> borrowedBooks;
    private static final int MAX_BORROW_LIMIT = 3;

    public Reader(String name) {
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public boolean borrowBook(Book book) {
        if (borrowedBooks.size() >= MAX_BORROW_LIMIT) {
            System.out.println("Borrow limit reached!");
            return false;
        }
        if (book.getStatus() == Book.Status.AVAILABLE) {
            borrowedBooks.add(book);
            book.setStatus(Book.Status.BORROWED);
            System.out.println(name + " borrowed " + book.getTitle());
            return true;
        } else {
            System.out.println(book.getTitle() + " is not available.");
            return false;
        }
    }

    public void returnBook(Book book) {
        if (borrowedBooks.remove(book)) {
            book.setStatus(Book.Status.AVAILABLE);
            System.out.println(name + " returned " + book.getTitle());
        } else {
            System.out.println(name + " does not have " + book.getTitle());
        }
    }
}


class Librarian {
    private String name;

    public Librarian(String name) {
        this.name = name;
    }

    public void addBook(Library library, Book book) {
        library.addBook(book);
        System.out.println("Librarian added " + book.getTitle() + " to the library.");
    }

    public void removeBook(Library library, Book book) {
        if (library.removeBook(book)) {
            System.out.println("Librarian removed " + book.getTitle() + " from the library.");
        } else {
            System.out.println("Book not found in library.");
        }
    }
}


class Library {
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public boolean removeBook(Book book) {
        return books.remove(book);
    }

    public List<Book> searchByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String author) {
        return books.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public void displayBooks() {
        books.forEach(System.out::println);
    }
}




public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Librarian librarian = new Librarian("Alice");

        // Adding books
        Book book1 = new Book("1984", "George Orwell", "1234567890");
        Book book2 = new Book("Brave New World", "Aldous Huxley", "0987654321");
        Book book3 = new Book("Fahrenheit 451", "Ray Bradbury", "1122334455");

        librarian.addBook(library, book1);
        librarian.addBook(library, book2);
        librarian.addBook(library, book3);


        System.out.println("Books in library:");
        library.displayBooks();


        Reader reader = new Reader("John");
        reader.borrowBook(book1);
        reader.borrowBook(book2);


        System.out.println("\nBooks in library after borrowing:");
        library.displayBooks();


        reader.returnBook(book1);


        System.out.println("\nBooks in library after returning:");
        library.displayBooks();

        System.out.println("\nSearch results:");
        library.searchByAuthor("George Orwell").forEach(System.out::println);

    }
}


