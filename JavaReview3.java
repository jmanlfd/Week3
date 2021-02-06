package review3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JavaReview3 {

	public static void main(String[] args) {
		String connectionString = "jdbc:mysql://127.0.0.1:3306/practice";
		String dbLogin = "root";
		String dbPassword = "password";
		Connection conn = null;
		Scanner input = new Scanner(System.in);
		System.out.println("1. November");
		System.out.println("2. December");
		int choice;
		do {
			choice = input.nextInt();
		} while (choice != 1 && choice != 2);
		int month = (choice == 1) ? 11 : 12;
		String sql = "SELECT * FROM temperatures WHERE month=" + month + ";";
		System.out.println();
		try {
			conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
			if (conn != null) {
				Statement stmt = conn.createStatement(
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				ResultSet rs = stmt.executeQuery(sql);
				int numRows;
				int[][] temperatures;
				double averageHi = 0, averageLo = 0;
				int[] highest = { 0, 0 };
				int[] lowest = { 0, Integer.MAX_VALUE };

				rs.last();
				numRows = rs.getRow();
				rs.first();
				temperatures = new int[numRows][4];
				for (int i = 0; i < numRows; ++i) {
					temperatures[i][0] = rs.getInt("day");
					temperatures[i][1] = rs.getInt("hi");
					averageHi += temperatures[i][1];
					
					if (temperatures[i][1] > highest[1]) {
						highest[0] = temperatures[i][0];
						highest[1] = temperatures[i][1];
					}
					
					temperatures[i][2] = rs.getInt("lo");
					averageLo += temperatures[i][2];
					
					if (temperatures[i][2] < lowest[1]) {
						lowest[0] = temperatures[i][0];
						lowest[1] = temperatures[i][2];
					}
					
					temperatures[i][3] = temperatures[i][1] - temperatures[i][2];
					rs.next();
				}
				input.close();
				
				averageHi = Math.round((averageHi / 31) * 10) / 10d;
				averageLo = Math.round((averageLo / 31) * 10) / 10d;
				
				StringBuilder sb = new StringBuilder(1024);
				sb.append("--------------------------------------------------------------\n");
				if (month == 11) {
					sb.append("November 2020: Temperatures in Utah\n");
				}  else {
					sb.append("December 2020: Temperatures in Utah\n");
				}
				sb.append("--------------------------------------------------------------\n");
				sb.append("Day  High Low  Variance\n");
				sb.append("--------------------------------------------------------------\n");

				for (int i = 0; i < numRows; ++i) {
					sb.append(temperatures[i][0]);

					if (temperatures[i][0] < 10) {
						sb.append("    ");
					} else {
						sb.append("   ");
					}

					for (int j = 1; j < 4; ++j) {
						if (j < 3) {
							if (temperatures[i][j] < 10) {
								sb.append(temperatures[i][j] + "    ");
							} else {
								sb.append(temperatures[i][j] + "   ");
							}
						} else {
							sb.append(temperatures[i][j] + "\n");
						}

					}

				}

				sb.append("--------------------------------------------------------------\n");
				if (month == 11) {
					sb.append("November Highest Temperature: " + month + "/" + highest[0] + ": " + highest[1] + " Average Hi: " + averageHi + "\n");
					sb.append("November Lowest Temperature: " + month + "/" + lowest[0] + ": " + lowest[1] + " Average Lo: " + averageLo + "\n");
				} else {
					sb.append("December Highest Temperature: " + month + "/" + highest[0] + ": " + highest[1] + " Average Hi: " + averageHi + "\n");
					sb.append("December Lowest Temperature: " + month + "/" + lowest[0] + ": " + lowest[1] + " Average Lo: " + averageLo + "\n");
				}
				sb.append("--------------------------------------------------------------\n");
				sb.append("Graph\n");
				sb.append("--------------------------------------------------------------\n");
				sb.append("      1   5    10   15   20   25   30   35   40   45   50\n");
				sb.append("      |   |    |    |    |    |    |    |    |    |    |\n");
				sb.append("--------------------------------------------------------------\n");
				for (int i = 0; i < numRows; ++i) {
					sb.append(temperatures[i][0] + " ");
					if (temperatures[i][0] < 10) {
						sb.append("  ");
					} else {
						sb.append(" ");
					}
					sb.append("Hi " + "+".repeat(temperatures[i][1]) + "\n");
					sb.append("    Lo " + "-".repeat(temperatures[i][2]) + "\n");
				}
				sb.append("--------------------------------------------------------------\n");
				sb.append("     |   |    |    |    |    |    |    |    |    |    |\n");
				sb.append("     1   5    10   15   20   25   30   35   40   45   50\n");
				sb.append("--------------------------------------------------------------");

				System.out.println(sb.toString());
				
				File newFile = new File("TemperaturesReportFromDB.txt");
				try {
					if (!newFile.exists()) {
						newFile.createNewFile();
					}
					newFile.createNewFile();
					FileWriter fw = new FileWriter("TemperaturesReportFromDB.txt");
					fw.write(sb.toString().replace("\n", "\r\n"));
					fw.close();
				} catch (IOException e) {
					System.err.println("Couldn't write to file: " + e.getMessage());
				}

			}
		} catch (SQLException e) {
			System.err.println("Couldn't connect to the database: " + e.getMessage());
		}
		
	}
	
}