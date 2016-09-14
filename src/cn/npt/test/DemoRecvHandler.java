package cn.npt.test;

import java.util.List;

import cn.npt.fs.cache.BaseMemoryCache;
import cn.npt.net.handler.BaseTCPClientHandler;
import cn.npt.net.handler.BaseYHandler;

public class DemoRecvHandler extends BaseTCPClientHandler {

	public DemoRecvHandler(String cachePropertyName, List<Long> sensorIds) {
		super(cachePropertyName, sensorIds);
	}

	public DemoRecvHandler(BaseMemoryCache cachePool, List<Long> sensorIds) {
		super(cachePool, sensorIds);
	}

	@Override
	public <T> List<T> filter(List<T> src) {
		// TODO Auto-generated method stub
		return src;
	}

	@Override
	public boolean convert(Object msg) {
		
		return false;
	}

	@Override
	public BaseYHandler deepClone() {
		return new DemoRecvHandler(this.cachePool,this.sensorIds);
	}

}
