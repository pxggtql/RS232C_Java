package app;
import java.util.Enumeration;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream.GetField;
import java.lang.ref.WeakReference;
import java.io.OutputStream;
import java.util.ArrayList;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;    
public class App {

	 public void listPort() {  
	        Enumeration ports = CommPortIdentifier.getPortIdentifiers();  
	          
	        //列出所有本机串口
	        System.out.println("本机串口为： \n"); 
	        while(ports.hasMoreElements())  
	            System.out.println(((CommPortIdentifier)ports.nextElement()).getName());  
	          
	 } 
	 public ArrayList<String>findPort()
	 {
		  //确定本机端口并返回对应的namelist
		 Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
	     ArrayList<String> portNameList = new ArrayList<String>();
	     while(portList.hasMoreElements())
	     {
	    	 String portName = portList.nextElement().getName();
	    	 portNameList.add(portName);
	     }
	     return portNameList;
	 }
	 
	 public static final SerialPort openPort(String portName ,int baudrate)throws PortInUseException  {
		
		 try{
		 CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
         CommPort commPort = portIdentifier.open(portName, 2000);
         if(commPort instanceof SerialPort)
        	 {
        	 	SerialPort serialPort = (SerialPort)commPort;
        	 	try {
        	 		serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
        	 		
        	 	}
        	 	catch(UnsupportedCommOperationException unsup_e) {
                    unsup_e.printStackTrace();
        	 }
        	 return serialPort;	
        	 }
		 }
         catch (NoSuchPortException no_e) {
        	 no_e.printStackTrace();
		}
         return null;
	
	 }
	 public static void showPort(SerialPort serialPort)
	 {
		 System.out.println("串口："+serialPort.getName()+"  \n");
		 System.out.println("波特率 "+serialPort.getBaudRate()+"  \n");
		 System.out.println("数据位 "+serialPort.getDataBits()+"   \n");
		 System.out.println("校验位 "+serialPort.getParity()+"   \n");
		 System.out.println("停止位 "+serialPort.getStopBits()+"   \n");
	 }
	  public static void closePort(SerialPort serialPort)
	  {
		  if(serialPort!=null)
		  {
			  serialPort.close();
		  }
	  }
	  //接受输入字符串
	  public static String inputMessage()
	  {
		  System.out.print("请输入：");
		  Scanner scan = new Scanner(System.in);
		  String read = scan.nextLine();
		  System.out.println("输入数据为："+read);
		  return read;
	  }
	  
	  //string 转换为字节 得到的传给
	  public static byte[] String2Bytes(String message) {  
	   		return (message+"\n").getBytes();  
	  }
	  //读取对应字节流并发送
	  public static void send(byte[]bytes,OutputStream output) {
		 try {
			System.out.println("发送: " + new String(bytes, 0, bytes.length));
			output.write(bytes);
			output.flush();
		 } catch ( IOException e) {
			// TODO: handle exception
			System.out.println("dsdsdsds");
			 e.printStackTrace();
		}
	   }
	  public void connectPortSender(String portName) throws Exception{
		  CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);  
	        
	        if (portIdentifier.isCurrentlyOwned()) {  
	            System.out.println("端口被占用！");  
	        } else {  
	            SerialPort erialPort = (SerialPort) portIdentifier.open(portName, 2000);  
	            erialPort.setSerialPortParams( 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);  
	            showPort(erialPort);
                System.out.println("这里是： "+portName+"\n");
                byte[] a = String2Bytes(inputMessage());
                send(a, erialPort.getOutputStream());
	            closePort(erialPort);
	        }  
	    }  
	  //以上为输出部分
	  //--------------------------------------------
	  //接受部分
	 
	  public void connectPorRec(String portName) throws Exception {
		  CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

			if (portIdentifier.isCurrentlyOwned()) {
				System.out.println("端口被占用！");
			} else {
				SerialPort erialPort = (SerialPort) portIdentifier.open(portName, 2000);

				// 设置串口参数
				erialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				showPort(erialPort);
				//serialPort.input = erialPort.getInputStream();
				System.out.println("这里是： "+portName+"\n");
				boolean received = false;
				while(!received){	// 若没有接收到就一直接收
					received = receive(erialPort.getInputStream());  //接收到返回true
				}
				closePort(erialPort);
				//定位函数
			}
	  }
      	public static boolean receive(InputStream input) throws IOException {
		  	byte[] buffer_rec = new byte[1024];
		  	int tail = 0; 
		  	int temp; 
		  	//考虑是否设置为一直发送
			//System.out.println("全部接受"+input.read());
			if((temp=input.read())==-1){
				return false;
			} 
			else{
				buffer_rec[tail++]=(byte)temp;
			}
		 // System.out.println("receive()正在接受信息");
			while((temp= input.read())!=-1){
				if((byte)temp=='\n'){
					if(tail!=0){
						String rec_message = new String(buffer_rec,0,tail);
						System.out.println("收到的信息： "+rec_message);
						tail = 0;
					}
				}
				else{
					buffer_rec[tail++]=(byte) temp;
				}
			}
			
			return true;
		}
	  
	    public static void main(String[] args) throws Exception {  
	        //new serialPort().list_port(); 
	        App mySerialPort = new App();
	        mySerialPort.listPort();
	        ArrayList<String>portNames=mySerialPort.findPort();
	 
//	        for(int i=0;i<portNames.size();i++)  //若有其他端口也打印
//	        {
//	        	String tempName = portNames.get(i);
//	        	SerialPort sPort = openPort(tempName, 9600);
//	        	showPort(sPort);
//	        	closePort(sPort);
//	        }	
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        mySerialPort.connectPorRec(portNames.get(1));
                    } catch (Exception e) {
						//TODO: handle exception
						
                        e.printStackTrace();
                    }
                }
			}).start();
			Thread.sleep(50);
            mySerialPort.connectPortSender(portNames.get(0));
	    }  
}
