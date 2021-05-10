import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * tv show main program
 *
 * @author Connie Xu
 * @version Apr 21, 2021
 * ITP 265, Spring 2021, Coffee Section
 * Email: caxu@usc.edu
 * Homework 09
 *
 */

public class TVShowMainProgram {

	private BFF bff;
	private List<TVWatcher> people; // a simple list of all the data
	private List<TVShow> shows; // a list of shows, with duplicates combined

	private Map<String, TVWatcher> userMap;
	private Map<TVShow, Integer> showCount ;
	private Map<Streaming, List<TVShow>> serviceMap ;

	public TVShowMainProgram() {
		bff = new BFF();
		people = new ArrayList<>();
		shows = new ArrayList<>();
		readInitialDataFromFile();
		shows = collapseDuplicates(shows);
		setupMaps(); // after the data is read in and the lists are made, set up the maps

	}
	public void setupMaps() {
		userMap =  makeUserMap();
		showCount =  makePopularityMap();
		serviceMap =  makeServiceMap();
	}

	////////////////////////////////////////////////////////////
	// STEP 1: Handling errors 
	////////////////////////////////////////////////////////////
	private void readInitialDataFromFile() {
		String file = "bin/tvShowFormData.tsv";
		ArrayList<String> data = FileReader.readFile(file);
		String header = data.get(0);
		System.out.println("Data is formatted like the following: \n" + header);

		for(int i = 1; i < data.size(); i++) {
			String line = data.get(i);
			TVWatcher person = parseDataLineToStudentWatcher(line);
			if(person != null) { // this check will go away once custom exception is in place
				people.add(person);
				for(TVShow show: person.getFavorites()) {
					this.shows.add(show);
				}
			}
		}

	}


	private TVWatcher parseDataLineToStudentWatcher(String lineOfFile) {
		//Timestamp	Email Address	Your first name	Your last name	Which section are you in?	
		//Your number 1 favorite tv show	Which streaming service is it on?	Your number 2 favorite tv show	Which streaming service is it on?	Your number 3 favorite tv show	Which streaming service is it on?
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(lineOfFile);
		sc.useDelimiter("\t");

		try {
			String email = sc.next();
			String fname = sc.next();
			String lname = sc.next();
			String section = sc.next().toUpperCase();
			ArrayList<TVShow> shows = new ArrayList<>();

			//additional loop allows to getting 1+ shows from the line
			while(sc.hasNext()) {
				String show = sc.next().toLowerCase();
				if(! show.isBlank()) {
					char let = show.charAt(0);
					String cap = (let+"").toUpperCase();
					show = cap + show.substring(1);
					String platform = sc.next(); // platforms could have multiple items
					String[] str_array = platform.split(",");
					Streaming[] services = new Streaming[str_array.length];
					for(int i = 0; i < str_array.length; i++) {
						services[i] = Streaming.matchService(str_array[i]);
					}
					shows.add(new TVShow(show, services));
				}
				// else : Missing data for rest of show, not adding
			}
			TVWatcher watcher = new StudentTVWatcher(email, fname, lname, shows, Section.valueOf(section));
			return watcher;
		}
		catch (Exception e) {
			System.err.println("An exception happened");
			e.printStackTrace();
			return null; // this should be removed when you throw an exception in the solution
		}
	}


	////////////////////////////////////////////////////////////
	//STEP 2: Commenting Helper Methods
	////////////////////////////////////////////////////////////

