import java.text.*;
import java.util.*;

import javax.mail.*;

public class ImapTool
{
	final static SimpleDateFormat logTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static void log(String msg)
	{
		System.out.println(logTimeFormat.format(new Date()) + " - " + msg);
	}

	private static void printUsage()
	{
		final String usage =
			"ImapTool folders url [parent] - print folders [under parent]\n" +
			"\turl - imap server url\n" +
			"\tparent - parent folder\n" +
			"url := imap://user:password@host[:port]\n" +
			"\tuser and password are urlencoded (if needed)\n" +
			"folder := folder | folder.subfolder | ...";

		System.out.println(usage);
	}

	private static Store getStore(String url)
		throws Exception
	{
		return Session.getDefaultInstance(new Properties())
			.getStore(new URLName(url));
	}

	private static void listFolders(String url, String parent)
		throws Exception
	{
		log("listing folders, url: " + url + ", parent: " + parent);

        final Store store = getStore(url);
        store.connect();
		log("connected to imap store");

		final Folder folder = (parent == null) ?
			store.getDefaultFolder() : store.getFolder(parent);
		log("got parent / default folder");

		for (Folder f : folder.list())
			System.out.println(f.getName());

		log("closeing");
		store.close();
	}

	public static void main(String params[])
		throws Exception
	{
		if (params.length == 0) {
			printUsage();
			return;
		}

		log("ImapTool started");

		if (params[0].equals("folders"))
		{
			if (params.length == 1)
				throw new RuntimeException("missing url parameter");

			listFolders(params[1], params.length == 3 ? params[2] : null);
		}
	}
}
