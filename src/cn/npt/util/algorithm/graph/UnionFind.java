package cn.npt.util.algorithm.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 并查集，用来判断多节点是否构成一棵树，或是否有节点成环
 * <br>时间复杂度=E*log(V)
 * @author Leonardo
 * @param <T> 节点编号类型
 *
 */
public class UnionFind<T> {
	private List<Integer> ids;
	private List<Vertex<T>> vertexes;
	
    private int cnt;
    private boolean isNoCycle;
    private Edge<T> lastEdge;
    /**
     * 时间复杂度=E*log(V)
     * @param edges 所有的边
     * @param vertexes 所有的顶点
     */
    public UnionFind(List<Edge<T>> edges,List<Vertex<T>> vertexes){
        this.ids = new ArrayList<Integer>();
        this.isNoCycle=true;
        this.vertexes=vertexes;
        Collections.sort(this.vertexes);
        //初始化并查集，每个节点的hashCode对应自己的集合号
        for(Vertex<T> v:vertexes){
        	this.ids.add(v.getIdentity());
        }
       
        this.cnt = this.ids.size();
        
        for (Edge<T> e : edges) {
			if(!union(e.source, e.target)){
				this.isNoCycle=false;
				this.lastEdge=e;
				break;
			}
		}
    }
    /**
     * 时间复杂度=log(N)*2=log(N)
     * @param m
     * @param n
     * @return true表示不在同一集合中,无环
     */
    private boolean union(Vertex<T> m, Vertex<T> n){
    	//每合并一次后，集合数减一
    	cnt--;
        int src = find(m);
        int dst = find(n);
        //如果两个节点不在同一集合中，将两个集合合并为一个(dst合并到src)
        if(this.ids.get(src) != this.ids.get(dst)){
        	this.ids.set(dst, this.ids.get(src));
            return true;
        } else {
            return false;
        }
    }
    /**
     * 二分查找,时间复杂度=log(N)
     * @param m
     * @return
     */
    private int find(Vertex<T> m){
        return Collections.binarySearch(this.vertexes, m);
    }
    /**
     * 判断两个顶点是否连通
     * @param m
     * @param n
     * @return
     */
    public boolean isConnected(Vertex<T> m, Vertex<T> n){
        return find(m) == find(n);
    }
    /**
     * 判断是否符合tree的条件（所有顶点连通和无环）
     * @return
     */
    public boolean isTree(){
    	return 1==cnt;
    }
    /**
     * 判断是否无环
     * @return
     */
    public boolean isNoCycle(){
    	return this.isNoCycle;
    }
	public List<Integer> getIds() {
		return ids;
	}
	/**
	 * 如果有环存在的话，则该方法返回的是最后检查到环的边
	 * @return
	 */
	public Edge<T> getLastEdge() {
		return lastEdge;
	}
    
}
