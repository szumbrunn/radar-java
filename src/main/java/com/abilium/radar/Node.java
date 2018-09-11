package com.abilium.radar;

public class Node implements Comparable<Node> {
	
	private Integer key;
	private Double val;
	
	public Node(Integer key, Double val) {
		this.key = key;
		this.val = val;
	}
	
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public Double getVal() {
		return val;
	}
	public void setVal(Double val) {
		this.val = val;
	}
	
	/**
	 * Sort descending for highest score --> highest anomaly
	 */
	public int compareTo(Node node) {
		if(node.val > this.val) {
			return 1;
		} else if(node.val < this.val) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return "Node " + key + ": " + val;
	}

}
