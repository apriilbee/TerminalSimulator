package GeneralTree;

import java.util.ArrayList;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author testuser
 */
public class Node {
    Node parent;
    public ArrayList<Node> children = new ArrayList();
    public FileDescriptor item = new FileDescriptor();    
    
    public Node(){
        parent = null;
    }
    
    public Node(FileDescriptor item) {
        this.item = item; 
    }
    
    public Node(FileDescriptor item, Node parent){
        this.item = item;
        this.parent = parent;
    }

    
}
