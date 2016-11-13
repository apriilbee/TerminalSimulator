/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tree;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author testuser
 */
public class Tree {
    Node current;
    Node root;
    
    Tree(){
        root = new Node(new FileDescriptor("root",true));
       // current = root;
    }
    
    public static void main(String[] args) throws ClassNotFoundException {
        Tree t = new Tree();
        Node parent = new Node(new FileDescriptor("Parent", getDate(), true));
        Node parent2 = new Node(new FileDescriptor("Parent2", getDate(), true));
        Node a = new Node(new FileDescriptor("parent2child", getDate(), false));
        Node child = new Node(new FileDescriptor("Child", getDate(), false));
        Node c = new Node(new FileDescriptor("Child2", getDate(), false));
        
        t.insertNode(t.root, parent);
        t.insertNode(t.root, parent2);
        t.insertNode(parent, child);
        t.insertNode(parent, c);
        t.insertNode(parent2, a);
       
        
//        if (t.search(t.root,"Child") == null)
//            System.out.println("file not exist");
       
        t.Display(t.root,"");
        System.out.println("\n\n");
       
        t.deleteNode(parent, c);
        t.Display(t.root,"");
        System.out.println("\n\n");
       
    }
    
    public static Date getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        return date;
    }
    
    public void insertNode(Node parent, Node child){
        if(parent.item.isDirectory){
            child.parent = parent;
            parent.children.add(child);
        }
        else{
            System.out.println("Can't insert to a file.");
        }
    }
    
    public void deleteNode(Node parent, Node child){
        if(parent.item.isDirectory){
            if(parent.children.contains(child)){
                parent.children.remove(child);
            }
            else{
                System.out.println(child.item.name + " not in " + parent.item.name);
            }
        }
    }
         
    public Node search(Node n, String title) throws ClassNotFoundException{
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.name.equals(title)){
                System.out.println(title + " is in " + n.item.name);
                return tmp;
            }
// recursively looks for a file in all directory           
//            else{
//                if(tmp.item.isDirectory)
//                   search(tmp, title);
//            }
        }
        return null;
    }
    
    
    public void Display(Node n, String indent){
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.isDirectory){
                System.out.println(indent + tmp.item.name);
                Display(tmp,indent+"->  ");
            }
            else{
                System.out.println(indent + tmp.item.name);
            }
        }
    }
}
