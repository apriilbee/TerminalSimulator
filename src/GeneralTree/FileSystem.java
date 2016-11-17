/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
            if(input.isEmpty())
                continue;
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
            String[] s = args[1].split("/");
            List<String> node_path = new ArrayList<String>(Arrays.asList(s));
            node_path.removeAll(Collections.singleton(""));
            
            String new_item = node_path.remove(node_path.size()-1);
            String path = "";
            for(int i=0; i<node_path.size(); i++){
                path += node_path.get(i) + "/";
            }
            if (checkPathExists(path)){
                Node n = recursive_search(path);
                Node new_item_node = new Node(new FileDescriptor(new_item, getDate(), true));
                if(!(tree.insertNode(n,new_item_node)))
                    System.out.println(n.item.name + " already exists in " + current.item.name);
            }
        }
    }

    private static void remove() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else {
            if(!args[1].contains("/") && !(args[1].contains("*"))){
                for(int i=1; i<args.length; i++){
                    Node del = tree.searchNode(current, args[i]);
                    if((getCommand(input).equals("rm") && !del.item.isDirectory) ||
                       (getCommand(input).equals("rmdir") && del.item.isDirectory)    ){
                        if(!(tree.deleteNode(current,del)))
                            System.out.println(args[1] + " does not exist.");
                    }
                    else{
                        if(getCommand(input).equals("rm") && del.item.isDirectory)
                            System.out.println(del.item.name + " is a directory.");
                        if(getCommand(input).equals("rmdir") && !del.item.isDirectory) 
                            System.out.println(del.item.name + " is not a directory.");
                        break;
                    }
                }
            }
            else if(args[1].contains("/")){
                String[] s = args[1].split("/");
                List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                node_path.removeAll(Collections.singleton(""));

                String del = node_path.remove(node_path.size()-1);
                String path = "";
                for(int i=0; i<node_path.size(); i++){
                    path += node_path.get(i) + "/";
                }
                if (checkPathExists(path)){
                    Node n = recursive_search(path);
                    Node del_node = tree.searchNode(n, del);
                    if((getCommand(input).equals("rm") && !del_node.item.isDirectory) ||
                       (getCommand(input).equals("rmdir") && del_node.item.isDirectory)    ){
                        if(!(tree.deleteNode(n, del_node)))
                            System.out.println(del + " does not exist in " + n.item.name);
                    }
                    else{
                        if(getCommand(input).equals("rm") && del_node.item.isDirectory)
                            System.out.println(del_node.item.name + " is a directory.");
                        if(getCommand(input).equals("rmdir") && !del_node.item.isDirectory) 
                            System.out.println(del_node.item.name + " is not a directory.");
                    }
                }
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
                System.out.println(args[1] + " does not exist");
            else if (!n.item.isDirectory)
                System.out.println(args[1] + " is not a directory");
            else{
                current = n;
            }
        }
        else{
            if(checkPathExists(args[1])){
                String[] s = args[1].split("/");
                Node n;
                n = recursive_search(args[1]);
                if(!n.item.isDirectory)
                    System.out.println(n.item.name + " is not a directory.");
                else
                    current = n;
            }
            else{
                System.out.println(args[1] + " does not exist in " + current.item.name);
            }
        }
    }

    private static void edit() throws ClassNotFoundException {
        String[] args = input.split(" ");
        boolean flag = false;
        Node location = current;
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else{
            String file; 
            if(!args[1].contains("/"))
               file = args[1];
            else{
                String[] s = args[1].split("/");
                List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                node_path.removeAll(Collections.singleton(""));

                file = node_path.remove(node_path.size()-1);
                String path = "";
                for(int i=0; i<node_path.size(); i++){
                    path += node_path.get(i) + "/";
                }
                if (!checkPathExists(path)){
                   System.out.println(path + " does not exist.");
                   flag = true;
                }
                else{
                    location = recursive_search(path);
                }
            }
            if(flag==false){
                if(tree.searchNode(location, file)!=null){
                    Node n = tree.searchNode(location, file);
                    System.out.println(n.item.content);
                    n.item.content += "\n";
                    openEditor(n);
                    n.item.last_modified = getDate();
                }
                else{
                    Node n = new Node(new FileDescriptor(file, getDate(), false));
                    if(!(tree.insertNode(location,n)))
                       System.out.println(n.item.name + " already exists in " + location.item.name);
                    else{
                        Scanner sc = new Scanner(System.in);
                        String tmp;
                        openEditor(n);
                        n.item.last_modified = getDate();
                    }
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

    private static void mv() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 3)
            System.out.println("Missing arguments.");
        else{
            Node file = tree.searchNode(current, args[1]);
            if(file == null){
                System.out.println(args[1] + " does not exist.");
            }
            else{
                if(checkPathExists(args[2])){
                    Node dest = recursive_search(args[2]);
                    if(tree.checkNodeExists(dest, file))
                        System.out.println(args[1] + " already in " + args[2]);
                    else{
                        current.children.remove(file);
                        dest.children.add(file);
                        file.parent = dest;
                    }
                }
                else{
                    System.out.println(args[2] + " does not exist.");
                }
            }
        }
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
    
    
    private static void ls() throws ClassNotFoundException {
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
            if(checkPathExists(args[1])){
                Node n = recursive_search(args[1]);
                if(n.item.isDirectory){
                    for(int i=0, ctr=0; i<n.children.size(); i++){
                        Node tmp = n.children.get(i);
                        System.out.print("Name: " + tmp.item.name + "\n" + "Date Created: " + tmp.item.created.toGMTString().replace("GMT", "") + "\n" + "Last Modified: " + tmp.item.last_modified.toGMTString().replace("GMT", ""));
                        System.out.println("\n");
                    }
                }
            }
            else{
                System.out.println(args[1] + " does not exist.");
            }
        }
    }

    private static void show() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else {
            Node n = null;
            if(!args[1].contains("/")){
                n = tree.searchNode(current, args[1]);
            }
            else{
                if(checkPathExists(args[1])){
                    n = recursive_search(args[1]);
                }
                else{
                    System.out.println("File does not exist.");
                }
            }
            if(n==null)
                System.out.println("File does not exist.");
            else if(!n.item.isDirectory)
                System.out.println(n.item.content);
            else if(n.item.isDirectory)
                System.out.println(args[1] + " is a directory.");
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
        commands.put("mv", () -> {
            try {
                mv();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("cp", () -> {
            try {
                cp();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put("ls", () -> {
            try {
                ls();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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
    
    private static boolean checkPathExists(String arg) throws ClassNotFoundException {
        String[] s = arg.split("/");
        List<String> node_path = new ArrayList<String>(Arrays.asList(s));
        node_path.removeAll(Collections.singleton(""));
        int i = (node_path.get(0).equals("root")) ? 1 : 0;
        Node tmp =  root;
        Node tmp2 = null;
        for(;i<node_path.size(); i++){
            tmp2 = tree.searchNode(tmp,node_path.get(i));
            if(tmp2 == null)
                return false;
            else if(!(tree.checkNodeExists(tmp, tmp2)))
                return false;
            tmp = tree.searchNode(tmp,node_path.get(i));
        }
        return true;
    }

    
    public static Node recursive_search(String arg) throws ClassNotFoundException{
        String[] s = arg.split("/");
        List<String> node_path = new ArrayList<String>(Arrays.asList(s));
        node_path.removeAll(Collections.singleton(""));
        int i = (node_path.get(0).equals("root")) ? 1 : 0;
        Node tmp = root;
        Node tmp2 = null;
        for(;i<node_path.size(); i++){
             tmp2 = tree.searchNode(tmp,node_path.get(i));
            if(tmp2 == null)
                return null;
            else if(!(tree.checkNodeExists(tmp, tmp2)))
                return null;
            tmp = tree.searchNode(tmp,node_path.get(i));
        }
        return tmp;
    }
}
