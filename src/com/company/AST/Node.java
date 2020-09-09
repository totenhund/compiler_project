package com.company.AST;

import com.company.tokens.Token;

import java.util.ArrayList;

public class Node {
    protected NodeType type;
    protected Token token;
    protected Node parent;
    protected ArrayList<Node> children;
    protected int childrenAmount;

    public Node(NodeType type, Node parent)
    {
        children = new ArrayList<Node>();
        childrenAmount=0;
        this.type = type;
        this.parent = parent;
    }

    public Node(NodeType type, Node parent, Token token)
    {
        children = new ArrayList<Node>();
        childrenAmount=0;
        this.type = type;
        this.parent = parent;
        this.token=token;
    }

    public NodeType getType() {
        return type;
    }

    public Node getParent() {
        return parent;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public Node getChild(int index) {
        return children.get(index);
    }

    public int getChildrenAmount() {
        return childrenAmount;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public void setChild(Node child, int index){
        children.set(index,child);
    }

    public void addChild(Node child){
        childrenAmount++;
        children.add(child);
    }
}
