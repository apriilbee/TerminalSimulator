package GeneralTree;

import java.io.Serializable;
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
public class Node implements Serializable{
    Node parent;
    public ArrayList<Node> children = new ArrayList();
    public FileDescriptor item;    
       
    public Node(FileDescriptor item) {
        this.item = item; 
    } 
}
