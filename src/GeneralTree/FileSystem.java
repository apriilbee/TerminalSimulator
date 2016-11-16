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
    static Node root = new Node(new FileDescriptor("root", getDate(), true));
    static Node current = root; 
    static Map<String, Runnable> commands = new HashMap();
    static Tree tree = new Tree();
    static String input; 
    
    
    public static void main(String[] args) {
        initCommands();
        Scanner sc = new Scanner(System.in);
        
        do{
            System.out.print("test@user:");
            getAbsolutePath(current);
            System.out.print("$ ");
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
                Node n = new Node(new FileDescriptor(args[i], getDate(), true));
                if(!(tree.insertNode(current,n)))
                    System.out.println(n.item.name + " already exists in " + current.item.name);
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
        else {
            if(!args[1].contains("/") && !(args[1].contains("*"))){
                if(!(tree.deleteNode(current,tree.searchNode(current, args[1]))))
                    System.out.println(args[1] + " does not exist.");
            }
            else if(args[1].contains("/")){
                
            }
            else if(args[1].contains("*")){
                ArrayList<Node> list = findAll(current, args[1]);
                for(int i=0; i<list.size(); i++){
                    tree.deleteNode(current,list.get(i));
                }
            }
        }
        
            
        
    }

    private static void cd() throws ClassNotFoundException {
        String[] args = input.split(" ");
        String go;
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else if(!args[1].contains("/")){
            go = args[1]; 
              Node n = tree.searchNode(current, go);
              
            if(go.equals("root"))
                current = root;
            else if(n==null)
                System.out.println("Directory does not exist");
            else if (!n.item.isDirectory)
                System.out.println(args[1] + " is not a directory");
            else{
                current = n;
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
            if(tree.searchNode(current, args[1])!=null){
                Node n = tree.searchNode(current, args[1]);
                System.out.println(n.item.content);
                n.item.content += "\n";
                openEditor(n);
                n.item.last_modified = getDate();
            }
            else{
                Node n = new Node(new FileDescriptor(args[1], getDate(), false));
                if(!(tree.insertNode(current,n)))
                   System.out.println(n.item.name + " already exists in " + current.item.name);
                else{
                    Scanner sc = new Scanner(System.in);
                    String tmp;
                    openEditor(n);
                    n.item.last_modified = getDate();
                }
            }
        }
    }

    private static void goback() {
        if(current.parent!=null)
            current = current.parent;
        else {
            System.out.println("Already in root node.");
        }
    }

    private static void rn() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 3)
            System.out.println("Missing arguments.");
        else{
            if(tree.searchNode(current, args[1])!=null){
                if(tree.searchNode(current, args[2])!=null){
                   System.out.println(args[2] + " already exists.");
                }
                else{
                   tree.searchNode(current, args[1]).item.name = args[2];
                }
            }
            else{
                System.out.println(args[1] + " does not exist.");
            }
        }
        
    }

    private static void mv() {
    
    }

    //fix this
    private static void cp() throws ClassNotFoundException {
         String[] args = input.split(" ");
        if(args.length < 3)
            System.out.println("Missing arguments.");
        else {
            Node tobecopied = tree.searchNode(current, args[1]);
            Node new_node = tree.searchNode(current, args[2]);
            if(tobecopied!=null && new_node==null){
                new_node = new Node(new FileDescriptor(args[2],getDate(),tobecopied.item.isDirectory));
                new_node.item.content = tobecopied.item.content;
                //fix this. if ichange ang new copy, machange pd ang old copy
                new_node.children = (ArrayList<Node>) tobecopied.children.clone();
                tree.insertNode(current, new_node);
            }
            else if(tobecopied==null){
                System.out.println(args[1] + " does not exist.");
            }
            else if (new_node!=null){
                System.out.println(args[2] + " already exists.");
            }
        }
    }
    
    
    private static void ls() {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        
        else if(args[1].equals("-")){
            for(int i=0, ctr=0; i<current.children.size(); i++){
                Node tmp = current.children.get(i);
                System.out.print("Name: " + tmp.item.name + "\n" + "Date Created: " + tmp.item.created.toGMTString().replace("GMT", "") + "\n" + "Last Modified: " + tmp.item.last_modified.toGMTString().replace("GMT", ""));
                System.out.println("\n");
            }
        }
        
        else if (args[1].contains("*")){
            ArrayList<Node> match = new ArrayList<>();
            
            String search = args[1].replaceAll("\\*", "\\\\w*");
            for(int i=0; i<current.children.size(); i++){
                Node tmp = current.children.get(i);
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

    private static void show() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else {
            Node n = tree.searchNode(current, args[1]);
            System.out.println(n.item.content);
        }
    }

    private static void whereis() throws ClassNotFoundException {
        String[] args = input.split(" ");
        searchAll(root, args[1]);
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
        commands.put("cp", () -> {
            try {
                cp();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("ls", () -> ls());
        commands.put("show", () -> {
            try {
                show();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
        String[] s = arg.split("/");
        //fill here pa
    }

    private static void openEditor(Node n) {
        Scanner sc = new Scanner(System.in);
        String tmp = "";
        do{
            tmp =  sc.nextLine();
            if(!(tmp.equals(":q!")))
                n.item.content += tmp + "\n";
        }while(!(tmp.equals(":q!")));
        
        StringBuilder x = new StringBuilder(n.item.content);
        x.setLength(x.length()-1);
        n.item.content = x.toString();
    }

    private static ArrayList<Node> findAll(Node current, String arg) {
        ArrayList<Node> match = new ArrayList<>();
            
        String search = arg.replaceAll("\\*", "\\\\w*");
        for(int i=0; i<current.children.size(); i++){
            Node tmp = current.children.get(i);
            if(tmp.item.name.matches(search)){
                match.add(tmp);
            }
        }
        return match;
    }
    
    public static Date getDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        return date;
    }
}
