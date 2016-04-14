package cn.npt.fs.bootstrap;

import java.util.ArrayList;
import java.util.List;

import cn.npt.fs.CachePoolFactory;
import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.net.BaseReceiveServer;
import cn.npt.net.BaseSendServer;
import cn.npt.net.BaseServer;
/**
 * 
 * @author Leonardo
 * @version
 *
 */
public class NPServerBootstrap {

	private List<BaseServer> receiveServers;
	private List<Runnable> netServers;
	public NPServerBootstrap(){
		this.receiveServers=new ArrayList<BaseServer>();
		this.netServers=new ArrayList<Runnable>();
	}
	/**
	 * 创建缓存池
	 * @param propertyFileName
	 * @return
	 */
	public NPServerBootstrap buildCachePool(String propertyFileName){
		CachePoolFactory.build(propertyFileName);
		return this;
	}
	/**
	 * 创建多个缓存池
	 * @param propertyFileNames
	 * @return
	 */
	public NPServerBootstrap buildCachePool(List<String> propertyFileNames){
		for(String propertyFileName:propertyFileNames){
			CachePoolFactory.build(propertyFileName);
		}
		return this;
	}
	public BaseMemoryCache getCachePool(String propertyFileName){
		return CachePoolFactory.getCachePools().get(propertyFileName);
	}
	/**
	 * 添加采集服务
	 * @param receiveServers
	 * @return
	 * @see #addServer
	 */
	public NPServerBootstrap addReceiveServer(BaseReceiveServer ...receiveServers){
		for(BaseReceiveServer receiveServer:receiveServers){
			this.receiveServers.add(receiveServer);
			new Thread(receiveServer).start();
			//receiveServer.start();
		}
		return this;
	}
	/**
	 * 添加数据推送服务
	 * @param sendServers
	 * @return
	 * @see #addServer
	 * 
	 */
	public NPServerBootstrap addSendServer(BaseSendServer ...sendServers){
		for(BaseSendServer sendServer:sendServers){
			this.receiveServers.add(sendServer);
			new Thread(sendServer).start();
			//sendServer.start();
		}
		return this;
	}
	/**
	 * 添加服务(推荐使用)
	 * @param servers
	 * @return
	 * @see #addReceiveServer(BaseReceiveServer...)
	 * @see #addSendServer(BaseSendServer...)
	 */
	public NPServerBootstrap addServer(Runnable... servers){
		for(Runnable server:servers){
			Thread t=new Thread(server);
			t.start();
			this.netServers.add(t);
		}
		return this;
	}
	/**
	 * 
	 * @param serverId
	 */
	public void stopServerById(int serverId){
		for(BaseServer receiveServer:this.receiveServers){
			if(receiveServer.getId()==serverId){
				receiveServer.stop();
				return;
			}
		}
	}
}
