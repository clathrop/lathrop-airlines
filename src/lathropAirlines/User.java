package lathropAirlines;

import java.io.*;
import java.sql.*;
import java.util.*;

class User {
	static Scanner scan = new Scanner(System.in);
	String input = null;
	int c_id;
	ArrayList<String> layoverLeg = new ArrayList<String>();
	ArrayList<String> flightNumArrayList = new ArrayList<String>();
	 ArrayList<Integer> legIntArray = new ArrayList<Integer>();
	int layoverCount = 0;
	int i;
	Connection con;
	Statement state;
	ResultSet result;
	String q;
	int originCount = 0;
	
	String nextDest;
	int destID;
	
	void connect()
	{
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			 con = DriverManager.getConnection("jdbc:oracle:thin:@edgar2.cse.lehigh.edu:1521:cse241","ccl213",
			 "P823087332");
		}
		catch(Exception  e ) {
			System.out.println(e);
		}
	}
	
	void newUser() throws ClassNotFoundException, SQLException, IOException{
		String email;
		int c_id = 0;
		
		print("Please enter the email you would like associated with this account: ");
		email = scan.next();
		
		try {
			state = con.createStatement();
			q = "select max(c_id) from customer";
			result=state.executeQuery(q);
			while(result.next()) {
				c_id=result.getInt(1)+1;
			}
			System.out.println(c_id);
			q = "insert into customer values("+ c_id + ",'" + email + "'," + null + "," + "0" + "," + "0" + ")";
			result = state.executeQuery(q);
			println("Inserting information..." + "\n" + "Thank you. Your new customer ID is: " + c_id);	
			
	}catch (SQLException e) {
		e.printStackTrace();
	}
		print("Now that you are in the system, what do you want to do?\n\n");
		customerMainMenu(c_id);
		
	}
	
	void printTime(int t){
		
		int hours = t / 60; //since both are ints, you get an int
		int minutes = t % 60;
		
		System.out.printf("%d:%02d", hours, minutes);
		
	}
	
	void customerMainMenu(int c_id){		
		do {  
			System.out.println("\n-=-=-=-=-=-=-=- Main Menu -=-=-=-=-=-=-=-" +
					"\nPress '1' if want to add credit card information. " +
					"This is needed in order to make a reservation.\n" +
					"Press '2' if you want to view your credit card information.\n" +
					"Press '3' if you want to find and/or book a flight.\n" +
					"Press '4' to view your reservations.\n" +
					"Press '5' to exit.\n");
			int ch = scan.nextInt();
			switch(ch) {
			case 1:
				updateCC(c_id);
				break;
			case 2:
				creditCardLookUp(c_id);
				break;
			case 3:
				flightLookup();
				break;
			case 4:
				reservationLookUp(c_id);
				break;
			case 5:
				println("Thank you for using Lathrop Airlines! We hope to fly with you again!");
				System.exit(0);
			default:
				System.out.println("Invalid entry. Try again.");
			}
			//System.out.println("Would you like to continue? (y/n)");    
			//input = scan.next();      
		}
		while(input.equalsIgnoreCase("y"));  
		
		
	}
	
	String addZeros(int num){
		String single0 = "0";
		String double0 = "00";
		
		String tempString = Integer.toString(num);
		//System.out.println("length is: " + tempString.length());
		
		if(tempString.length() == 1){
			//System.out.println("length is: " + tempString.length());
			tempString = double0 + tempString;
			//System.out.println("new string is " + tempString);
		}else if(tempString.length() == 2){
			tempString = single0 + tempString;
			//System.out.println("new string is " + tempString);
		}else{
			//System.out.println("old string didnt change " + tempString);
			return tempString;
		}
		return tempString;
	}
	
	boolean validAirport(String ap){
		try {
			state = con.createStatement();
			q = "select ap_id from airport where ap_id = '" + ap + "'";
			result = state.executeQuery(q);
			if(!result.next()) {
				return false;
			}
			else{
			 do{
				return true;
			}while(result.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	void flightLookup() {
		println("What day are you looking to fly on? (mmdd)");
		String date = scan.next();
		println("Please enter the origin of your flight (in airport code ex. JFK)");
		String origin = scan.next();
		while(validAirport(origin) == false){
			System.out.println("Please enter a valid airport:");
			origin = scan.next();
		}
		println("Please enter the destination of your flight (in airport code ex. LAX)");
		String destination = scan.next();
		while(validAirport(destination) == false){
			System.out.println("Please enter a valid airport:");
			destination = scan.next();
		}
		println("Please enter the maximum number of layovers you wish to have");
		int maxLayover = scan.nextInt();

		String flightNum = date;
		
		try {
			state = con.createStatement();
			q = "select connect_by_root (origin), SYS_CONNECT_BY_PATH (leg_id, ',') \"Legs\"," + 
					"connect_by_iscycle " +
					"from LEG " +
					"where destination = '" + destination + "'" + 
					"start with origin = '" + origin + "'" +
					"connect by nocycle origin = prior destination and level <= " + maxLayover;
			
			result = state.executeQuery(q);
			if(!result.next()) {}
			else{
			 do{
				String legIDs = result.getString("Legs");
				//System.out.println("LegIDS: " + legIDs);
				layoverLeg.add(legIDs);
			}while(result.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
			for(int i = 0 ; i< layoverLeg.size(); i++)
			 {
			     String splitLegs[] = layoverLeg.get(i).split(",");
			     
			     for(int k = 1; k < splitLegs.length; k++){
			    	 int thisLeg = Integer.parseInt(splitLegs[k]);
			    	 
			    	 flightNum = flightNum + addZeros(thisLeg);;
			    	 //System.out.println(thisLeg + "\n");
				}
			     print("\n( " + (i+1) + " )  Flight " + flightNum + ":\n");
			     flightNumArrayList.add(flightNum);
			     flightNum = date;
			     
			     System.out.printf("%-10s%-20s%-20s%-20s%-20s", "LegID", "Origin", "Destination", "Departure Time", "Arrival Time");
			     System.out.println();
			     System.out.println("---------------------------------------------------------------------------------");
			    //System.out.print("Leg");
			     
			     
			     for(int j = 1; j < splitLegs.length; j++){
			    	 
			    	 //System.out.println("j is: " + j);
			    	 //splitLegs[j].replaceAll("\\s","");
			    	 int thisLeg = Integer.parseInt(splitLegs[j]);
			    	 //flightNum = flightNum + thisLeg;
			    	 //System.out.println(thisLeg + "\n");
			    	 
			    	 try {
			 			state = con.createStatement();
			 			q = "select * from leg where leg_id = " + thisLeg;
			 			result = state.executeQuery(q);
			 			if(!result.next()) {}
			 			else{
			 			 do{
			 				String tempOrigin = result.getString("origin");
			 				String tempDestination = result.getString("destination");
			 				int tempDeparture = result.getInt("departure_time");
			 				int tempArrival = result.getInt("arrival_time");
			 				System.out.printf("%-10d%-20s%-20s", thisLeg, tempOrigin, tempDestination);
			 				//print("\t");
			 				printTime(tempDeparture);
			 				print("\t\t\t");
			 				printTime(tempArrival);
			 				println("");

			 			}while(result.next());
						}
			 			//System.out.println("\t\t<---" + flightNum);
			 			
			 			
					} catch (SQLException e) {
						e.printStackTrace();
					}
			     }
			 }
		System.out.println("Would you like to book one of these flights? If so please enter number option seen left of the flight number." +
				"\nTo return to the main menu press '0': ");
		int option = scan.nextInt();
		if(option == 0){
			customerMainMenu(c_id);
		}
		createFlight(option, date);
		createLegInstances(option, date);
		bookFlight(option, date);
		
		
	}
	
	void bookFlight(int option, String date){
		int res_id = 0;
		ArrayList<String> customerResEmail = new ArrayList<String>();
		println("How many people would you like to book on this flight?");
		int numSeats = scan.nextInt();
		println("Enter each person's email who will be flying on this plane. Include yourself (if you are flying) and hit enter after each one");
		for(int i = 0; i < numSeats; i ++){
			String email = scan.next();
			customerResEmail.add(email);
		}
		
		try{
		for(int i = 0; i < customerResEmail.size(); i++ ){
		state = con.createStatement();
		q = "select email, c_id from customer where email = '" + customerResEmail.get(i) + "'";
		result = state.executeQuery(q);
		
		if (!result.next()){
				state = con.createStatement();
				q = "select max(c_id) from customer";
				ResultSet result1 = state.executeQuery(q);
				while(result1.next()) {
					c_id=result1.getInt(1)+1;
				}
				//System.out.println(c_id);
				q = "insert into customer values("+ c_id + ",'" + customerResEmail.get(i) + "'," + null + "," + "0" + "," + "0" + ")";
				result1 = state.executeQuery(q);
		}
		 do {
			 state = con.createStatement();
				q = "select max(res_id) from reservation";
				ResultSet result2 = state.executeQuery(q);
				while(result2.next()) {
					res_id=result2.getInt(1)+1;
				}
				//System.out.println("res_id: " + res_id);
				q = "insert into reservation values("+ res_id + ",'" + flightNumArrayList.get(option) + "','" + date + "','" + customerResEmail.get(i) + "')";
				result2 = state.executeQuery(q);
		 } while (result.next());
		System.out.println("Thank you, your reservations have been made");
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	void createLegInstances(int option, String date) {
		ArrayList<Integer> seatsArrayList = new ArrayList<Integer>();

		for(int i = 0; i < legIntArray.size(); i++){
		try {
			state = con.createStatement();
			q = "select plane_id, num_seats, leg_id from leg " +
					"natural join PLANE natural join PLANE_TYPE " +
					"where leg_id = " + legIntArray.get(i);
			result = state.executeQuery(q);
			if (!result.next()){ 
			}
			 else {
			 do {
				int seats = result.getInt("num_seats");
				seatsArrayList.add(seats);
			 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		}
		
		for(int i = 0; i < legIntArray.size(); i++){
		try {
			state = con.createStatement();
			q = "insert into leg_instance values( " + legIntArray.get(i) + ",'" + date + "'," + seatsArrayList.get(i) + ")";
			result = state.executeQuery(q);
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		}
	
	}
	
	void creditCardLookUp(int c_id){
		try {
			state = con.createStatement();
			q = "select * from billing where c_id = " + c_id;
			result = state.executeQuery(q);
			 System.out.printf("%-20s%-20s%-20s%-20s%-20s", "CC#", "First Name", "Last Name", "City", "Country");
		     System.out.println();
		     System.out.println("----------------------------------------------------------------------------------------");
			if (!result.next()){ 
				System.out.println("Sorry, you do not have any credit cards on file at this time, returning to main menu.");
				customerMainMenu(c_id);
			}
			 else {
			 do {
				 long ccnum = result.getLong("cc_num");
				 String fname = result.getString("fname");
				 String lname = result.getString("lname");
				 String city = result.getString("city");
				 String country = result.getString("country");
				 System.out.printf("%-20d%-20s%-20s%-20s%-20s", ccnum, fname, lname, city, country);
				 println("");
					
			 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		customerMainMenu(c_id);
	}
	
	void reservationLookUp(int c_id){
		try {
			state = con.createStatement();
			q = "select email from customer where c_id = " + c_id;
			result = state.executeQuery(q);
			 System.out.printf("%-20s%-20s%-20s", "ResID", "Date", "Flight Number");
		     System.out.println();
		     System.out.println("---------------------------------------------");
			if (!result.next()){ 
			}
			 else {
			 do {
				 String email = result.getString("email");
				 state = con.createStatement();
				 q = "select * from reservation where email = '" + email + "'";
				 ResultSet result1 = state.executeQuery(q);
					if (!result1.next()){ 
						System.out.println("Sorry, you do not have any reservations at this time, returning to main menu.");
						customerMainMenu(c_id);
					}
					 else {
					 do {
						 int res_id = result1.getInt("res_id");
						 String flight_num = result1.getString("flight_num");
						 String date = result1.getString("date");
						 
						 System.out.printf("%-20d%-20s%-20s", res_id, date, flight_num);
						
						 
					 } while (result1.next());
					 }
				
				 
			 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		customerMainMenu(c_id);
	}

	private void createFlight(int option, String date) {
		//flightNumArrayList has the flights from the options before
		
		try {
			state = con.createStatement();
			q = "select flight_num from flight where flight_num = " + flightNumArrayList.get(option);
			result = state.executeQuery(q);
			if(result.next()) {
				System.out.println("already exists");
				return;
				}
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
			 String legString = "";
		     String splitLegs[] = layoverLeg.get(option).split(",");
		     ArrayList<String> destArrayList = new ArrayList<String>();
		     ArrayList<String> originArrayList = new ArrayList<String>();
		     int price = 0;
		     
		     for(int k = 1; k < splitLegs.length; k++){
		    	 int thisLeg = Integer.parseInt(splitLegs[k]);
		    	 legString += thisLeg + " ";
		    	 legIntArray.add(thisLeg);
		 }
		     
		     try {
					
					//fill dest and origin array lists
				for(int i = 0; i < legIntArray.size(); i++){
					state = con.createStatement();
					q = "select * from leg where leg_id = " + legIntArray.get(i);
					result = state.executeQuery(q);
					if(!result.next()) {}
					else{
					 do{
						destArrayList.add(result.getString("destination"));
						//System.out.print("dest: " + result.getString("destination"));
						originArrayList.add(result.getString("origin"));
						//System.out.print("origin: " + result.getString("origin"));

					}while(result.next());
					}
					}
				
				for(int i = 0; i < legIntArray.size(); i++){
				state = con.createStatement();
				q = "select * from price where ap_id1 = '" + originArrayList.get(i) +
						"' and ap_id2 = '" + destArrayList.get(i) + "'";
				result = state.executeQuery(q);
				if(!result.next()) {}
				else{
				 do{
					//System.out.println("price: " + result.getInt("price"));
					price += result.getInt("price");
				}while(result.next());
				}
				
				}
				//System.out.println("price: " + price);
					
					state = con.createStatement();
					q = "insert into flight values('"+ flightNumArrayList.get(option) + "','" + legString + "','" + originArrayList.get(0) + "','"
					+ destArrayList.get(destArrayList.size()-1) + "'," + price + ")";
					result = state.executeQuery(q);
							
			}catch (SQLException e) {
				e.printStackTrace();
			}
		     
		     System.out.println("flight created");
		
		//flightNumArrayList.clear();
		//layoverLeg.clear();
	}

	private void updateCC(int c_id) {
		long cc_num;
		String fname, lname, street_address, city, postal_zip, country;
		scan.nextLine();
		
		println("Enter First Name: ");
		fname = scan.nextLine();
		println("Enter Last Name: ");
		lname=scan.nextLine();
		println("Enter Street Address: ");
		street_address = scan.nextLine();
		println("Enter City: ");
		city=scan.nextLine();
		println("Enter Postal Zip Code: ");
		postal_zip =scan.nextLine();
		println("Enter Country: ");
		country =scan.nextLine();
		println("Enter Credit Card Number: ");
		cc_num = scan.nextLong();
		
		try {
			state = con.createStatement();
			
			q = "insert into billing values("+ cc_num + ",'" + fname + "','" + lname + "','"
			+ street_address + "','" + city + "','" + postal_zip + "','" + country + "'," + c_id + ")";
			
			result = state.executeQuery(q);
			
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		println("Your Credit Card information has been updated");
		customerMainMenu(c_id);
		
	}

	void welcome() throws ClassNotFoundException, SQLException, IOException{

		print("----------------------------------------\n"
				+ "Hello and Welcome to Lathrop Airlines! \n"
				+ "----------------------------------------\n\n"
				+ "Are you a returning customer (y/n)?: ");
		
	        ynCheck(scan);
	        input = scan.next();

		if (input.equalsIgnoreCase("n")) {
			print("An account is needed to book a flight. Would you like to create an account now?(y/n): ");
			ynCheck(scan);
	        input = scan.next();
	        
			if (input.equalsIgnoreCase("y")) {
				newUser();

			} else if (input.equalsIgnoreCase("n")) {
				println("That's too bad D: Good Bye ");
				System.exit(0);
			}

		} else if (input.equalsIgnoreCase("y")) {
			print("Great, please enter your email to log in: ");
			
			input = scan.next();
			login(input);
			//customerMainMenu(c_id);
			
		}
		
	}
	
	
	private void login(String emailID) {
		try {
			state = con.createStatement();
			q = "select c_id from customer where email = '" + emailID + "'";
			result = state.executeQuery(q);
			
			if (!result.next()){ 
				System.out.print("\nIncorrect email, please try again: ");
				input = scan.next();
				login(input);
			}
			 else {
			 do {
				 int c_id = result.getInt("c_id");
				 System.out.println ("You are logged in as c_id: " +  c_id);			 
				 customerMainMenu(c_id);
			 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
	}

	public static void print(String str){
		System.out.print(str);
	}
	public static void println(String str){
		System.out.println(str);
	}

	static void ynCheck(Scanner s) {
		while (!s.hasNext("[ynYN]")) {
            System.out.print("enter y or n: ");
            s.next(); 
        }
	}
	
	public static void main(String[] arg) throws SQLException, IOException,
			java.lang.ClassNotFoundException {
		
		User user = new User();
		user.connect();
		user.welcome();
		//String date = "1214";
		//user.createFlight(1, date);


	}
	
	

}