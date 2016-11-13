/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author April Dae Bation
 */
public class Tree {
    Node current = new Node();
    Node root = new Node();
    
    public Tree(){
        root = new Node(new FileDescriptor("root",true));
        current = root;
    }
    
    public static Date getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        return date;
    }
    
    public boolean insertNode(Node parent, Node child){
        if(parent.item.isDirectory){
            if(!checkFileExists(parent,child)){
                child.parent = parent;
                parent.children.add(child);
                return true;
            }
            else{
                System.out.println(child.item.name + " already exists in " + parent.item.name);
                return false;
            }
        }
        else{
            System.out.println("Can't insert to a file.");
            return false;
        }
    }
    
    public boolean deleteNode(Node parent, Node child){
        if(parent.item.isDirectory){
            if(parent.children.contains(child)){
                parent.children.remove(child);
                return true;
            }
            else{
                System.out.println(child.item.name + " not in " + parent.item.name);
                return false;
            }
        }
        else{
           return false;
        }
    }
    
    // use this function when locating for a node, given a string
    // Node n is current/ root node
    public Node locateNode(Node n, String title) throws ClassNotFoundException{
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.name.equals(title)){
                return tmp;
            }
        }
        return null;
    }
    
    // recursively looks for a file in all directory  
    public Node searchAll(Node n, String title) throws ClassNotFoundException{
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.name.equals(title)){
                System.out.println(title + " is in " + n.item.name);
                return tmp;
            }
            else{
                if(tmp.item.isDirectory)
                   searchAll(tmp, title);
            }
        }
        return null;
    }
    
    //recursive display; opens all subdirectories
    public void displayAll(Node n, String indent){
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.isDirectory){
                System.out.println(indent + tmp.item.name + "fff");
                displayAll(tmp,indent+"->  ");
            }
            else{
                System.out.println(indent + tmp.item.name);
            }
        }
    }
    
    //displays all files/directory in specific folder; not recursive
    public void display(Node n, String indent){
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            System.out.println(indent + tmp.item.name);
        }
    }

    //returns true if file of the same name exists in directory
    private boolean checkFileExists(Node p, Node t) {
        String name = t.item.name;
        for(int i=0; i<p.children.size(); i++){
            if(p.children.get(i).item.name.equals(name))
                return true;
        }
        return false;
    }
}
