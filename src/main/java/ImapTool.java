import java.text.*;
import java.util.*;

public class ImapTool
{
	final static SimpleDateFormat logTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static void log(String msg)
	{
		System.out.println(logTimeFormat.format(new Date()) + " - " + msg);
	}

	public static void main(String params[])
	{
		log("ImapTool started");
	}
}
