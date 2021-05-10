import java.util.ArrayList;

/**
 * streaming enum
 *
 * @author Connie Xu
 * @version Apr 21, 2021
 * ITP 265, Spring 2021, Coffee Section
 * Email: caxu@usc.edu
 * Homework 09
 *
 */

public enum Streaming {
NETFLIX (new String[] {"netflix"}),
HULU(new String[] {"hulu"}),
DISNEY_PLUS(new String[] {"disney", "disney plus", "disney_plus", "disney+"}),
PEACOCK(new String[] {"peacock"}),
HBO_MAX(new String[] {"hbo", "hbo max", "hbo max"}),
AMAZON_PRIME(new String[] {"amazon", "amazon prime", "amazon prime", "amazon prime video"}),
OTHER(new String[] {});
	
	private String[] names;
	
	private Streaming(String[] names) {
		this.names = names;
	}
	
	public static Streaming matchService(String name) {
		Streaming service = OTHER;
		for(Streaming s: Streaming.values()) {
			for(String n: s.names) {
				if(n.equalsIgnoreCase(name))
					service = s;
			}
		}
		
		return service;
	}
	
}
