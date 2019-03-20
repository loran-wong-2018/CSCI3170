import java.io.*;
import java.util.Scanner;

import java.sql.*;


class employee {

	static Connection con = null;

	static String dbAddress = "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db28";
	static String dbUsername = "Group28";
	static String dbPassword = "CSCI3170";

	// Caution: Won't work if the order is changed
	public static String[] employeeMessages = {
		"Show Available Positions",
		"Mark Interested Position",
		"Check Average Working Time",
		"Go back"
	};

	static String employeeInput;

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
			System.out.println("Employee, what would you like to do?");
			for (int i = 0; i < employeeMessages.length; i++) {
				System.out.println((i + 1) + ". " + employeeMessages[i]);
			}
			System.out.println("Please enter [1-" + employeeMessages.length + "].");
			Scanner employeeReader = new Scanner(System.in);  // Reading from System.in
			employeeInput = employeeReader.next();
			// Show Available Positions
			if (employeeInput.equals("1")) {
				System.out.println("Please enter your ID.");
				Scanner eeidReader = new Scanner(System.in);  // Reading from System.in
				String eeidInput = eeidReader.next();

				// Check whether employee id is valid
				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employee WHERE Employee_ID='" + eeidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					// Find the salary, experience, skills of employee
					int eeSalary = 99999;
					int eeExperience = 0;
					String[] eeSkills = {};
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Employee WHERE Employee_ID='" + eeidInput + "'";
						ResultSet rs = stmt.executeQuery(query);
						rs.next();
						eeSalary = rs.getInt("Expected_Salary");
						eeExperience = rs.getInt("Experience");
						eeSkills = rs.getString("Skills").split(";");
						success = true;
					} catch (SQLException e) {
						System.out.println(e);
					}

					// Convert the skills array into condition query
					String skillCondition = "";
					for (int i = 0; i < eeSkills.length; i++) {
						if (i != 0) {
							skillCondition += " OR ";
						}
						skillCondition += "Position_Title='" + eeSkills[i] + "'";
					}

