import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


public class Logger {
	public static void LogMessage(String message)
	{
		try 
		{
			Date dateTime = new Date();
			FileWriter writer = new FileWriter(FileNames.logFileName, true);
			writer.write("\n");
			writer.write(dateTime.toString()+": "+message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
