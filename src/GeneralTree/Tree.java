/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author April Dae Bation
 */
public class Tree {
    Node current;
    Node root;
    
    public Tree(){
        root = new Node(new FileDescriptor("root", getDate(), true));
        current = root;
    }
   
    public boolean insertNode(Node parent, Node child){
        if(!checkNodeExists(parent,child)){
            child.parent = parent;
            parent.children.add(child);
            return true;
        }
        else{
            return false;
        }
       
    }
    
    public boolean deleteNode(Node parent, Node child){
        if(parent.children.contains(child)){
            parent.children.remove(child);
            return true;
        }
        else {
            return false;
        }
    }
    
    // use this function when locating for a node, given a string
    // Node n is current/ root node
    public Node searchNode(Node n, String title) throws ClassNotFoundException{
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.name.equals(title)){
                return tmp;
            }
        }
        return null;
    }
    
     //returns true if file of the same name exists in directory
    private boolean checkNodeExists(Node n, Node t) {
        String name = t.item.name;
        for(int i=0; i<n.children.size(); i++){
            if(n.children.get(i).item.name.equals(name))
                return true;
        }
        return false;
    }
    
    public static Date getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        return date;
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
}
