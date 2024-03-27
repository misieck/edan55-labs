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
	int	i;
	Node	next;
	LinkedList<Edge>	adj;
    boolean p;

	boolean working;

	Node(int i)
	{
		this.i = i;
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
	Node	u;
	Node	v;
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


public class MaxCut {
    public static void main(String args[])
	{
        File f = new File("data/pw09_100.9.txt");
        int	n;
        int	m;
        int	i;
        int	u;
        int	v;
        int	c;
        Graph	g;
        try (Scanner s = new Scanner(f)){
            n = s.nextInt();
            m = s.nextInt();
            Node[] node = new Node[n];
            Edge[] edge = new Edge[m];

            for (i = 0; i < n; i += 1)
                node[i] = new Node(i);

            for (i = 0; i < m; i += 1) {
                u = s.nextInt() - 1;
                v = s.nextInt() - 1;
                c = s.nextInt(); 
                edge[i] = new Edge(node[u], node[v], c);
                node[u].adj.addLast(edge[i]);
                node[v].adj.addLast(edge[i]);
            }
            g = new Graph(node, edge);
        }
        catch (FileNotFoundException e) {
            System.out.println("Failed bad");
            return;
        }
        g.algRS();
        System.out.println(g.eval());
	}
}