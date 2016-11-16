/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author testuser
 */
public class FileSystem {
    static Map<String, Runnable> commands = new HashMap();
    static Tree tree = new Tree();
    static String input; 
    
    
    public static void main(String[] args) {
        initCommands();
        Scanner sc = new Scanner(System.in);
        
        do{
            getAbsolutePath(tree.current);
            System.out.print(": ");
            input = sc.nextLine();
            try{
                commands.get(getCommand(input)).run();
            } catch (Exception e){
                System.out.println("Command not found.");
            }
        } while(true);
    }
    
    private static void mkdir() throws ClassNotFoundException { 
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else if(!args[1].contains("/")){
            for(int i=1; i<args.length; i++){
                Node n = new Node(new FileDescriptor(args[i], tree.getDate(), true));
                if(!(tree.insertNode(tree.current,n)))
                    System.out.println(n.item.name + " already exists in " + tree.current.item.name);
            }
        }
        else{
              checkPathExists(args[1]);
//            Node dest = getDestination(args[1]);
//            if(dest == null)
//                System.out.println("Folder does not exist");
//            else {
//                String name = getRelativePath(args[1]);
//                Node n = new Node(new FileDescriptor(name, tree.getDate(), true));
//                if(!(tree.insertNode(dest,n)))
//                    System.out.println(n.item.name + " already exists in " + dest.item.name);
//            }
        }
    }

    private static void remove() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else if(!args[1].contains("/")){
            if(!(tree.deleteNode(tree.current,tree.searchNode(tree.current, args[1]))))
                System.out.println(args[1] + " does not exist.");
        }
        else{
            
        }
    }

    private static void cd() throws ClassNotFoundException {
        String[] args = input.split(" ");
        String go;
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else if(!args[1].contains("/")){
            go = args[1]; 
              Node n = tree.searchNode(tree.current, go);
              
            if(go.equals("root"))
                tree.current = tree.root;
            else if(n==null)
                System.out.println("Directory does not exist");
            else if (!n.item.isDirectory)
                System.out.println(args[1] + " is not a directory");
            else{
                tree.current = n;
            }
        }
        else{
            go = "change pa ni";
           // go = getRelativePath(args[1]);
        }

      
    }

    private static void edit() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else{
            if(tree.searchNode(tree.current, args[1])!=null){
                //open editor here
            }
            else{
                Node n = new Node(new FileDescriptor(args[1], tree.getDate(), false));
                if(!(tree.insertNode(tree.current,n)))
                   System.out.println(n.item.name + " already exists in " + tree.current.item.name);
                //open editor here
            }
            
        }

    }

    private static void goback() {
        if(tree.current.parent!=null)
            tree.current = tree.current.parent;
        else {
            System.out.println("Already in root node.");
        }
    }

    private static void rn() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 3)
            System.out.println("Missing arguments.");
        else{
            if(tree.searchNode(tree.current, args[1])!=null){
                if(tree.searchNode(tree.current, args[2])!=null){
                   System.out.println(args[2] + " already exists.");
                }
                else{
                   tree.searchNode(tree.current, args[1]).item.name = args[2];
                }
            }
            else{
                System.out.println(args[1] + " does not exist.");
            }
        }
        
    }

    private static void mv() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void cp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void ls() {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        
        else if(args[1].equals("-")){
            for(int i=0, ctr=0; i<tree.current.children.size(); i++){
                Node tmp = tree.current.children.get(i);
                System.out.print(tmp.item.name + "\t");
            }
            System.out.println("");
        }
        
        else if (args[1].contains("*")){
            ArrayList<Node> match = new ArrayList<>();
            
            String search = args[1].replaceAll("\\*", "\\\\w*");
            for(int i=0; i<tree.current.children.size(); i++){
                Node tmp = tree.current.children.get(i);
                if(tmp.item.name.matches(search)){
                    match.add(tmp);
                }
            }
            
            for(int i=0; i<match.size(); i++){
                System.out.print(match.get(i).item.name + " ");
            }
            System.out.println("");
        }
        else if (args[1].contains("/")){
            
        }
    }

    private static void show() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void whereis() throws ClassNotFoundException {
        String[] args = input.split(" ");
        searchAll(tree.root, args[1]);
        System.out.println("");
    }
    
    public static void searchAll(Node n, String search) throws ClassNotFoundException{
        for(int i=0; i<n.children.size(); i++){
            Node tmp = n.children.get(i);
            if(tmp.item.name.equals(search)){
                getAbsolutePath(n);
                System.out.print("/" + search + "\t");
            }
            if(tmp.item.isDirectory)
               searchAll(tmp, search);
        }
    }

    private static void initCommands(){
        commands.put("mkdir", () -> {
            try {
                mkdir();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("rmdir", () ->  {
            try {
                remove();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("cd", () -> {
            try {
                cd();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("cd..", () -> goback());
        commands.put("edit", () -> {
            try {
                edit();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("rm", () -> {
            try {
                remove();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("rn", () -> {
            try {
                rn();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("mv", () -> mv());
        commands.put("cp", () -> cp());
        commands.put("ls", () -> ls());
        commands.put("show", () -> show());
        commands.put("whereis", () ->  {
            try {
                whereis();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
     
        
    public static String getCommand(String input){
        return input.split(" ")[0];
    }
    
    private static void getAbsolutePath(Node cur){
        Node p = cur;
        if(p.parent!=null){
           getAbsolutePath(p.parent); 
           System.out.print("/" + p.item.name);
        }
        else{
           System.out.print("~");
        }
    }

    private static void checkPathExists(String arg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
