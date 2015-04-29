import java.io.*;
import java.net.*;
import java.util.Random;

public class tcpserver extends Thread{
	   private static ServerSocket serverSocket;
	   private static int[] upnodes;
	   private static String[] paths;
	   private static String[] nodes;
	   private static String address;
	   private int token;

	   public static void getData()
	   {
	   		try {
		    	BufferedReader br = new BufferedReader(new FileReader("config.txt"));
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        String everything = sb.toString();
		       	paths = everything.split("\n");
		       	nodes = new String[paths.length];
		       	for(int i=0;i<paths.length;i++)
		       	{
		       		nodes[i]=paths[i].split(";")[0];
		       	}
		       	upnodes = new int[paths.length];
		       	for(int i=0;i<paths.length;i++)
		       	{
		       		upnodes[i]=0;
		       		//System.out.println("Node "+i+" = "+nodes[i]+" path = "+paths[i]);
		       	}
		        br.close();
		    } 
		    catch(IOException e) {
		    	System.out.println(e);
		        
		    }
	   }
	   public static String makePacket()
	   {
	   		int index = Integer.parseInt(address);
	   		System.out.println(index+" "+paths[index]);
	   		int len = paths[index].split(";").length;
	   		System.out.println("length...."+len);
	   		String msg = ""+("0'")+paths[index]+("'"+len+"");
	   		System.out.println(msg);
	   		return msg;
	   }

	   public static void callMaster(String msg)
	   {
	   		try{
	   		 Socket client = new Socket("dc01.utdallas.edu", 60656);
	         System.out.println("Just connected to "+ client.getRemoteSocketAddress());
	         OutputStream outToServer = client.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);

	         out.writeUTF(msg);
	         InputStream inFromServer = client.getInputStream();
	         DataInputStream in = new DataInputStream(inFromServer);
	         String inp = in.readUTF();
	         System.out.println(inp);
	         }
	         catch(SocketTimeoutException s)
	         {
	            System.out.println("Socket timed out!");
	         }
	         catch(IOException e)
	         {
	            e.printStackTrace();
	         }
	   }


	   public tcpserver(int port,String index) throws IOException
	   {
	      serverSocket = new ServerSocket(port);
	      address = index;
	      getData();
  	   }

	   public void run()
	   {
		   Random rand = new Random();
		   int counter = 0;
		   String inpmsg = "";
		   token = rand.nextInt((100 - 1) + 1) + 1;
		   System.out.println(token);
		   Boolean cond = true;
	      while(cond)
	      {
	         try
	         {
	            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
	            Socket server = serverSocket.accept();
	            System.out.println("Just connected to " + server.getRemoteSocketAddress());
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            inpmsg = in.readUTF();
	            System.out.println(inpmsg);
	            //String port1 = ""+serverSocket.getLocalSocketAddress()+" "+serverSocket.getLocalSocketAddress()+"";
	            DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            //System.out.println(counter);
	            //out.writeUTF(""+token+"");
	            //System.out.println(inpmsg);
	            if(inpmsg.split(":")[0].equals("Get"))
	            {
	            	//DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            	//System.out.println(counter);
	            	out.writeUTF(makePacket());
	            	System.out.println("pack"+makePacket());
	            	//break;
	            	//server.close();
	            	//break;
	        	}
	        	if(inpmsg.split(":")[0].equals("Token"))
	        	{
	        		//System.out.println("In");
	        		out.writeUTF(""+token+"");
	        		//System.out.println("Out");	
	        	}
	        	if(inpmsg.split(":")[0].equals("Close"))
	        	{
	        		out.writeUTF(""+token+"");
	        		callMaster("I am Done");
	        		
	        	}
	        	if(inpmsg.split(":")[0].equals("Stop"))
	        	{
	        		cond=false;
	        		server.close();
	        		
	        	}
	            
	            
	         }catch(SocketTimeoutException s)
	         {
	            System.out.println("Socket timed out!");
	            break;
	         }catch(IOException e)
	         {
	            e.printStackTrace();
	            break;
	         }
	      }
	   }


	   public static void main(String [] args)
	   {
	      int port = Integer.parseInt(args[0]);
	      String index = args[1];
	      try
	      {
	         Thread t = new tcpserver(port,index);
	         t.start();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	   }
}