	//TODO: Comment (For Step 2)
	//show names and then make sure that there are no duplicate tv shows. if the tv show matches and so does the name, do not add. but if not, then add it to the display. this method ensures that there aren't multiple responses of the same thing that is showcased
	/*
	 * checks to make sure there are no duplicates listed; if there are, it collapses them into one item
	 * @param tv show list
	 * @return noDups
	 */
	private List<TVShow> collapseDuplicates(List<TVShow> allData) {
		Set<String> showNames = new HashSet<>();
		List<TVShow> noDups = new ArrayList<>();
		for(TVShow show : allData) {
			String name = show.getName();
			if(showNames.contains(name)) {
				// showName is in set, does the show match one in the list?
				if(!noDups.contains(show)) {
					//show name matches, but the TVShow object is different, so match up services
					TVShow other = findMatch(name, noDups);
					for(Streaming serv: show.getServices()) {
						other.addSevice(serv);
					}

				}
			}
			else {
				showNames.add(name);
				noDups.add(show);

			}
		}
		return noDups;
	}

	//TODO: Comment (For Step 2)
	//finding matches for the tv show and name in the data. gets rid of case when finding a match. used for displaying info the user requests.
	/* takes in name of show and list of show to find shows with the same name before returning matches or null in the case of no matches
	 * @param name, all Shows
	 * @return match show to name
	 */
	private TVShow findMatch(String name, List<TVShow> allShows) {
		TVShow match = null;
		int i = 0;
		while (i < allShows.size() && match == null) {
			TVShow show = allShows.get(i);
			if(show.getName().equalsIgnoreCase(name)) {
				match = show;
			}
			i++;
		}
		return match;
	}
	////////////////////////////////////////////////////////////
	//STEP 3: Making maps
	////////////////////////////////////////////////////////////

	/*
	 * makes hashmap that links to email for the watcher object in the list of people
	 */
	private Map<String, TVWatcher> makeUserMap() {
		//TODO
		Map<String, TVWatcher> emailAndWatcher = new HashMap<String, TVWatcher>(people.size());
		for(TVWatcher p : people) {
			emailAndWatcher.put(p.getEmail(), p);
		}
		
		return emailAndWatcher;
	}

	/*
	 * creates hashmap that links streaming service to list of shows on that service
	 */
	private Map<Streaming, List<TVShow>> makeServiceMap() {
		//TODO
		Map<Streaming, List<TVShow>> serviceAndShows = new HashMap<Streaming, List<TVShow>>(shows.size());
		
		for(Streaming serv : Streaming.values()) {
			List<TVShow> servShows = new ArrayList<>();
			for(TVShow show: shows) {
				for(Streaming s : show.getServices()) {
					if(serv == s) servShows.add(show);
				}
			}
			serviceAndShows.put(serv,  servShows);
		}
		return serviceAndShows;
	}

	/*
	 * creates hashmap that links tv show to an integer that shows how popular the show is in the two polled classes
	 */
	private Map<TVShow, Integer> makePopularityMap() {
		//TODO
		Map<TVShow, Integer> popMap = new HashMap<>(shows.size());
		
		for(TVShow show : shows) {
			int showCount = 0;
			for(TVWatcher s : people) {
				for(TVShow pop : s.getFavorites()) {
					if(show.equals(findMatch(pop.getName(), shows))) {
						showCount++;
					}
				}
			}
			popMap.put(show, showCount);
		}
		return popMap;
	}

	public void run(){
		bff.print("Welcome to the program for exploring tv shows");

		boolean keepGoing = true;
		while(keepGoing){
			TVProgramManager.printMenu();
			int num = bff.inputInt(">", 1,  TVProgramManager.getNumOptions());
			TVProgramManager option = TVProgramManager.getOption(num);
			switch(option){

			case PRINT_MAP_USERS: 
				printUserMap();
				break;
			case  PRINT_SERVICE_MAP: 
				printServiceMap();
				break;
			case PRINT_MAP_POPULARITY_SHOWS: 
				printPopularityMap();
				break;
			case FIND_NUMBER_LIKES: 
				//TODO
				System.out.println("Find number likes not yet implemented");
				break;
			case GET_MOST_POPULAR: //optional
				//TODO
				System.out.println("Get most popular not yet implemented (and is extra credit");
				break;
			case WRITE_LIST_TO_FILE: 
				//TODO
				System.out.println("Make shows on a streaming service file not yet implemented");
				break;
			case MAKE_USER_FILE: // ("Make user file of people who like a show"),
				//TODO
				System.out.println("Make user file not yet implemented");
				break;
			case READ_USER_FILE: // ("Read user file based on name of file"),
				//TODO
				System.out.println("Read user file not yet implemented");
				break;
			case QUIT   :  keepGoing = false; break;
			}
		}
		bff.print("Goodbye");
	}