					try {
						// Search for all available positions for the employee
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Position WHERE Status=True AND Salary>=" + eeSalary + " AND Experience<=" + eeExperience + " AND (" + skillCondition + ")";
						ResultSet rs = stmt.executeQuery(query);
						
						System.out.println("Your available positions are:");
						System.out.println("Position_ID, Position_Title, Salary, Company, Size, Founded");

						// Find the company name, size, founded year for each available positions
						while(rs.next()) {
							String company = "NULL";
							int size = 0;
							int founded = 0;
							
							try {
								Statement nested_stmt = con.createStatement();
								String nested1_query = "SELECT * FROM Employer WHERE Employer_ID='" + rs.getString("Employer_ID") + "'";
								ResultSet nested1_rs = nested_stmt.executeQuery(nested1_query);
								nested1_rs.next();
								String nested2_query = "SELECT * FROM Company WHERE Company='" + nested1_rs.getString("Company") + "'";
								ResultSet nested2_rs = nested_stmt.executeQuery(nested2_query);
								nested2_rs.next();
								company = nested2_rs.getString("Company");
								size = nested2_rs.getInt("Size");
								founded = nested2_rs.getInt("Founded");
							} catch (SQLException e) {
								System.out.println(e);
							}
							
							System.out.println(rs.getString("Position_ID") + ", " + rs.getString("Position_Title") + ", " + rs.getInt("Salary") + ", " + company + ", " + size + ", " + founded);
						}
					} catch (SQLException e) {
						System.out.println(e);
					}
				} else {
					System.out.println("Employee ID does not exist.");
				}

			// Mark Interested Position
			} else if (employeeInput.equals("2")) {
				System.out.println("Please enter your ID.");
				Scanner eeidReader = new Scanner(System.in);  // Reading from System.in
				String eeidInput = eeidReader.next();

				// Check whether employee id is valid
				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employee WHERE Employee_ID='" + eeidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					// Search for companies worked in before
					String workedBefore = "";
					int loopCount1 = 0;
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Employment_History WHERE Employee_ID='" + eeidInput + "'";
						ResultSet rs = stmt.executeQuery(query);
						while(rs.next()) {
							try {
								Statement nested_stmt = con.createStatement();
								String nested1_query = "SELECT * FROM Position WHERE Position_ID='" + rs.getString("Position_ID") + "'";
								ResultSet nested1_rs = nested_stmt.executeQuery(nested1_query);
								nested1_rs.next();
								String nested2_query = "SELECT * FROM Employer WHERE Employer_ID='" + nested1_rs.getString("Employer_ID") + "'";
								ResultSet nested2_rs = nested_stmt.executeQuery(nested2_query);
								nested2_rs.next();
								if (loopCount1 != 0) {
									workedBefore += ",";
								}
								workedBefore += nested2_rs.getString("Company");
							} catch (SQLException e) {
								System.out.println("1234");
							}
							loopCount1++;
						}
					} catch (SQLException e) {
						System.out.println(e);
					}

					String[] workedCompanies = workedBefore.split(",");

					// Convert the workedCompanies array into condition query
					String workedCompaniesCondition = "";
					for (int i = 0; i < workedCompanies.length; i++) {
						if (i != 0) {
							workedCompaniesCondition += " OR ";
						}
						workedCompaniesCondition += "Company='" + workedCompanies[i] + "'";
					}

					// Search for all employers of the workedCompanies
					String allEmployers = "";
					int loopCount2 = 0;
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Employer WHERE " + workedCompaniesCondition;
						ResultSet rs = stmt.executeQuery(query);
						while(rs.next()) {
							if (loopCount2 != 0) {
								allEmployers += ",";
							}
							allEmployers += rs.getString("Employer_ID");
							loopCount2++;
						}
					} catch (SQLException e) {
						System.out.println(e);
					}
					String[] workedEmployers = allEmployers.split(",");

					// Convert the workedEmployers array into condition query
					String workedEmployersCondition = "";
					for (int i = 0; i < workedEmployers.length; i++) {
						if (i != 0) {
							workedEmployersCondition += " AND ";
						}
						workedEmployersCondition += "Employer_ID<>'" + workedEmployers[i] + "'";
					}

					// Search for the positions that has been marked as interested
					String interestedBefore = "";
					int loopCount3 = 0;
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM marked WHERE Employee_ID='" + eeidInput + "'";
						ResultSet rs = stmt.executeQuery(query);
						while(rs.next()) {				
							if (loopCount3 != 0) {
								interestedBefore += ",";
							}
							interestedBefore += rs.getString("Position_ID");
							loopCount3++;
						}
					} catch (SQLException e) {
						System.out.println(e);
					}
					String[] interestedPositions = interestedBefore.split(",");

					// Convert the interestedPositions array into condition query
					String interestedPositionsCondition = "";
					for (int i = 0; i < interestedPositions.length; i++) {
						if (i != 0) {
							interestedPositionsCondition += " AND ";
						}
						interestedPositionsCondition += "Position_ID<>'" + interestedPositions[i] + "'";
					}

					// Find the salary, experience, skills of employee
					int eeSalary = 99999;
					int eeExperience = 0;
					String[] eeSkills = {};
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Employee WHERE Employee_ID='" + eeidInput + "'";
						ResultSet rs = stmt.executeQuery(query);
						rs.next();
						eeSalary = rs.getInt("Expected_Salary");
						eeExperience = rs.getInt("Experience");
						eeSkills = rs.getString("Skills").split(";");
					} catch (SQLException e) {
						System.out.println(e);
					}

					// Convert the skills array into condition query
					String skillCondition = "";
					for (int i = 0; i < eeSkills.length; i++) {
						if (i != 0) {
							skillCondition += " OR ";
						}
						skillCondition += "Position_Title='" + eeSkills[i] + "'";
					}

					int loopCount4 = 0;
					try {
						// Search for all available positions that do not violate the two additional conditions
						Statement stmt = con.createStatement();
						String query = "SELECT * FROM Position WHERE Status=True AND Salary>=" + eeSalary + " AND Experience<=" + eeExperience + " AND (" + skillCondition + ") AND (" + interestedPositionsCondition + ") AND (" + workedEmployersCondition + ")";
						ResultSet rs = stmt.executeQuery(query);

						System.out.println("Your interested positions are:");
						System.out.println("Position_ID, Position_Title, Salary, Company, Size, Founded");
						
						// Find the company name, size, founded year for each listed position
						while(rs.next()) {
							loopCount4++;
							String company = "NULL";
							int size = 0;
							int founded = 0;
							try {
								Statement nested_stmt = con.createStatement();
								String nested1_query = "SELECT * FROM Employer WHERE Employer_ID='" + rs.getString("Employer_ID") + "'";
								ResultSet nested1_rs = nested_stmt.executeQuery(nested1_query);
								nested1_rs.next();
								String nested2_query = "SELECT * FROM Company WHERE Company='" + nested1_rs.getString("Company") + "'";
								ResultSet nested2_rs = nested_stmt.executeQuery(nested2_query);
								nested2_rs.next();
								company = nested2_rs.getString("Company");
								size = nested2_rs.getInt("Size");
								founded = nested2_rs.getInt("Founded");
							} catch (SQLException e) {
								System.out.println(e);
							}
							System.out.println(rs.getString("Position_ID") + ", " + rs.getString("Position_Title") + ", " + rs.getInt("Salary") + ", " + company + ", " + size + ", " + founded);
						}
					} catch (SQLException e) {
						System.out.println(e);
					}

					// Only ask employee to mark interested position if there are at least one available positions
					if (loopCount4 > 0) {
						System.out.println("Please enter one interested Position_ID.");
						Scanner pidReader = new Scanner(System.in);  // Reading from System.in
						String pidInput = pidReader.next();
						try {
							Statement nested_stmt = con.createStatement();
							String nested_query = "Insert into marked values ('" + pidInput + "', '" + eeidInput + "', FALSE)";
							nested_stmt.executeUpdate(nested_query);
						} catch (SQLException e) {
							System.out.println("Position ID is invalid.");
						}
					}
				} else {
					System.out.println("Employee ID does not exist.");
				}

			// Check Average Working Time
			} else if (employeeInput.equals("3")) {
				System.out.println("Please enter your ID.");
				Scanner eeidReader = new Scanner(System.in);  // Reading from System.in
				String eeidInput = eeidReader.next();

				Boolean success = false;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT * FROM Employee WHERE Employee_ID='" + eeidInput + "'";
					ResultSet rs = stmt.executeQuery(query);
					while(rs.next()) {
						success = true;
					}
				} catch (SQLException e) {
					System.out.println(e);
				}

				if (success) {
					try {
						Statement stmt = con.createStatement();
						String query = "SELECT TIMESTAMPDIFF(day, Start, End) AS 'Duration' FROM Employment_History WHERE Employee_ID='" + eeidInput + "' LIMIT 3";
						ResultSet rs = stmt.executeQuery(query);
						int sum = 0;
						int loopCount5 = 0;
						while (rs.next()) {
							sum += rs.getInt("Duration");
							loopCount5++;
						}
						if (loopCount5 >= 3) {
							System.out.println("Your average working time is: " + (sum / 3) + " days.");
						} else {
							System.out.println("Less than 3 records.");
						}
					} catch (SQLException e) {
						System.out.println(e);
					}
				} else {
					System.out.println("Employee ID does not exist.");
				}
			}

		} while (!employeeInput.equals(String.format("%d", employeeMessages.length)));
	}
}