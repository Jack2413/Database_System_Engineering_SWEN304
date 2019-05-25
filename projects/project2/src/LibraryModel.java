
/*
 * LibraryModel.java
 * Author:
 * Created on:
 */

import javax.swing.*;
import java.sql.*;

public class LibraryModel {

	// For use in creating dialogs and making them modal
	private JFrame dialogParent;
	private Connection con = null;

	public LibraryModel(JFrame parent, String userid, String password) {
		dialogParent = parent;
		// Register a PostgreSQL Driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Can not find the driver class: \n" + "Either I have not installed it properly or \n "
					+ "postgresql.jar file is not in my CLASSPATH");
		}

		// Establish a Connection
		String url = "jdbc:postgresql:"+ "//db.ecs.vuw.ac.nz/" + userid + "_jdbc";
		//String url = "jdbc:postgresql://localhost/project2";
		try {
			con = DriverManager.getConnection(url, userid, password);
		} catch (SQLException sqlex) {
			System.out.println("Can not connect");
			System.out.println(sqlex.getMessage());
		}
	}

	private String checkSQL(String statement) {

		try {
			// Create a Statement object
			Statement s = con.createStatement();
			// Execute the Statement object
			ResultSet rs = s.executeQuery(statement);

			// Handle query answer in ResultSet object
			rs.next();
			return rs.getString(1);

		} // End of the try block
		catch (SQLException sqlex) {
			System.out.println(sqlex.getMessage());
			return "error";
		}

	}

	private String getResultSQL(String format, String statement) {
		StringBuffer sBuffer = new StringBuffer();
		try {
			// Create a Statement object
			Statement s = con.createStatement();
			// Execute the Statement object
			ResultSet rs = s.executeQuery(statement);

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			// Handle query answer in ResultSet object

			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					sBuffer.append(String.format(format, rs.getString(i)));
				}
				sBuffer.append("\n");
			}
			sBuffer.append("\n");
		} // End of the try block
		catch (SQLException sqlex) {
			System.out.println(sqlex.getMessage());
		}

		return sBuffer.toString();

	}

	public String bookLookup(int isbn) {
		if (!checkBooklExits(isbn)) {
			return "The book is not exits";
		}
		String SQLstatement = "SELECT name,surname FROM author NATURAL JOIN book_author WHERE isbn =" + isbn
				+ "ORDER BY authorSeqNo";
		String attribute = String.format("%-15s|%-15s|\n%s", "Name", "Surname", "---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showCatalogue() {
		String SQLstatement = "SELECT * FROM book";
		String attribute = String.format("%-15s|%-60s|%-15s|%-15s|%-15s|\n%s", "Isbn", "Book Title", "Edition No",
				"Number of Cop", "Number left",
				"---------------+------------------------------------------------------------+---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showLoanedBooks() {
		String SQLstatement = "SELECT * FROM cust_book";
		String attribute = String.format("%-15s|%-15s|%-15s|\n%s", "Isbn", "Due date", "CustomerID",
				"---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showAuthor(int authorID) {
		if (!checkAuthorExits(authorID)) {
			return "author is not exits";
		}
		String SQLstatement = "SELECT * FROM author WHERE authorid =" + authorID;
		String attribute = String.format("%-15s|%-15s|%-15s\n%s", "AuthorID", "Name", "Surname",
				"---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showAllAuthors() {
		
		String SQLstatement = "SELECT * FROM author";
		String attribute = String.format("%-15s|%-15s|%-15s\n%s", "AuthorID", "Name", "Surname",
				"---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showCustomer(int customerID) {
		if (!checkCustomerExits(customerID)) {
			return "Customer is not exits";
		}
		String SQLstatement = "SELECT * FROM customer Where customerID =" + customerID;
		String attribute = String.format("%-15s|%-15s|%-15s|%-15s|\n%s", "CustomerID", "last name", "frist name",
				"City", "---------------+---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String showAllCustomers() {
		String SQLstatement = "SELECT * FROM customer";
		String attribute = String.format("%-15s|%-15s|%-15s|%-15s|\n%s", "CustomerID", "last name", "frist name",
				"City", "---------------+---------------+---------------+---------------+\n");
		System.out.print(attribute + getResultSQL("%-15s|", SQLstatement));
		return attribute + getResultSQL("%-15s|", SQLstatement);
	}

	public String borrowBook(int isbn, int customerID, int day, int month, int year) {
		PreparedStatement updateBook = null;
		PreparedStatement insertLoan = null;

		try {
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			if (!checkCustomerExits(customerID)) {
				con.rollback();
				return "Customer is not exits";
			}
			if (!checkBooklExits(isbn)) {
				con.rollback();
				return "The book is not exits";
			}

			int numleft = checkNumOfLeft(isbn);
			if (numleft < 1) {
				con.rollback();
				return "The book is not available, do not have enough book in the library";
			}

			String SQLstatement = "UPDATE book SET numleft = " + (numleft - 1) + " where isbn= " + isbn;
			String SQLstatement2 = String.format("INSERT INTO cust_book VALUES (%d,'%d-%d-%d',%d)", isbn, day, month,
					year, customerID);

			updateBook = con.prepareStatement(SQLstatement);
			updateBook.executeUpdate();

			JOptionPane.showMessageDialog(null, "Message form Library");

			insertLoan = con.prepareStatement(SQLstatement2);
			insertLoan.executeUpdate();

			con.commit();
			return "Borrow Book Successful\n";
		} catch (SQLException sqlex) {
			System.out.println(sqlex.getMessage());
			try {
				con.rollback();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (updateBook != null) {
				try {
					updateBook.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (insertLoan != null) {
				try {
					insertLoan.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "Do not borrow Books\n";
	}

	private int checkNumOfLeft(int isbn) {
		String SQLstatement = "SELECT numleft FROM book WHERE isbn =" + isbn;
		System.out.println("Numer of books left: " + checkSQL(SQLstatement));
		return Integer.parseInt(checkSQL(SQLstatement));
	}

	private boolean checkBooklExits(int isbn) {
		String SQLstatement = "SELECT isbn FROM book WHERE isbn =" + isbn;
		System.out.println("Isbn from book: " + checkSQL(SQLstatement));
		return !checkSQL(SQLstatement).equals("error");
	}

	private boolean checkCustomerExits(int customerID) {
		String SQLstatement = "SELECT customerID FROM customer WHERE customerID =" + customerID;
		System.out.println("CustomerID: " + checkSQL(SQLstatement));
		return !checkSQL(SQLstatement).equals("error");
	}

	public String returnBook(int isbn, int customerid) {
		PreparedStatement updateBook = null;
		PreparedStatement updateLoan = null;

		try {
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			if (!checkCustomerLoadedBook(customerid)) {
				con.rollback();
				return "Customer is not loaned any book";
			}
			if (!checkLoadedBooklExits(isbn)) {
				con.rollback();
				return "The book is not loaned";
			}
			int numleft = checkNumOfLeft(isbn);

			String SQLstatement = "Delete from cust_book where isbn =" + isbn + "AND customerid = " + customerid;
			String SQLstatement2 = "UPDATE book SET numleft = " + (numleft + 1) + " where isbn= " + isbn;

			updateBook = con.prepareStatement(SQLstatement);
			updateBook.executeUpdate();

			JOptionPane.showMessageDialog(null, "Message form Library");

			updateLoan = con.prepareStatement(SQLstatement2);
			updateLoan.executeUpdate();

			con.commit();
			return "Return Book Successful\n";

		} catch (SQLException sqlex) {
			System.out.println(sqlex.getMessage());
			try {
				con.rollback();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (updateBook != null) {
				try {
					updateBook.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (updateLoan != null) {
				try {
					updateLoan.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "Return fail";
	}

	private boolean checkAuthorExits(int authorID) {
		String SQLstatement = "SELECT authorid FROM author WHERE authorID =" + authorID;
		System.out.println("authorID: " + checkSQL(SQLstatement));
		return !checkSQL(SQLstatement).equals("error");
	}

	private boolean checkCustomerLoadedBook(int customerid) {
		String SQLstatement = "SELECT customerID FROM cust_book WHERE customerID =" + customerid;
		System.out.println("CustomerID: " + checkSQL(SQLstatement));
		return !checkSQL(SQLstatement).equals("error");

	}

	private boolean checkLoadedBooklExits(int isbn) {
		String SQLstatement = "SELECT isbn FROM cust_book WHERE isbn =" + isbn;
		System.out.println("Isbn from book: " + checkSQL(SQLstatement));
		return !checkSQL(SQLstatement).equals("error");
	}

	public void closeDBConnection() {
		try {
			con.close();
		} catch (SQLException sqlex) {
			System.out.println(sqlex.getMessage());
		}
	}

	public String deleteCus(int customerID) {
		try {
			con.setAutoCommit(false);

			if (!checkCustomerExits(customerID)) {
				con.rollback();
				return "Customer is not exits";
			}
			if (checkCustomerLoadedBook(customerID)) {
				con.rollback();
				return "Customer is loaned book, it can not be delete";
			}
			String SQLstatement = "Delete from customer where customerid = " + customerID;
			Statement s = con.createStatement();
			s.executeUpdate(SQLstatement);
			s.close();
			return "Delete Customer Successful";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Delete Customer fail";

	}

	public String deleteAuthor(int authorID) {
		try {
			con.setAutoCommit(false);
			if (!checkAuthorExits(authorID)) {
				con.rollback();
				return "author is not exits";
			}
			String SQLstatement = "Delete from author where authorid = " + authorID;
			Statement s = con.createStatement();
			s.executeUpdate(SQLstatement);
			s.close();
			return "Delete Author Successful";
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Delete Author fail";
	}

	public String deleteBook(int isbn) {

		try {
			con.setAutoCommit(false);

			if (!checkBooklExits(isbn)) {
				con.rollback();
				return "The book is not exits";
			}
			if (checkLoadedBooklExits(isbn)) {
				con.rollback();
				return "The book is loaned, it can not be delete";
			}

			String SQLstatement = "Delete from book where isbn = " + isbn;
			Statement s = con.createStatement();
			s.executeUpdate(SQLstatement);
			s.close();
			return "Delete Book Successful";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				con.rollback();
				con.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Delete Book fail";
	}
}