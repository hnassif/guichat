package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * method that returns the current time at an instance
 */
    public class Time {

    	/**
    	 * @return the current time, formatted as HH:mm:ss
    	 */
        public static String main() {
            Calendar calendar = Calendar.getInstance();
            calendar.getTime(); //returns the current time
            return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime()); //formats it as HH:mm:ss
        }

    }