package maxcut;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;
import java.util.LinkedList;
import java.io.*;
class Graph {
	int	n;
	int	m;
	Node	node[];
	Edge	edge[];

    Graph(Node node[], Edge edge[])
	{
		this.node	= node;
		this.n		= node.length;
		this.edge	= edge;
		this.m		= edge.length;
	}

    Node other(Edge a, Node u)
	{
		if (a.u == u)	
			return a.v;
		else
			return a.u;
	}
    void algR() {
        Random r = new Random(); //6942013
        for (int i = 0; i < n; i++) {
            node[i].p = r.nextBoolean();
        }
    }
    void algS() {
        int i = 0;
        int last = 0;
        do {
            int c = node[i].value();
            int s = node[i].valueIfSwapped();
            if (c < s) {
                node[i].p = !node[i].p;
                last = i;
            }
            i++;
            if (i >= n) {
                i = 0;
            } 
        }
        while (i != last);
    }
    void algRS() {
        algR();
        algS();
    }
    int eval() {
        int sum = 0;
        for (Edge e: edge) {
            sum += e.value();
        }
        return sum;
    }
}

class Node {
	Node	next;
	LinkedList<Edge>	adj;
    boolean p;
	Node(int i)
	{
		adj = new LinkedList<Edge>();
        p = false;
	}
    int value() {
        int sum = 0;
        for (Edge e: adj) {
            sum += e.value();
        }
        return sum;
    }
    int valueIfSwapped() {
        p = !p;
        int sum = value();
        p = !p;
        return sum;
    }
}

class Edge {
	Node	u,v;
    int     c;
	Edge(Node u, Node v, int c)
	{
		this.u = u;
		this.v = v;
        this.c = c;
	}   
    int value() {
        if (u.p ^ v.p) return c;
        else return 0;
    }
}

