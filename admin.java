import java.io.*;
import java.util.Scanner;

import java.sql.*;


class admin {

	static Connection con = null;

	static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db28";
	static String dbUsername = "Group28";
	static String dbPassword = "CSCI3170";

	// Caution: Won't work if the order is changed
	public static String[] adminMessages = {
		"Create tables",
		"Delete tables",
		"Load data",
		"Check data",
		"Go back"
	};

	static String adminInput;

	public static void main(String[] args) {

		// Connect to the remote CUHK database provided in this course
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		} catch (ClassNotFoundException e) {
			System.out.println("[Error]: Java MySQL DB Driver not found!");
			System.exit(0);
		} catch (SQLException e) {
			System.out.println(e);
		}

		do {
			System.out.println("Administrator, what would you like to do?");
			for (int i = 0; i < adminMessages.length; i++) {
				System.out.println((i + 1) + ". " + adminMessages[i]);
			}
			System.out.println("Please enter [1-" + adminMessages.length + "].");
			Scanner adminReader = new Scanner(System.in);  // Reading from System.in
			adminInput = adminReader.next();
			// Create All Tables
			if (adminInput.equals("1")) {
				System.out.print("Processing...");
				try {
					Statement stmt = con.createStatement();
					// Employee Table
					String createQuery1 = "CREATE TABLE IF NOT EXISTS Employee (";
						createQuery1 += "Employee_ID CHAR(6) NOT NULL,";
						createQuery1 += "Name CHAR(6) NOT NULL,";
						createQuery1 += "Expected_Salary INT UNSIGNED,";
						createQuery1 += "Experience INT UNSIGNED,";
						createQuery1 += "Skills CHAR(50) NOT NULL,";
						createQuery1 += "PRIMARY KEY (Employee_ID))";
					stmt.executeUpdate(createQuery1);
					// Company Table
					String createQuery2 = "CREATE TABLE IF NOT EXISTS Company (";
						createQuery2 += "Company CHAR(30) NOT NULL,";
						createQuery2 += "Size INT UNSIGNED,";
						createQuery2 += "Founded INT(4) UNSIGNED,";
						createQuery2 += "PRIMARY KEY (Company))";
					stmt.executeUpdate(createQuery2);
					// Employer Table
					String createQuery3 = "CREATE TABLE IF NOT EXISTS Employer (";
						createQuery3 += "Employer_ID CHAR(6) NOT NULL,";
						createQuery3 += "Name CHAR(30) NOT NULL,";
						createQuery3 += "Company CHAR(30) NOT NULL,";
						createQuery3 += "PRIMARY KEY (Employer_ID),";
						createQuery3 += "FOREIGN KEY (Company) REFERENCES Company(Company))";
					stmt.executeUpdate(createQuery3);
					// Position Table
					String createQuery4 = "CREATE TABLE IF NOT EXISTS Position (";
						createQuery4 += "Position_ID CHAR(6) NOT NULL,";
						createQuery4 += "Position_Title CHAR(30) NOT NULL,";
						createQuery4 += "Salary INT UNSIGNED,";
						createQuery4 += "Experience INT UNSIGNED,";
						createQuery4 += "Employer_ID CHAR(6) NOT NULL,";
						createQuery4 += "Status BOOLEAN,";
						createQuery4 += "PRIMARY KEY (Position_ID),";
						createQuery4 += "FOREIGN KEY (Employer_ID) REFERENCES Employer(Employer_ID))";
					stmt.executeUpdate(createQuery4);
					// Employment_History Table
					String createQuery5 = "CREATE TABLE IF NOT EXISTS Employment_History (";
						createQuery5 += "Employee_ID CHAR(6) NOT NULL,";
						createQuery5 += "Position_ID CHAR(6) NOT NULL,";
						createQuery5 += "Start DATE,";
						createQuery5 += "End DATE,";
						createQuery5 += "PRIMARY KEY (Position_ID),";
						createQuery5 += "FOREIGN KEY (Employee_ID) REFERENCES Employee (Employee_ID),";
						createQuery5 += "FOREIGN KEY (Position_ID) REFERENCES Position (Position_ID))";
					stmt.executeUpdate(createQuery5);
					// marked Table
					String createQuery6 = "CREATE TABLE IF NOT EXISTS marked (";
						createQuery6 += "Position_ID CHAR(6) NOT NULL,";
						createQuery6 += "Employee_ID CHAR(6) NOT NULL,";
						createQuery6 += "Status BOOLEAN,";
						createQuery6 += "PRIMARY KEY (Position_ID, Employee_ID),";		
						createQuery6 += "FOREIGN KEY (Position_ID) REFERENCES Position (Position_ID),";
						createQuery6 += "FOREIGN KEY (Employee_ID) REFERENCES Employee (Employee_ID))";
					stmt.executeUpdate(createQuery6);
				} catch (SQLException e) {
					System.out.println(e);
				}
				System.out.println("Done! Tables are created!");
			// Delete All Tables
			} else if (adminInput.equals("2")) {
				System.out.print("Processing...");
				try {
					Statement stmt = con.createStatement();
					String dropQuery1 = "DROP TABLE IF EXISTS Employee";
					String dropQuery2 = "DROP TABLE IF EXISTS Company";
					String dropQuery3 = "DROP TABLE IF EXISTS Employer";
					String dropQuery4 = "DROP TABLE IF EXISTS Position";
					String dropQuery5 = "DROP TABLE IF EXISTS Employment_History";
					String dropQuery6 = "DROP TABLE IF EXISTS marked";
					stmt.executeUpdate(dropQuery6);
					stmt.executeUpdate(dropQuery5);
					stmt.executeUpdate(dropQuery4);
					stmt.executeUpdate(dropQuery3);
					stmt.executeUpdate(dropQuery2);
					stmt.executeUpdate(dropQuery1);
				} catch (SQLException e) {
					System.out.println(e);
				}
				System.out.println("Done! Tables are deleted!");
			// Insert the Data from csv Files
			} else if (adminInput.equals("3")) {
				System.out.println("Please enter the folder path.");
				Scanner folderReader = new Scanner(System.in);  // Reading from System.in
				String folderInput = folderReader.next();
				System.out.print("Processing...");
				// Insert data from employee.csv
				try { 
				    BufferedReader bReader = new BufferedReader(new FileReader(folderInput + "/employee.csv"));
				    String line = "";
				    try {
				    	while ((line = bReader.readLine()) != null) {
				            if (line != null) {
				                String[] array = line.split(",");
			                    try {
									Statement stmt = con.createStatement();
									String insertQueryLoop = "Insert into Employee values ('" + array[0] + "', '" + array[1] + "', " + array[2] + ", " + array[3] + ", '" + array[4] + "')";
									stmt.executeUpdate(insertQueryLoop);
								} catch (SQLException e) {
									System.out.println(e);
								}
				            } 
				        }
				        bReader.close();
				    } catch (IOException ex) {
				        ex.printStackTrace();
				    }
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				}
				// Insert data from company.csv
				try { 
				    BufferedReader bReader = new BufferedReader(new FileReader(folderInput + "/company.csv"));
				    String line = "";
				    try {
				    	while ((line = bReader.readLine()) != null) {
				            if (line != null) {
				                String[] array = line.split(",");
			                    try {
									Statement stmt = con.createStatement();
									String insertQueryLoop = "Insert into Company values ('" + array[0] + "', " + array[1] + ", " + array[2] + ")";
									stmt.executeUpdate(insertQueryLoop);
								} catch (SQLException e) {
									System.out.println(e);
								}
				            } 
				        }
				        bReader.close();
				    } catch (IOException ex) {
				        ex.printStackTrace();
				    }
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				}
				// Insert data from employer.csv
				try { 
				    BufferedReader bReader = new BufferedReader(new FileReader(folderInput + "/employer.csv"));
				    String line = "";
				    try {
				    	while ((line = bReader.readLine()) != null) {
				            if (line != null) {
				                String[] array = line.split(",");
			                    try {
									Statement stmt = con.createStatement();
									String insertQueryLoop = "Insert into Employer values ('" + array[0] + "', '" + array[1] + "', '" + array[2] + "')";
									stmt.executeUpdate(insertQueryLoop);
								} catch (SQLException e) {
									System.out.println(e);
								}
				            } 
				        }
				        bReader.close();
				    } catch (IOException ex) {
				        ex.printStackTrace();
				    }
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				}
				// Insert data from position.csv
				try { 
				    BufferedReader bReader = new BufferedReader(new FileReader(folderInput + "/position.csv"));
				    String line = "";
				    try {
				    	while ((line = bReader.readLine()) != null) {
				            if (line != null) {
				                String[] array = line.split(",");
			                    try {
									Statement stmt = con.createStatement();
									String insertQueryLoop = "Insert into Position values ('" + array[0] + "', '" + array[1] + "', " + array[2] + ", " + array[3] + ", '" + array[4] + "', " + array[5] + ")";
									stmt.executeUpdate(insertQueryLoop);
								} catch (SQLException e) {
									System.out.println(e);
								}
				            } 
				        }
				        bReader.close();
				    } catch (IOException ex) {
				        ex.printStackTrace();
				    }
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				}
				// Insert data from history.csv
				try { 
				    BufferedReader bReader = new BufferedReader(new FileReader(folderInput + "/history.csv"));
				    String line = "";
				    try {
				    	while ((line = bReader.readLine()) != null) {
				            if (line != null) {
				                String[] array = line.split(",");
			                    try {
									Statement stmt = con.createStatement();
									String insertQueryLoop = "";
									// Both Start date and End date are NULL
									if (array[3].equals("NULL") && array[4].equals("NULL")) {
										insertQueryLoop = "Insert into Employment_History values ('" + array[0] + "', '" + array[2] + "', NULL, NULL)";
									// Only Start date is NULL
									} else if (array[3].equals("NULL")) {
										insertQueryLoop = "Insert into Employment_History values ('" + array[0] + "', '" + array[2] + "', NULL, '" + array[4] + "')";
									// Only End date is NULL
									} else if (array[4].equals("NULL")) {
										insertQueryLoop = "Insert into Employment_History values ('" + array[0] + "', '" + array[2] + "', '" + array[3] + "', NULL)";
									// Both dates are valid
									} else {
										insertQueryLoop = "Insert into Employment_History values ('" + array[0] + "', '" + array[2] + "', '" + array[3] + "', '" + array[4] + "')";
									}	
									stmt.executeUpdate(insertQueryLoop);
								} catch (SQLException e) {
									System.out.println(e);
								}
				            } 
				        }
				        bReader.close();
				    } catch (IOException ex) {
				        ex.printStackTrace();
				    }
				} catch (FileNotFoundException ex) {
				    ex.printStackTrace();
				}
				System.out.println("Done! Data is loaded!");
			// Print the number of rows in each table
			} else if (adminInput.equals("4")) {
				System.out.println("Number of records in each table:");
				try {
					Statement stmt = con.createStatement();
					// Print the number of rows in the Employee table
					String loadQuery = "SELECT COUNT(*) AS total FROM Employee";
					ResultSet rs1 = stmt.executeQuery(loadQuery);
					rs1.next();
					System.out.println("Employee: " + rs1.getInt("total"));
					// Print the number of rows in the Company table
					String loadQuery2 = "SELECT COUNT(*) AS total FROM Company";
					ResultSet rs2 = stmt.executeQuery(loadQuery2);
					rs2.next();
					System.out.println("Company: " + rs2.getInt("total"));
					// Print the number of rows in the Employer table
					String loadQuery3 = "SELECT COUNT(*) AS total FROM Employer";
					ResultSet rs3 = stmt.executeQuery(loadQuery3);
					rs3.next();
					System.out.println("Employer: " + rs3.getInt("total"));
					// Print the number of rows in the Position table
					String loadQuery4 = "SELECT COUNT(*) AS total FROM Position";
					ResultSet rs4 = stmt.executeQuery(loadQuery4);
					rs4.next();
					System.out.println("Position: " + rs4.getInt("total"));
					// Print the number of rows in the Employment_History table
					String loadQuery5 = "SELECT COUNT(*) AS total FROM Employment_History";
					ResultSet rs5 = stmt.executeQuery(loadQuery5);
					rs5.next();
					System.out.println("Employment_History: " + rs5.getInt("total"));
					// Print the number of rows in the marked table
					String loadQuery6 = "SELECT COUNT(*) AS total FROM marked";
					ResultSet rs6 = stmt.executeQuery(loadQuery6);
					rs6.next();
					System.out.println("marked: " + rs6.getInt("total"));
				} catch (SQLException e) {
					System.out.println(e);
				}
			}

		} while (!adminInput.equals(String.format("%d", adminMessages.length)));
	}
}