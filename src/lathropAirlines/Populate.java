package lathropAirlines;

import java.io.*;
import java.sql.*;
import java.util.*;


class Populate {
	
	Connection con;
	Statement state;
	ResultSet result;
	String q;
	ArrayList<String> airports1 = new ArrayList<String>();
	//ArrayList<String> airports2 = new ArrayList<String>();
	ArrayList<String> time = new ArrayList<String>();
	ArrayList<String> planeModels = new ArrayList<String>();
	ArrayList<Integer> availablePlanes = new ArrayList<Integer>(); 
	boolean fail = false;
	int x = 0;
	
	
	
	public static void main (String[] arg) 
			  throws SQLException, IOException, java.lang.ClassNotFoundException {
				  Populate populate = new Populate(); 
				  populate.connect();
			      populate.getAirports();
			      //populate.popInFlight();
			      //populate.printAirports();
			      //populate.createTimes();
			      //populate.populatePlanes();
			      //populate.createLeg();
			      //populate.populatePilotLocation();
			      //populate.putPilotOnPlane();
			      //populate.createPrices();
			      
//			      for(int i=0;i<20;i++){
//			    	  populate.createLeg();
//			      }
		      
			      System.out.println("done");
			  }
	
	
	
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
	
	void getAirports(){
		//System.out.println("in getAirports");
		try {
			state = con.createStatement();
			q = "select * from airport";
			result = state.executeQuery(q);
			if (!result.next()) System.out.println ("Empty result.");
			 else {
			 do {
				 //System.out.println (result.getString("ap_id"));
				 airports1.add(result.getString("ap_id"));
				 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
		
	}
	
	int layoverTime(int time){
		Random rand = new Random();

			int random = rand.nextInt(120 - 60) + 60;
			
		 return time + random;
	}
	
	int flightTime(int time){
		Random rand = new Random();

			int random = rand.nextInt(360 - 90) + 90;
			
		 return time + random;
	}
  

  void printAirports(){  
	  for (String s : airports1){
		  System.out.println(s);
	  }
}
  
  
  void createLeg() {
		Random rand = new Random();
		int destinationRand, originRand;
		int leg_id = 0;
		String origin=null;
		String destination=null;
		int est_arrival = 0;
		
		int arrival_time =0;
		int departure_time =0;
		int plane_id = 0;
		try {
			//for(int i= 0;i<20;i++){
				originRand = rand.nextInt(airports1.size());
				origin = airports1.get(originRand);
				
				destinationRand = rand.nextInt(airports1.size());
				destination = airports1.get(destinationRand);
				
				if(destination == origin){
					while(destination == origin){
						destinationRand = rand.nextInt(airports1.size());
						destination = airports1.get(destinationRand);
					}
				}
				
				state = con.createStatement();
				q = "select plane_id, est_arrival from plane natural join plane_type I where plane_loc = '"
						+ origin + "' and exists(select * from airport J where J.ap_id = '"+
						destination+"' and I.runway_requirement < J.runway_length)";
				ResultSet result1 = state.executeQuery(q);
				if(result1.next()){
					est_arrival = result1.getInt("est_arrival");
					plane_id = result1.getInt("plane_id");
					if(plane_id!=0 && !(destination == origin) && est_arrival>=300 && est_arrival<(1000)){

						departure_time = layoverTime(est_arrival);
						arrival_time = flightTime(departure_time);
						state = con.createStatement();
						q = "select max(leg_id) from leg";
						ResultSet result2 = state.executeQuery(q);
						while (result2.next()) {
							leg_id = result2.getInt(1) + 1;
						}
						System.out.println("Plane ID:" + plane_id);
						System.out.println("Leg Number: "+ leg_id);
						System.out.println("Est Arrival:" + est_arrival);
						System.out.println("Origin:" + origin);
						System.out.println("Dest:" + destination);
						state = con.createStatement();
						q = "insert into leg values(" + leg_id + ",'" + plane_id
								+ "','" + origin + "','" + destination + "'," + departure_time
								+ "," + arrival_time + "," + null + "," + null + ","+null+")";
						result2 = state.executeQuery(q);
						
						state = con.createStatement();
						q = "update plane set plane_loc = '"+destination+"' where plane_id ="+plane_id;
						result2 = state.executeQuery(q);
						
						state = con.createStatement();
						q = "update plane set est_arrival = "+arrival_time+" where plane_id = "+plane_id;
						result2 = state.executeQuery(q);
				
						}
				}

			}catch (SQLException e) {
			//System.out.println("Broke");
			e.printStackTrace();
		}
}
  
  
//  void createLeg() {
//		Random rand = new Random();
//		int max, min;
//		int destinationRand, originRand;
//		int leg_id = 0;
//		String origin;
//		String destination;
//		int arrival_time = 0;
//		int departure_time = 0;
//		//ArrayList<Integer> availablePlanes = new ArrayList<Integer>();
//		int est_arrival = 0;
//		String plane_loc = null;
//		int plane_id =0 ;
//		
//
//		max = 27;
//		min = 0;
//
//		originRand = rand.nextInt(max - min + 1) + min;
//		destinationRand = rand.nextInt(max - min + 1) + min;
//		origin = airports1.get(originRand);
//		destination = airports1.get(destinationRand);
//		
//		try {
//		if(x == 0){ 
//		originRand = rand.nextInt(max - min + 1) + min;
//		origin = airports1.get(originRand);
//		plane_loc = origin;
//		}
//		else{
//
//			state = con.createStatement();
//			q = "select plane_loc from plane";
//			result = state.executeQuery(q);
//			while(result.next()){
//				plane_loc = result.getString("plane_loc");
//				System.out.println("Starting Location: " + plane_loc);
//				if (destination.equals(plane_loc)) {
//					do {
//						destinationRand = rand.nextInt(max - min + 1) + min;
//					} while (destination.equals(plane_loc));
//				}
//		
//		try {
//			//select the planeIDs that can travel from plan_loc to dest
//			state = con.createStatement();
//			q = "select plane_id, est_arrival from plane natural join plane_type I where plane_loc = '"
//					+ plane_loc + "' and exists(select * from airport J where J.ap_id = '"+
//					destination+"' and I.runway_requirement < J.runway_length)";
//			ResultSet result1 = state.executeQuery(q);
//			//System.out.println("after result1");
//			
//			while(result1.next()){
//				plane_id = result1.getInt("plane_id");
//				System.out.println("PlaneID: " + plane_id);
//				est_arrival = result1.getInt("est_arrival");
//				System.out.println("Estimated Arrival: " + est_arrival);
//				
//
//			//add 30-180 mins
//			departure_time = layoverTime(est_arrival);
//			arrival_time = flightTime(departure_time);
//			
//			
//			
//				state = con.createStatement();
//				q = "select max(leg_id) from leg";
//				ResultSet result2 = state.executeQuery(q);
//				while (result2.next()){
//				leg_id = result2.getInt(1) + 1;
//				System.out.println("LegID: " + leg_id);
//				}
//				
//				if(est_arrival < 1300 & est_arrival > 299){
//				state = con.createStatement();
//				
//				q = "insert into leg values(" + leg_id + "," + plane_id
//						+ ",'" + plane_loc + "','" + destination + "'," + departure_time
//						+ "," + arrival_time + "," + null+ "," + null + ")";
//				ResultSet result3 = state.executeQuery(q);
//				
//				state = con.createStatement();
//				q = "update plane set plane_loc = '"+ destination + "' where plane_id =" + plane_id;
//				result3 = state.executeQuery(q);
//				
//				state = con.createStatement();
//				q = "update plane set est_arrival = "+arrival_time+" where plane_id = "+plane_id;
//				result3 = state.executeQuery(q);
//				//System.out.println("PlaneID: " + plane_id + "\n PlaneLoc: " + plane_loc);
//			}else{
//				
//			}
//			}
//			} catch (SQLException e) {
//				//System.out.println("broke");
//				e.printStackTrace();
//			}
//			}
//		}
//			
//		} catch (SQLException e) {
//			//System.out.println("broke");
//			e.printStackTrace();
//		}
//		
//	x = 1;
//		
//}
			
		
  void populatePlanes(){
	  Random rand = new Random();
	  int plane_id = 0;
	  int est_arrival = 300;
	  
	  try {
			
			//get plane models
			state = con.createStatement();
			q = "select model_num from plane_type";
			result = state.executeQuery(q);
			
			//populate planes array models
			while(result.next()) {
				planeModels.add(result.getString("model_num"));
				//System.out.println(result.getString("model_num"));
			}
						
			
			//System.out.println(planeModels.size());
			for(int i = 0; i<airports1.size();i++){
				String apt = airports1.get(i);
				for(int j = 0;j<4;j++){
					state = con.createStatement();
					q = "select max(plane_id) from plane";
					result=state.executeQuery(q);
					while(result.next()) {
						plane_id = result.getInt(1)+1;
						System.out.println(plane_id);
					}
					int randmiles = rand.nextInt(10000000) + 10000000;
					int modelRand = rand.nextInt(planeModels.size());
					String planeType = planeModels.get(modelRand);
					System.out.println(planeType);
					
					state = con.createStatement();
					q = "insert into plane values("+ plane_id +","+randmiles+","+null+",'"+planeType+"','"+apt+ "'," + est_arrival+ ")";
					result = state.executeQuery(q);
					
					
					
				}
			}
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
	  
  }
  
  void populatePilotLocation(){
	  int randInt;
	  Random rand = new Random();
	  
	  int max = 27;
	  int min = 0;
	  
	  try {
		  	state = con.createStatement();
			q = "select pilot_id, fname from pilot";
			System.out.println("after query");
			result=state.executeQuery(q);
			if (!result.next()) System.out.println ("Empty result.");
			 else {
			 do {
				 int pilot_id = result.getInt("pilot_id");
				 String fname = result.getString("fname");
				 System.out.println(fname);
				 System.out.println(pilot_id);
					randInt = rand.nextInt(airports1.size());
					String pilotLocation = airports1.get(randInt);
					state = con.createStatement();
					q = "update pilot set location = '" + pilotLocation + "' where pilot_id = " + pilot_id;
					ResultSet result1 = state.executeQuery(q);
				 } while (result.next());
			 }
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
  
  }
  
  void popInFlight(){
		int pilot_id;
		  try {
			  	state = con.createStatement();
				q = "select pilot_id from pilot";
				result=state.executeQuery(q);
				while(result.next()) {
					pilot_id = result.getInt("pilot_id");
					System.out.println(pilot_id);
					state = con.createStatement();
					q = "update pilot set pilot_arrival = 0000 where pilot_id = " + pilot_id;
					ResultSet result1 = state.executeQuery(q);
				}			
		}catch (SQLException e) {
			e.printStackTrace();
		}
	  
	  }
  
  void putPilotOnPlane(){
	  int pilot_id;
	  int leg_id;
	  String origin;
	  String certified_for;
	  String flight_class;
	  int pilot_arrival;
	  String destination;
	  
	  
	  try {
		  
	  state = con.createStatement();
	  q = "select leg_id, origin from leg where pilot_id is null";
	  result = state.executeQuery(q);
	  
		 while(result.next()){
			 leg_id = result.getInt("leg_id");
			 origin = result.getString("origin");
			 
			 state = con.createStatement();
			 
			 q = "select pilot.pilot_id, departure_time, destination, pilot_arrival, arrival_time, plane_id, certified_for, flight_class from" +
                     " pilot inner join leg on location = '" + origin + "' natural join plane natural" +
                                     " join plane_type where leg_id = "+ leg_id;
			 
				ResultSet result1 = state.executeQuery(q);
				
					 while(result1.next()){
							pilot_id = result1.getInt("pilot_id");
							flight_class = result1.getString("flight_class");
							certified_for = result1.getString("certified_for");
							pilot_arrival = result1.getInt("pilot_arrival");
							destination = result1.getString("destination");
							int depart = Integer.parseInt(result1.getString("departure_time"));
							int arrive = Integer.parseInt(result1.getString("arrival_time"));
							if((flight_class.equals(certified_for)|certified_for.equals("both")) && depart > pilot_arrival){
								
								state = con.createStatement();
								q = "update leg set pilot_id = "+ pilot_id +" where leg_id = "+leg_id;
								ResultSet result2 = state.executeQuery(q);
								System.out.println(leg_id+" is now flown by +"+pilot_id);
								
								state = con.createStatement();
								q = "update pilot set pilot_arrival = "+arrive+" where pilot_id ="+pilot_id;
								ResultSet result3 = state.executeQuery(q);
								System.out.println(pilot_id+" will be arriving in "+origin+" at "+arrive);
								state = con.createStatement();
								q = "update pilot set location = '"+ destination +"' where pilot_id ="+pilot_id;
								ResultSet result4 = state.executeQuery(q);
								break;
							}
						}
					 
					 
					 }
		 	
		 
					
	}catch (SQLException e) {
		e.printStackTrace();
	}
  
  }
  
  void createPrices(){
	  int i, j, k;
	  String tempAirport1;
	  String tempAirport2;
	  Random rand = new Random();

		
	  
	  try {
		
		//for(i = 0; i < airports1.size(); i++){
			tempAirport1 = airports1.get(27);
			//System.out.println(i + " " + tempAirport1);
		for(j = 0; j < airports1.size(); j++){
			//System.out.println(airports1.size());
			tempAirport2 = airports1.get(j);
			System.out.println(j + " " + tempAirport2);
			int price = rand.nextInt(500 - 100) + 100;
			if(tempAirport1.equals(tempAirport2)){
			}else{
			state = con.createStatement();
			q = "insert into price values('" + tempAirport1 + "','" + tempAirport2 + "'," + price + ")";
			result = state.executeQuery(q);
			}
		}
		//}
	} catch (SQLException e) {
				e.printStackTrace();
	}
	  
	  
  }
  
  
}

