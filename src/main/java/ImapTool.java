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
			"ImapTool list url folder - print messages in parent folder\n" +
			"\turl - imap server url\n" +
			"\tfolder - parent folder\n" +
			"ImapTool copy srcUrl srcFolder dstUrl dstFolder - copy messages\n" +
			"\t*Url - imap server url\n" +
			"\t*Folder - parent folder\n" +
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

	private static void listMessages(String url, String parent)
		throws Exception
	{
		log("listing messages, url: " + url + ", parent: " + parent);

        final Store store = getStore(url);
        store.connect();
		log("connected to imap store");

		final Folder folder = store.getFolder(parent);
		folder.open(Folder.READ_ONLY);
		log("opened folder");

		for (Message m : folder.getMessages())
			System.out.println("<" + m.getFrom()[0] + "> " + m.getSubject());

		log("closeing");
		folder.close();
		store.close();
	}

	private static void copyMessages(String srcUrl, String srcParent,
			String dstUrl, String dstParent)
		throws Exception
	{
		log("copying messages, srcUrl: " + srcUrl + ", srcParent: " + srcParent +
				", dstUrl: " + dstUrl + ", dstParent: " + dstParent);

        final Store srcStore = getStore(srcUrl);
        final Store dstStore = getStore(dstUrl);
        srcStore.connect();
        dstStore.connect();
		log("connected to both stores");

		final Folder srcFolder = srcStore.getFolder(srcParent);
		srcFolder.open(Folder.READ_ONLY);
		log("opened source folder");

		final Folder dstFolder = dstStore.getFolder(dstParent);
		log("opened destination folder");

		final Message[] messages = srcFolder.getMessages();
		for (int i = 0; i < messages.length; ++i)
		{
			final Message m = messages[i];

			log("copying " + (i+1) + " / " + messages.length + " - <" + m.getFrom()[0] +
					"> " + m.getSubject());

			final Message[] chunk = new Message[1];
			chunk[0] = m;
			dstFolder.appendMessages(chunk);
		}

		log("closeing");
		srcFolder.close();
		dstStore.close();
		srcStore.close();
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

		if (params[0].equals("list"))
		{
			if (params.length != 3)
				throw new RuntimeException("missing url and/or folder parameters");

			listMessages(params[1], params[2]);
		}

		if (params[0].equals("copy"))
		{
			if (params.length != 5)
				throw new RuntimeException("missing url and/or folder parameters");

			copyMessages(params[1], params[2], params[3], params[4]);
		}
	}
}
