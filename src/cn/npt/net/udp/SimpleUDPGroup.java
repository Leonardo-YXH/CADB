package cn.npt.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;

/**
 * udp组播client
 * @author Leonardo
 *
 */
public class SimpleUDPGroup {

	private String groupAddr;
	
	private int localPort;
	
	private MulticastSocket multicastSocket;
	
	
	private static int length=4096;
	
	private byte[] buf;
	
	private Logger log=Logger.getLogger(SimpleUDPGroup.class);
	/**
	 * 
	 * @param groupAddr 组播组
	 * @param localPort 本地端口
	 */
	public SimpleUDPGroup(String groupAddr,int localPort) {
		this.groupAddr=groupAddr;
		this.localPort=localPort;
		try {
			init();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error("初始化组播client失败,可能是本地端口"+localPort+"被占用");
		}
	}
	private void init() throws IOException{
		this.multicastSocket=new MulticastSocket(localPort);
		this.multicastSocket.setSoTimeout(5000);
		InetAddress groupAddress = InetAddress.getByName(groupAddr);
        multicastSocket.joinGroup(groupAddress);
	}
	/**
	 * 接收组播信息,阻塞模式
	 * @return
	 */
	public String receive(){
		this.buf=new byte[length];
		DatagramPacket p=new DatagramPacket(buf, length);
		try {
			this.multicastSocket.receive(p);
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
			log.error("接收组播信息失败,请检查网络是否畅通");
			this.close();
		}
		String rs=new String(p.getData(),0,p.getLength());
		return rs;
	}
	
	public void close(){
		this.multicastSocket.close();
	}
	
	public static void main(String[] args) {
		SimpleUDPGroup client=new SimpleUDPGroup("224.0.1.2", 10945);
		client.receive();
	}
}
