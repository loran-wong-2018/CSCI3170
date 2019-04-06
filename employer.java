import java.io.*;
import java.util.Scanner;
import java.util.Random;

import java.sql.*;


class employer {

	static Connection con = null;

	static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db28";
	static String dbUsername = "Group28";
	static String dbPassword = "CSCI3170";

	// Caution: Won't work if the order is changed
	public static String[] employerMessages = {
		"Post Position Recruitment",
		"Check employees and arrange an interview",
		"Accept an employee",
		"Go back"
	};

	static String employerInput;

	public static void main(String[] args) {

		// Connect to the database
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
			System.out.println("Employer, what would you like to do?");
			for (int i = 0; i < employerMessages.length; i++) {
				System.out.println((i + 1) + ". " + employerMessages[i]);
			}
			System.out.println("Please enter [1-" + employerMessages.length + "].");
			Scanner employerReader = new Scanner(System.in);  // Reading from System.in
			employerInput = employerReader.next();
			// Show Available Positions
			if (employerInput.equals("1")) {
				System.out.println("Please enter your ID.");
				Scanner eidReader = new Scanner(System.in);  // Reading from System.in
				String eidInput = eidReader.next();

				// Check whether employer id is valid
				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employer WHERE Employer_ID='" + eidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					System.out.println("Please enter the position title.");
					Scanner ptReader = new Scanner(System.in);  // Reading from System.in
					String ptInput = ptReader.next();

					System.out.println("Please enter an upper bound of salary.");
					Scanner ubsReader = new Scanner(System.in);  // Reading from System.in
					String ubsInput = ubsReader.next();

					System.out.println("Please enter the required experience (press enter to skip).");
					Scanner reReader = new Scanner(System.in);  // Reading from System.in
					String reInput = reReader.nextLine();
					if(reInput.equals("")){
						reInput = "0";
					}

					// Check if there are potential employees
					int havePotentialEmployees = 0;
					try {
						// Filter all potential employees with expected salary and experience
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Employee WHERE Expected_Salary<=" + ubsInput + " AND Experience>=" + reInput;
						ResultSet rs = stmt.executeQuery(query);

						// Filter the above employees with position title
						while(rs.next()) {
							String[] eeSkills = {};
							eeSkills = rs.getString("Skills").split(";");
							for (int i = 0; i < eeSkills.length; i++) {
								if (ptInput.equals(eeSkills[i])) {

									// Filter the above employees who are working currently
									int currentlyWorking = 0;
									try {
										Statement nestedStmt = con.createStatement();
										String nestedQuery = "SELECT * FROM Employment_History WHERE Employee_ID='" + rs.getString("Employee_ID") + "' AND END IS NULL";
										ResultSet nestedRs = nestedStmt.executeQuery(nestedQuery);
										while(nestedRs.next()) {
											currentlyWorking = 1;
										}
									} catch (SQLException e) {
										System.out.println(e);
									}
									if (currentlyWorking == 0) {
										havePotentialEmployees++;
									}
								}
							}
						}	
					} catch (SQLException e) {
						System.out.println(e);
					}

					if(havePotentialEmployees != 0) {
						// Loop until generate one unused pid string
						int generatePid = 1;
						String pidString = "pid";
						while(generatePid == 1) {
							pidString = "pid";
							char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
							for(int i = 0; i < 3; i++) {
								Random r = new Random();
								int result = r.nextInt(26);
								pidString += alphabet[result];
							}
							try {
								Statement pidLoopStmt = con.createStatement();
								String pidLoopQuery = "SELECT * FROM Position WHERE Position_ID='" + pidString + "'";
								ResultSet pidLoopRs = pidLoopStmt.executeQuery(pidLoopQuery);
								if(pidLoopRs.next()) {
									generatePid = 1;
								} else {
									generatePid = 0;
								}
							} catch (SQLException e) {
								System.out.println(e);
							}
						}
						// Insert to Position table if there are potential employees
						try {
							System.out.println(havePotentialEmployees + " potential employees are founded. The position recruitment is posted.");
							Statement stmt = con.createStatement();
							String query = "Insert into Position values ('" + pidString + "', '" + ptInput + "', " + ubsInput + ", " + reInput + ", '" + eidInput + "', True)";
							stmt.executeUpdate(query);
						} catch (SQLException e) {
							System.out.println(e);
						}
					} else {
						System.out.println("There are no potential employees meeting all the criteria.");
					}
				} else {
					System.out.println("Employer ID does not exist.");
				}

			// Check employees and arrange an interview
			} else if (employerInput.equals("2")) {
				System.out.println("Please enter your ID.");
				Scanner eidReader = new Scanner(System.in);  // Reading from System.in
				String eidInput = eidReader.next();

				// Check whether employer id is valid
				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employer WHERE Employer_ID='" + eidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					int postedPositionExists = 0;
					System.out.println("The id of position recruitments posted by you are.");
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Position WHERE Employer_ID='" + eidInput + "'";
						ResultSet rs = stmt.executeQuery(query);
						while(rs.next()) {
							postedPositionExists = 1;
							System.out.println(rs.getString("Position_ID"));
						}
					} catch (SQLException e) {
						System.out.println(e);
					}
					if (postedPositionExists == 1) {
						System.out.println("Please pick one position id.");
						Scanner pidReader = new Scanner(System.in);  // Reading from System.in
						String pidInput = pidReader.next();
						try {
							System.out.println("The employees who mark interested in this position recruitment are:");
							System.out.println("Employee_ID, Name, Expected_Salary, Experience, Skills");
							Statement pidnestedStmt = con.createStatement();
							String pidnestedQuery = "SELECT * FROM marked WHERE Position_ID='" + pidInput + "'";
							ResultSet pidnestedRs = pidnestedStmt.executeQuery(pidnestedQuery);
							while(pidnestedRs.next()) {
								Statement nested2Stmt = con.createStatement();
								String nested2Query = "SELECT * FROM Employee WHERE Employee_ID='" + pidnestedRs.getString("Employee_ID") + "'";
								ResultSet nested2Rs = nested2Stmt.executeQuery(nested2Query);
								while(nested2Rs.next()) {
									System.out.println(nested2Rs.getString("Employee_ID") + ", " + nested2Rs.getString("Name") + ", " + nested2Rs.getInt("Expected_Salary") + ", " + nested2Rs.getInt("Experience") + ", " + nested2Rs.getString("Skills"));
								}
							}
						} catch (SQLException e) {
							System.out.println(e);
						}
						System.out.println("Please pick one employee by Employee_ID.");
						Scanner eeidReader = new Scanner(System.in);  // Reading from System.in
						String eeidInput = eeidReader.next();
						try {
							Statement nestedStmt = con.createStatement();
							String nestedQuery = "UPDATE marked SET Status=TRUE WHERE Position_ID='" + pidInput + "' AND Employee_ID='" + eeidInput + "'";
							nestedStmt.executeUpdate(nestedQuery);
						} catch (SQLException e) {
							System.out.println(e);
						}
						System.out.println("An IMMEDIATE interview has done.");
					}
				} else {
					System.out.println("Employer ID does not exist.");
				}

			// Accept an employee
			} else if (employerInput.equals("3")) {
				System.out.println("Please enter your ID.");
				Scanner eidReader = new Scanner(System.in);  // Reading from System.in
				String eidInput = eidReader.next();

				// Check whether employer id is valid
				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employer WHERE Employer_ID='" + eidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}


				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					
				} else {
					System.out.println("Employer ID does not exist.");
				}
			}

		} while (!employerInput.equals(String.format("%d", employerMessages.length)));
	}
}