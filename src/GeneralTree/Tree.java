/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;

/**
 *
 * @author April Dae Bation
 */
public class Tree {

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
    public boolean checkNodeExists(Node n, Node t) {
        String name = t.item.name;
        for(int i=0; i<n.children.size(); i++){
            if(n.children.get(i).item.name.equals(name))
                return true;
        }
        return false;
    }
}
