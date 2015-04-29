/**
 * 
 */
import java.net.*;
import java.io.*;


public class tcpclient {
	public static String getIp(String msg,int i)
	{
		//System.out.println(msg+" "+i);
		//System.out.println(msg.split("'")[1].split(";")[i].split(":")[0]);
		return msg.split("'")[1].split(";")[i].split(":")[0];
	}

	public static String getPort(String msg,int i)
	{
		//System.out.println(msg+" "+i);
		//System.out.println(msg.split("'")[1].split(";")[i].split(":")[1]);
		return msg.split("'")[1].split(";")[i].split(":")[1];
	}

	public static String getToken(String msg)
	{
		return msg.split("'")[0];
	}

	public static int setN(String msg)
	{
		return Integer.parseInt(msg.split("'")[2]);
	}

	public static void main(String [] args)
	{
	      int port = Integer.parseInt(args[0]);
	      int i = -1,sum=0,n=7;
	      String serverName = "localhost";//"serverName1[i]";
	      String pathString = "";
	      Boolean condition = false;
	      try
	      {
	      for(int t=0;t<n+2;t++){
	      
	         System.out.println("Connecting to " + serverName+ " on port " + port);
	         Socket client = new Socket(serverName, port);
	         System.out.println("Just connected to "+ client.getRemoteSocketAddress());
	         OutputStream outToServer = client.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);
	         

	         if(i==-1)
	         {
	         	out.writeUTF("Get:");
	         	InputStream inFromServer = client.getInputStream();
		        DataInputStream in = new DataInputStream(inFromServer);
		        pathString = in.readUTF();
		        //System.out.println(pathString);
	         	serverName = getIp(pathString,i+1);
	         	port = Integer.parseInt(getPort(pathString,i+1));
	         	n = setN(pathString);
	         	i+=2;
	         	//System.out.println(i);
	         }
	         else if(i<=n)
	         {
	         	//System.out.println()
	         	if(i<n)
	         	{
	         		//System.out.println("In token");
	         		out.writeUTF("Token:");
	         		//System.out.println("Out token");
	         		InputStream inFromServer = client.getInputStream();
			        DataInputStream in = new DataInputStream(inFromServer);
			        String inp = in.readUTF();
			        
			        sum+=Integer.parseInt(getToken(inp));
			        System.out.println(inp + " Sum: " + sum);
			        serverName = getIp(pathString,i);
	         		port = Integer.parseInt(getPort(pathString,i));
	         		//System.out.println(serverName+port+i);
	         		i=i+1;
	         		if(i==n)
	         		{
	         			condition = true;
	         		}
	         	}
	         	else if(condition == true)
	         	{
	         		out.writeUTF("Close:");
	         		System.out.println(serverName+port+i);
	         		InputStream inFromServer = client.getInputStream();
			        DataInputStream in = new DataInputStream(inFromServer);
			        String inp = in.readUTF();
			        sum+=Integer.parseInt(inp);
	         		System.out.println(sum);
	         		condition = false;
	         	}
	         	else
	         	{
	         		out.writeUTF("ok:");
	         	}
	         	

	         	
	         	
	         }
	         client.close();
	     }
	         
	         //System.out.println(inp);
			 //sum += Integer.parseInt(inp);
	         
	         
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	  
	   }
}
