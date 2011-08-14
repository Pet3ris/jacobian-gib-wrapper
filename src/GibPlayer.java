import java.io.*;

public class GibPlayer
{
	private Process gib = null;
	private BufferedReader in = null;
	private PrintWriter out = null;

	private String eat() throws IOException, InterruptedException { return eatChar(':'); }
	private String eatChar(char waitChar) throws IOException, InterruptedException
	{
		StringBuilder s = new StringBuilder();
		char c;

		boolean readyToLeave = false;
		while (!readyToLeave)
		{
			if (in.ready())
			{
				c = (char)in.read();
				s.append(c);

				if (c == waitChar)
					readyToLeave = true;

				final int id = s.toString().indexOf("I play");
				if (id != -1 && id + 9 < s.toString().length())
				{
					break;
				}

			}
			else
				Thread.sleep(200);
		}
		while (in.ready())
		{
			final int id = s.toString().indexOf("I play");
			if (id != -1 && id + 9 < s.toString().length())
				break;

			c = (char)in.read();
			s.append(c);
		}
		
		return s.toString();
	}

	private void write(String command) throws IOException
	{
		out.println(command + "\n");
		out.flush();
	}

	public GibPlayer()
	{
		try
		{
			final ProcessBuilder builder = new ProcessBuilder();
			builder.command().add("bash");
			builder.command().add("-c");
			builder.command().add("stdbuf -o0 wine gib.exe");
			builder.redirectErrorStream(true);
			this.gib = builder.start();

			in = new BufferedReader(new InputStreamReader(this.gib.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(this.gib.getOutputStream()));

			eat();
			write("T -1");

			BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

			for (;;)
			{
				String input = stdin.readLine();
				String output = communicate(input);
				if (output != "")
					System.out.print(output);
				else
					System.out.println();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Bridge.Seat seat;
	private Bridge.Hand myHand;
	private Bridge.Contract contract;
	private Bridge.Seat declarer;

	private String lastMessage = "";

	public String communicate(String message) 
	{
		try
		{
			final String[] tokens = message.split("\\s+");
			if (tokens.length == 0)
			{
				lastMessage = "";
				return "\n";
			}

			if (tokens[0].equals("jacobian"))
			{
				lastMessage = tokens[0];
				return "id GiB 1997\n";
			}
			else if (tokens[0].equals("seat"))
				this.seat = Bridge.Seat.valueOf(tokens[1].toUpperCase());
			else if (tokens[0].equals("hand"))
				this.myHand = Bridge.Hand.fromText(tokens[1]);
			else if (tokens[0].equals("contract"))
			{
				this.contract = Bridge.Contract.fromText(tokens[1]);
				this.declarer = this.contract.getDeclarer();
			}
			else if (tokens[0].equals("dummy"))
			{
				if (!lastMessage.equals("go"))
					eat();
				write(tokens[1]);
			}
			else if (tokens[0].equals("go"))
			{
				final String output = eatChar('I');
				final int idx = output.indexOf("I play");
				final String choice = output.substring(idx + 7, idx + 9);
				lastMessage = tokens[0];
				return "play " + choice + "\n";
			}
			else if (tokens[0].equals("show"))
			{
				if (!lastMessage.equals("go"))
					eat();
				write(tokens[2]);
			}

			if (seat != null && myHand != null && contract != null)
			{
				eat();
				write("" + this.seat.getSymbol());
				eat();
				write("" + this.myHand);
				eat();
				write("" + this.declarer.getSymbol());
				eat();
				// FIXME
				write("B");
				eat();
				write(this.contract.toString().substring(0, this.contract.toString().length() - 1));
				for (int i = 0; i < 3; ++i)
				{
					eat();
					write("P");
				}

				// FIXME: hack
				seat = null;
			}
			lastMessage = tokens[0];
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return "";
	}

	private boolean closed = false;
	public void close()
	{
		this.gib.destroy();
		closed = true;
	}
	public void finalize() throws Exception
	{
		if (!closed)
			close();
	}

	public static void main(String[] args)
	{
		new GibPlayer();
	}
}


