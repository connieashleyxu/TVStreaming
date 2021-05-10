/**
 * tv program menu (changed name to TV program manager)
 *
 * @author Connie Xu
 * @version Apr 19, 2021
 * ITP 265, Spring 2021, Coffee Section
 * Email: caxu@usc.edu
 * Homework 09
 *
 */

public enum TVProgramManager {
	 PRINT_MAP_USERS("Print map: email -> person objects"),
	 PRINT_SERVICE_MAP("Print map:  service -> shows on that service (alphabetically)"),
	 PRINT_MAP_POPULARITY_SHOWS("Print map of tv-shows -> number of people who like the show"),
	 FIND_NUMBER_LIKES("Enter a show and find out how many people like it"),
	 GET_MOST_POPULAR("(Optional EC) Find out the most popular show"),
	 WRITE_LIST_TO_FILE("Enter a service and write the list of show names on that service to a file"),
	 MAKE_USER_FILE("Make user file of people who like a show"),
	 READ_USER_FILE("Read user file based on name of file"),
	 QUIT("Quit");
	    private String description;
	    
	    private TVProgramManager(String d){
	         this.description = d;   
	    }
	    
	    public static TVProgramManager getOption(int num){
	       return  TVProgramManager.values()[num-1];
	       
	    }
	    public static void printMenu(){
	         for(int i = 1 ; i <= TVProgramManager.values().length; i++){
	             System.out.println(i + ": " + TVProgramManager.values()[i-1].description);
	            }
	    }
	    public static int getNumOptions(){
	        return TVProgramManager.values().length;
	    }
}
