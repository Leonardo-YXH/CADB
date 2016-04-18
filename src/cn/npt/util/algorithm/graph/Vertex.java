package cn.npt.util.algorithm.graph;
/**
 * 顶点
 * @author Leonardo
 *
 * @param <T>
 */
public class Vertex<T> implements Comparable<Vertex<T>>{
	/**
	 * 顶点编号
	 */
	public T id;
	/**
	 * 顶点信息
	 */
	public Object info;
	
	public Vertex(T id) {
		this.id=id;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}
	/**
	 * 顶点编号对应的hashCode
	 * @return
	 */
	public int getIdentity(){
		return this.id.hashCode();
	}

	@Override
	public int compareTo(Vertex<T> o) {
		if(this.getIdentity()>o.getIdentity()){
			return 1;
		}
		else if(this.getIdentity()<o.getIdentity()){
			return -1;
		}
		return 0;
	}
}
