
/**
 * Name:Sandeeb Adhikari
 * Project: Stack and Queue 
 */
import java.util.Random;

public class Client {
	//global variables for client classes 
	public static Random generator = new Random();
	public static final int NUM_STATIONS = 5;
	public static final int TRAIN_CAPACITY = 50;
	public static final int TRAIN_INTERVAL = 10;
	public static final int TIME_INTERVAL = 200;
	public static int trainCount = 0;
	public static int passengersOnTrains = 0;
	public static int passengersDelivered = 0;
	public static Station[] stations;
	public static QueueInterface<Train> trains;
	public static QueueInterface<Passenger> passenger;
	public static int passengersCreated;
	/**
	 * main method
	 */
	public static void main(String[]args) {
		//creates a list for train station
		stations = new Station[NUM_STATIONS];
		//creates a queue for trains
	    trains = new LinkedQueue<>();
	    //creates a queue for passengers
	    passenger = new LinkedQueue<>();
		randomStations();
		//loop for time interval
		for( int Clock= 0; Clock<TIME_INTERVAL; Clock++) {
			//reports how many passenger are waiting
			passengerWaitingReport(Clock);
			//method to start a new train
			newTrain(Clock);
			//method to create passenger at this interval and updates passenger creates
			randomPassenger();
			//method to move the train in queue
			moveTrainOnQueue(Clock);
		}
		//final report
		finalReport(TIME_INTERVAL);
	}
	
	/**station method
	 */
	public static void randomStations()
	{
		//loops for number of station
		for( int i=0; i < NUM_STATIONS ; i++) {
			//generates random number for time to next station
			//ranging from 5 to 14
			int timeToNextStation = 5+generator.nextInt(9);
			//adds new station to the list 
			stations[i] = new Station(timeToNextStation);
			//outputs statement to confirm station is created with next station
			System.out.println("  Created station "+i+ " time to next train is " + timeToNextStation);
		}
	}
	
	/**new train method
	 * @param Time
	 */
	public static void newTrain(int Time) {
		//checking to see if time is divisible by TRAIN_INTERVAL
		if ((Time % TRAIN_INTERVAL)==0) {
			//if is divisible than add new train to queue
			trains.enqueue( new Train(TRAIN_CAPACITY));
			trainCount++;
		}
		
	}
	
	/*passenger method
	 */
	public static void randomPassenger() {
		//generates random number to determine how many passenger to create
		int createdPassengers = generator.nextInt(5);
		//loops for the number of passenger created
		for(int i =0; i< createdPassengers ; i++) {
			//Initializing start and stop station
			int startStation = 0;
			int stopStation = 0;
			//loops until the start station is less than start station
			while(startStation>= stopStation) {
				//picks a random number for start station and stop station
				startStation = generator.nextInt(NUM_STATIONS);
				stopStation = generator.nextInt(NUM_STATIONS);
			}
			//creates new passengers with start station and stop station
			Passenger passengers = new Passenger(startStation, stopStation);
			//adds passenger to the appropriate start station
			stations[startStation].addPassenger(passengers);
			//add the passenger to the passenger queue
			passenger.enqueue(passengers);
			passengersCreated++;
		}
		
	}
	
	/**move train method
	 * @param clock
	 */
	public static void moveTrainOnQueue(int clock) {
		//stores trainCount variable in local variable for numTrain
		int numTrains = trainCount;
		//loops to process all the trains
		for( int i =0; i< numTrains; i++) {
			//get a train from queue
			Train moveTrain = trains.dequeue();
			//move train
			moveTrain.move();
			//get the time to next station
			int timeToNextStations = moveTrain.timeToNext();
			//checks if the current time is equals to zero
			if(timeToNextStations  == 0) {
				//gets the station number
				int stationNumber =moveTrain.nextStation();
				// unloads passenger at the station
				int unloadedPassengers = moveTrain.unloadPassengers(stationNumber);
				//load passengers from the station
				int loadedPassengers = moveTrain.loadPassengers(stations[stationNumber], clock);
				//updates passenger on train
				passengersOnTrains += loadedPassengers; 
				passengersOnTrains -= unloadedPassengers;
				//updates passengers delivered to station
				passengersDelivered += unloadedPassengers;
			}
			//checks to make sure the next station is valid 
			if (moveTrain.nextStation() <= NUM_STATIONS) {
				//puts the train back in queue
				trains.enqueue(moveTrain);
			}
			else {
				trainCount--;
			}
		}
	}
	
	/**passengers waiting method
	 * @param Time
	 */
	public static void passengerWaitingReport(int Time){
		
		// passenger waiting on train
		int WaitingPassengers = passengersCreated -passengersOnTrains - passengersDelivered;
		//show time and waiting passengers on train
		System.out.println("\n Time Marker " + Time+ "\t waiting: " + WaitingPassengers + "\t on trains: " + passengersOnTrains );

		}
	
	/** Reports the final situations of the trains and passengers waiting
	and some statistics for passengers' wait times.
	@param clock The time that train operations have ceased. */
	
	public static void finalReport(int clock)
			{
			System.out.println("Final Report");
			System.out.println("The total number of passengers is " +
			 passengersCreated);
			System.out.println("The number of passengers currently on a train " +
			 passengersOnTrains);
			System.out.println("The number of passengers delivered is " +
			 passengersDelivered);
			int waitBoardedSum = 0;
			int waitNotBoardedSum = 0;
			for (int i=0; i < passengersCreated; i++)
			{
			Passenger p = passenger.dequeue();
			if(p.boarded())
			waitBoardedSum += p.waitTime(clock);
			else
			waitNotBoardedSum += p.waitTime(clock);
			} // end for
			System.out.println("The average wait time for passengers " +
			 "that have boarded is");
			System.out.println((double)waitBoardedSum/(passengersOnTrains + passengersDelivered));
			System.out.println("The average wait time for passengers that have not yet boarded is");
			System.out.println((double)waitNotBoardedSum /(passengersCreated - passengersOnTrains -passengersDelivered));
			} // end finalReport
}

	
	
