from datetime import date, timedelta
from abc import ABC, abstractmethod
from typing import List, Optional

class Book:
    def __init__(self, title: str, author: str, isbn: str, publication_year: int):
        self.__title = title
        self.__author = author
        self.__isbn = isbn
        self.__publication_year = publication_year
        self.__is_borrowed = False
        self.__borrower = None
        self.__due_date = None

    def get_title(self) -> str:
        return self.__title

    def get_author(self) -> str:
        return self.__author

    def get_isbn(self) -> str:
        return self.__isbn

    def get_publication_year(self) -> int:
        return self.__publication_year

    def is_borrowed(self) -> bool:
        return self.__is_borrowed

    def get_borrower(self):
        return self.__borrower

    def get_due_date(self):
        return self.__due_date

    def borrow_book(self, borrower, due_date: date) -> bool:
        if not self.__is_borrowed:
            self.__is_borrowed = True
            self.__borrower = borrower
            self.__due_date = due_date
            return True
        return False

    def return_book(self) -> bool:
        if self.__is_borrowed:
            self.__is_borrowed = False
            self.__borrower = None
            self.__due_date = None
            return True
        return False

    def calculate_late_fee(self) -> float:
        if self.__is_borrowed and self.__due_date and date.today() > self.__due_date:
            days_late = (date.today() - self.__due_date).days
            return days_late * 0.50
        return 0.0

    def __str__(self):
        status = "Borrowed" if self.__is_borrowed else "Available"
        due = f", Due: {self.__due_date}" if self.__is_borrowed and self.__due_date else ""
        return f"'{self.__title}' by {self.__author} (ISBN: {self.__isbn}, Year: {self.__publication_year}) - Status: {status}{due}"

class EBook(Book):
    def __init__(self, title: str, author: str, isbn: str, publication_year: int, file_format: str):
        super().__init__(title, author, isbn, publication_year)
        self.__file_format = file_format

    def get_file_format(self) -> str:
        return self.__file_format

    def calculate_late_fee(self) -> float:
        if self.is_borrowed() and self.get_due_date() and date.today() > self.get_due_date():
            days_late = (date.today() - self.get_due_date()).days
            return days_late * 0.25
        return 0.0

    def __str__(self):
        base_str = super().__str__()
        return f"{base_str}, Format: {self.__file_format}"

class PrintedBook(Book):
    def __init__(self, title: str, author: str, isbn: str, publication_year: int, num_pages: int):
        super().__init__(title, author, isbn, publication_year)
        self.__num_pages = num_pages

    def get_num_pages(self) -> int:
        return self.__num_pages

    def calculate_late_fee(self) -> float:
        if self.is_borrowed() and self.get_due_date() and date.today() > self.get_due_date():
            days_late = (date.today() - self.get_due_date()).days
            return days_late * 0.75
        return 0.0

    def __str__(self):
        base_str = super().__str__()
        return f"{base_str}, Pages: {self.__num_pages}"

class Member:
    def __init__(self, member_id: str, name: str):
        self.__member_id = member_id
        self.__name = name
        self.__borrowed_books = []

    def get_member_id(self) -> str:
        return self.__member_id

    def get_name(self) -> str:
        return self.__name

    def get_borrowed_books(self) -> List[Book]:
        return list(self.__borrowed_books)

    def borrow_book(self, book: Book, due_date: date) -> bool:
        if book.borrow_book(self, due_date):
            self.__borrowed_books.append(book)
            return True
        return False

    def return_book(self, book: Book) -> bool:
        if book in self.__borrowed_books and book.return_book():
            self.__borrowed_books.remove(book)
            return True
        return False

# --- SOLID Payment Processors ---
class PaymentProcessor(ABC):
    @abstractmethod
    def process_payment(self, amount: float) -> bool:
        pass

class CreditCardPayment(PaymentProcessor):
    def process_payment(self, amount: float) -> bool:
        print(f"💳 Processing credit card payment of ${amount:.2f}")
        return amount > 0

class PayPalPayment(PaymentProcessor):
    def process_payment(self, amount: float) -> bool:
        print(f"🅿️ Processing PayPal payment of ${amount:.2f}")
        return amount > 0

class CashPayment(PaymentProcessor):
    def process_payment(self, amount: float) -> bool:
        print(f"💵 Processing cash payment of ${amount:.2f}")
        return amount >= 0

# LibraryCatalog and LibraryManagementSystem classes can also be added (same as previous)
