import java.io.*;
import java.net.*;
import java.util.Random;

public class tcpmaster extends Thread{
	   private ServerSocket serverSocket;
	   private int[] upnodes;
	   private String[] paths;
	   private String[] nodes;
	   private int up;
	   

	   public void tellSlavesStart()
	   {
	   	 for(int i=0;i<up;i++)
	     {
		   	 try{
		   	 Socket client = new Socket(nodes[i].split(":")[0], Integer.parseInt(nodes[i].split(":")[1]));
	         OutputStream outToServer1 = client.getOutputStream();
	         DataOutputStream out1 =
	                       new DataOutputStream(outToServer1);

	         out1.writeUTF("Start:"+i);
	         System.out.println("Started Server "+i);
	         InputStream inFromServer1 = client.getInputStream();
	         DataInputStream in1 =
	                        new DataInputStream(inFromServer1);
	         String inp = in1.readUTF();
	         System.out.println(inp);
	         client.close();
	     	}catch(SocketTimeoutException s)
	         {
	            System.out.println("Socket timed out!");
	         
	         }catch(IOException e)
	         {
	            e.printStackTrace();
	        
	         }
		 }
		}


		public void tellSlavesStop()
	   {
	   	 for(int i=0;i<up;i++)
	     {
		   	 try{
		   	 Socket client = new Socket(nodes[i].split(":")[0], Integer.parseInt(nodes[i].split(":")[1]));
	         OutputStream outToServer1 = client.getOutputStream();
	         DataOutputStream out1 =
	                       new DataOutputStream(outToServer1);
	         System.out.println("Stopped Server "+i);
	         out1.writeUTF("Stop:"+i);
	         
	         client.close();
	     	}catch(SocketTimeoutException s)
	         {
	            System.out.println("Socket timed out!");
	         
	         }catch(IOException e)
	         {
	            e.printStackTrace();
	        
	         }
		 }
		}

	   public void getData()
	   {

	   		up=0;
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
		       	up = paths.length;
		       	//System.out.println(up);
		        br.close();
		    } 
		    catch(IOException e) {
		    	System.out.println(e);
		        
		    }
	   }

	   public tcpmaster(int port) throws IOException
	   {
	      serverSocket = new ServerSocket(port);
	      //serverSocket.setSoTimeout(65567);
	   }

	   public void run()
	   {
		   Random rand = new Random();
		   int n=0,q=0;
		   getData();
		   System.out.println("MASTER"+up);
		   String inpmsg ="";
		   Boolean cond = true;
	      while(cond)
	      {
	         try
	         {

	         	//System.out.println("MASTER1");
	            Socket server = serverSocket.accept();
	            DataInputStream in =
	                  new DataInputStream(server.getInputStream());
	            inpmsg = in.readUTF();
	            //System.out.println(inpmsg.equals("I am up"));
	            if(inpmsg.equals("I am up")==true)
	            {

		            System.out.println(inpmsg);
		            DataOutputStream out =
		                 new DataOutputStream(server.getOutputStream());
		            out.writeUTF("ok");
		            n+=1;
		            System.out.println("Servers up:"+n);
		            if(n==up)
		        	{
		        		tellSlavesStart();
		        	}
	        	}

	        	if(inpmsg.equals("I am Done")==true)
	            {
		            System.out.println(inpmsg);
		            DataOutputStream out =
		                 new DataOutputStream(server.getOutputStream());
		            out.writeUTF("ok");
		            
		            q+=1;
		            System.out.println("Servers done:"+q+" up "+up);
		            if(q>=up)
		        	{
		        		tellSlavesStop();
		        		server.close();
		        		cond=false;
		        	}
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
	      int port = Integer.parseInt("60656");
	      try
	      {
	         Thread t = new tcpmaster(port);
	         t.start();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	   }
}
