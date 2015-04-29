import java.io.*;
import java.net.*;
import java.util.Random;

public class tcpserver extends Thread{
	   private ServerSocket serverSocket;
	   private int[] upnodes;
	   private String[] paths;
	   private String[] nodes;
	   private String address;
	   private int token;

	   public void getData()
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
	   public void makePacket()
	   {
	   		int index = Integer.parseInt(address);
	   		System.out.println(index+" "+paths[index]);
	   		String msg = ""+("0'")+paths[index]+("'0");
	   		System.out.println(msg);
	   		sendForward(msg,token);
	   }
	   public void callMaster(String msg)
	   {
	   		try{
	   		 Socket client = new Socket("localhost", 60666);
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

	   public void sendForward(String inputString,int token)
	   {
	   		//System.out.println("Error:"+inputString+" "+inputString.split("'")[0]);
	   		try{
			   		String msg = ""+(Integer.parseInt(inputString.split("'")[0])+token)+"'"+inputString.split("'")[1]+"'"+(Integer.parseInt(inputString.split("'")[2])+1)+"";
			   		//System.out.println(msg+" node length "+nodes.length+" "+Integer.parseInt(inputString.split("'")[2]));
			   		if(Integer.parseInt(inputString.split("'")[2])<nodes.length)
			   		{
				   		String senderaddress = msg.split("'")[1].split(";")[Integer.parseInt(msg.split("'")[2])].split(":")[0];
				   		String senderport = msg.split("'")[1].split(";")[Integer.parseInt(msg.split("'")[2])].split(":")[1];
				   		//System.out.println(" Address "+senderaddress+" "+senderport );
				   		try{
				   		 Socket client = new Socket(senderaddress, Integer.parseInt(senderport));
				         //System.out.println("Just connected to " + client.getRemoteSocketAddress());
				         OutputStream outToServer = client.getOutputStream();
				         //System.out.println("hi");
				         DataOutputStream out = new DataOutputStream(outToServer);
				         //System.out.println("hi");
				         out.writeUTF(msg);
				         //System.out.println("hi");
				         //InputStream inFromServer = client.getInputStream();
				         //System.out.println("hi");
				         //DataInputStream in = new DataInputStream(inFromServer);
				         //System.out.println("hi");
				         //client.close();
				         //String inp = in.readUTF();
				         //System.out.println(inp);
				         client.close();
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
			         else
			         {
			         	System.out.println("Sum:"+Integer.parseInt(inputString.split("'")[0]));
			         	System.out.println("Path:"+inputString.split("'")[1]);
			         	callMaster("I am Done");
			         }
			    }
			    catch(NumberFormatException e)
			    {
			    	System.out.println("Bye");
			    }
	     
	   }


	   public tcpserver(int port) throws IOException
	   {
	      serverSocket = new ServerSocket(port);
	      //serverSocket.setSoTimeout(65567);
	      getData();
	   }

	   public void run()
	   {
		   Random rand = new Random();
		   int counter = 0;
		   token = rand.nextInt((100 - 1) + 1) + 1;
		   System.out.println(token);
		   //String localaddr = ""+serverSocket.getLocalPort()+":"+serverSocket.getInetAddress()+"";
	   	   //System.out.println("In make packet "+localaddr);
		   callMaster("I am up");
	      while(true)
	      {
	         try
	         {
	            System.out.println("Waiting for client on port " +
	            serverSocket.getLocalPort() + "...");
	            Socket server = serverSocket.accept();
	            System.out.println("Just connected to " + server.getRemoteSocketAddress());
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            String inpmsg = in.readUTF();
	            //String port1 = ""+serverSocket.getLocalSocketAddress()+" "+serverSocket.getLocalSocketAddress()+"";
	            DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            //System.out.println(counter);
	            out.writeUTF(""+token+"");
	            //System.out.println(port1+" "+inpmsg.split(":")[0].equals("Start"));
	            if(inpmsg.split(":")[0].equals("Start"))
	            {
	            	counter=1;
	            	address=inpmsg.split(":")[1];
	            	//System.out.println("In Start");
	            	makePacket();
	            	//sendForward();
	            	//counter +=1;

	            }
	            //System.out.println(inpmsg);
	            if(inpmsg.split(":")[0].equals("Stop"))
	            {
	            	//DataOutputStream out = new DataOutputStream(server.getOutputStream());
	            	//System.out.println(counter);
	            	out.writeUTF(""+token+"");
	            	//break;
	            	server.close();
	            	break;
	        	}
	            if(counter==1)
	            {
	            	if(inpmsg.split(":")[0].equals("Start"))
	            	{}
	            	else{
	            	sendForward(inpmsg,token);}
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
	      try
	      {
	         Thread t = new tcpserver(port);
	         t.start();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	   }
}
