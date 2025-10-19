import unittest
from library import *

class TestLibrary(unittest.TestCase):
    def test_add_and_find_book(self):
        repo = InMemoryBookRepository()
        book = PrintedBook("1", "OOP in Python", "John Doe", 300)
        repo.save(book)
        self.assertEqual(repo.find_by_id("1").get_title(), "OOP in Python")

    def test_late_fee(self):
        ebook = EBook("2", "AI Basics", "Jane Smith", "download.com/ai")
        self.assertEqual(ebook.calculate_late_fee(5), 0.5)
        printed = PrintedBook("3", "Design Patterns", "Martin Fowler", 450)
        self.assertEqual(printed.calculate_late_fee(4), 2.0)

if __name__ == "__main__":
    unittest.main()
