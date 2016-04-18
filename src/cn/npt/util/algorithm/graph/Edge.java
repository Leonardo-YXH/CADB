package cn.npt.util.algorithm.graph;

public class Edge<T> implements Comparable<Edge<T>>{
	/**
	 * 出发点
	 */
	public Vertex<T> source;
	/**
	 * 目标点
	 */
	public Vertex<T> target;
	
	public Edge(Vertex<T> source,Vertex<T> target) {
		this.source=source;
		this.target=target;
	}

	public Vertex<T> getSource() {
		return source;
	}

	public void setSource(Vertex<T> source) {
		this.source = source;
	}

	public Vertex<T> getTarget() {
		return target;
	}

	public void setTarget(Vertex<T> target) {
		this.target = target;
	}

	@Override
	public int compareTo(Edge<T> o) {
		if(this.source.getIdentity()>o.source.getIdentity()){
			return 1;
		}
		else if(this.source.getIdentity()<o.source.getIdentity()){
			return -1;
		}
		else{
			if(this.target.getIdentity()>o.target.getIdentity()){
				return 1;
			}
			else if(this.target.getIdentity()<o.target.getIdentity()){
				return -1;
			}
		}
		return 0;
	}
}