	////////////////////////////////////////////////////////////
	//STEP 4: Methods for the TVProgram Menu
	////////////////////////////////////////////////////////////
	private void printUserMap() {
		System.out.println("User Map");
		for(Map.Entry<String, TVWatcher> pair : userMap.entrySet()) {
			bff.print(" " + pair.getKey() + ": " + pair.getValue());
		}
	}
	private void printServiceMap() {
		System.out.println("Service Map");
		for(Map.Entry<Streaming, List<TVShow>> pair : serviceMap.entrySet()) {
			bff.print(pair.getKey().name() + ">");
			List<TVShow> servShows = pair.getValue();
			Collections.sort(servShows);
			for(TVShow show : servShows) {
				bff.print(" " + show);
			}
		}
	}

	private void printPopularityMap() {
		bff.print("Popularity Map");
		for(Map.Entry<TVShow, Integer> pair : showCount.entrySet()) {
			bff.print(" " + pair.getKey() + ": " + pair.getValue());
		}
	}

	//TODO: other methods for step 4 can be included here.
	/*
	 * makes user file with show and users that like given show (with email and name)
	 */
	public void writeToFile(String fileName, ArrayList<String> commonUsers) {
		try {	
				String content = " " + commonUsers;
                File file = new File(fileName + ".txt");

	            if(!file.exists()){
	                file.createNewFile();
	            }
	
	            FileWriter fw = new FileWriter(file);
	
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(content);
	            bw.close();
		      
		        System.out.println("Successfully wrote to the file.");
		    } 
			catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
			}
	}
	
	public void makeUserFile() {
		TVShow commonShow = findMatch(bff.inputLine("Enter show you want to find a match for."), shows);
		ArrayList<String> commonUsers = new ArrayList<>();
		for(TVWatcher p : people) {
			for(TVShow s : p.getFavorites()) {
				if(commonShow.getName().equalsIgnoreCase(s.getName())){
					commonUsers.add(p.getEmail() + "/" + p.getName());
				}
			}
		}
		String fileName = commonShow.getName() + ".txt";
		writeToFile(fileName, commonUsers);
		bff.print("matching users in " + fileName);
	}
	
	/*
	 * provide name of file and adds users tv shows to rec list to be printed
	 */
	public void readUserFile() {
		String showName = bff.inputLine("Enter name of file you want to view");
		String fileName = showName + ".txt";
		
		ArrayList<String> data = FileReader.readFile(fileName);
		
		if(!(data.size() == 0)){
			bff.print("Based on people who liked " + fileName.replace(".txt", "") + " you may also like: ");
			
			List<TVShow> tvRecs = new ArrayList<>();
			
			for(int i = 0; i < data.size(); i++){
				String line = data.get(i);
				String pEmail = line.split("/")[0];
				
				tvRecs.addAll(userMap.get(pEmail).getFavorites());
			}
			
			tvRecs = collapseDuplicates(tvRecs);
			tvRecs.remove(findMatch(showName, shows));
			
			for(TVShow show : tvRecs) {
				bff.print(" " + show);
			}
		}
	}
	
	/*
	 * prints popularity of show based on user request
	 */
	public void findNumberLikes() {
		String name = bff.inputLine("Enter show name for number of likes.");
		TVShow show = findMatch(name, this.shows);
		if(!(show == null)) {
			bff.print("There are " + showCount.get(show) + " people that like that show.");
		}
		else bff.print("Show not in data set.");
	}

	public static void main(String[] args) {
		TVShowMainProgram program = new TVShowMainProgram();
		program.run();

	}
}
