/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralTree;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author testuser
 */
public class FileSystem implements Serializable{
    static Node root; 
    static Node current;
    static Map<String, Runnable> commands = new HashMap();
    static Tree tree = new Tree();
    static String input; 
    static FileOutputStream fileOut;
    static ObjectOutputStream out;
    static Scanner in;
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
       System.out.println("Virtual Terminal");
       System.out.println("Type \"quit\" to terminate program. ");
        System.out.println("For editor, type \":q!\" to close editor. \n");
        
        initCommands();
        readFromFile();
        deserialize();
        
        fileOut = new FileOutputStream("filesystem.ser");
        out = new ObjectOutputStream(fileOut);
        
        
        do{
            System.out.print("test@user:");
            getAbsolutePath(current);
            System.out.print("$ ");
            if( in!=null && in.hasNext() ){
                input = in.nextLine();
                System.out.println(input);
            }
            else{
                Scanner sc = new Scanner(System.in);
                input = sc.nextLine();
            }
            if(input.isEmpty())
                continue;
            else if (input.equals("quit"))
                break;
            try{
                commands.get(getCommand(input)).run();
            } catch (Exception e){
                System.out.println("Check commands");
            }
            currentpath = "";
        } while (true);    
        
        serialize(root);
        out.close();
        fileOut.close();
    }
    
    private static void mkdir() throws ClassNotFoundException, IOException { 
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");

        else{
                for(int j=1; j<args.length; j++){
                    String[] s = args[j].split("/");
                    List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                    node_path.removeAll(Collections.singleton(""));

                    String new_item = node_path.remove(node_path.size()-1);
                    
                    if(!node_path.isEmpty()){
                        String path = "";
                        if(node_path.contains("..")){
                            path = findPath2(args[1]);
                            String[] tmp = path.split("/");
                            
                            path = "";
                            for(int i=0; i<tmp.length-1; i++){
                                path += tmp[i] + "/";
                            }
                            
                        }
                        else {
                            for(int i=0; i<node_path.size(); i++){
                                path += node_path.get(i) + "/";
                            }
                            path = appendPath(path);
                        }
                        
                        if (checkPathExists(path)){
                            Node n = recursive_search(path);
                            Node new_item_node = new Node(new FileDescriptor(new_item, getDate(), true));
                            if(!(tree.insertNode(n,new_item_node)))
                                System.out.println(n.item.name + " already exists in " + current.item.name);
                        }
                        else{
                            System.out.println(path + " does not exist.");
                        }
                    }
                    else{
                        //for mkdir /this
                        Node new_item_node = new Node(new FileDescriptor(new_item, getDate(), true));
                        if(!(tree.insertNode(current,new_item_node)))
                            System.out.println(new_item_node.item.name + " already exists in " + current.item.name);
                    }
            }
           
        }
    }

    private static void remove() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else {
            for(int j=1; j<args.length; j++){
                    String[] s = args[j].split("/");
                    List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                    node_path.removeAll(Collections.singleton(""));

                    String del = node_path.remove(node_path.size()-1);
                    
                    if(node_path.size() > 0){
                        String path = "";
                        if(node_path.contains("..")){
                            path = findPath2(args[1]);
                            String[] tmp = path.split("/");
                            
                            path = "";
                            for(int i=0; i<tmp.length-1; i++){
                                path += tmp[i] + "/";
                            }
                            
                        }
                        else {
                            for(int i=0; i<node_path.size(); i++){
                               path += node_path.get(i) + "/";
                            }
                            path = appendPath(path);
                        }
                       
                            
                        if (checkPathExists(path)){
                            Node n = recursive_search(path);
                            if(del.contains("*")){
                                ArrayList<Node> list = findAll(n, del);
                                for(int i=0; i<list.size(); i++){
                                    tree.deleteNode(n,list.get(i));
                                }
                            }
                            else{
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
                        else{
                            System.out.println(path + " does not exist");
                        }
                    }
                    else{
                        if(del.contains("*")){
                            ArrayList<Node> list = findAll(current, del);
                            for(int i=0; i<list.size(); i++){
                                tree.deleteNode(current,list.get(i));
                            }
                        }
                        else{
                            if(del.contains("/"))
                                del = del.replace("/", "");
                            Node del_node = tree.searchNode(current, del);
                            if((getCommand(input).equals("rm") && !del_node.item.isDirectory) ||
                               (getCommand(input).equals("rmdir") && del_node.item.isDirectory)    ){
                                if(!(tree.deleteNode(current, del_node)))
                                    System.out.println(del + " does not exist in " + current.item.name);
                            }
                            else{
                                if(getCommand(input).equals("rm") && del_node.item.isDirectory)
                                    System.out.println(del_node.item.name + " is a directory.");
                                if(getCommand(input).equals("rmdir") && !del_node.item.isDirectory) 
                                    System.out.println(del_node.item.name + " is not a directory.");
                            }
                        }
                    }
                }
        }
    }

    private static void cd() throws ClassNotFoundException {
        String[] args = input.split(" ");
        String go;
        if(args.length < 2)
            System.out.println("Missing arguments.");
        else if(args[1].contains("..")){
            go = args[1];
            String s = findPath2(args[1]);
            if(checkPathExists(s)){
                Node n;
                n = recursive_search(s);
                if(!n.item.isDirectory)
                    System.out.println(n.item.name + " is not a directory.");
                else
                    current = n;
            }
            else{
                System.out.println(args[1] + " does not exist.");
            }
        }
        else if(!args[1].contains("/")){
            go = args[1]; 
            Node n = tree.searchNode(current, go);
              
           
            if(n==null)
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

            String[] s = args[1].split("/");
            List<String> node_path = new ArrayList<String>(Arrays.asList(s));
            node_path.removeAll(Collections.singleton(""));

            file = node_path.remove(node_path.size()-1);

            if(!node_path.isEmpty()){
                String path = "";
                if(node_path.contains("..")){
                           path = findPath2(args[1]);
                           String[] tmp = path.split("/");

                           path = "";
                           for(int i=0; i<tmp.length-1; i++){
                               path += tmp[i] + "/";
                           }

                }
                else {
                    for(int i=0; i<node_path.size(); i++){
                        path += node_path.get(i) + "/";
                    }
                    path = appendPath(path);
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
                    if(!n.item.isDirectory){
                        if(args[0].equals(">"))
                            n.item.content = "";
                        else {
                            System.out.println(n.item.content);
                            n.item.content += "\n";
                        }
                        openEditor(n);
                        n.item.last_modified = getDate();
                    }
                    else {
                        System.out.println(file + " is directory.");
                    }
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
            String oldname = args[1];
            String newname = args[2];
            boolean flag = false;
            if(args[1].contains("/")){
                String[] s = args[1].split("/");
                List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                node_path.removeAll(Collections.singleton(""));

                String filename = node_path.remove(node_path.size()-1);
                String path = "";
                
                if(node_path.contains("..")){
                    path = findPath2(args[1]);
                    String[] tmp = path.split("/");

                    path = "";
                    for(int i=0; i<tmp.length-1; i++){
                        path += tmp[i] + "/";
                    }

                }               
                else{
                    for(int i=0; i<node_path.size(); i++){
                        path += node_path.get(i) + "/";
                    }
                    path = appendPath(path);
                }
                
                if (!checkPathExists(path)){
                   System.out.println(path + " does not exist.");
                   flag = true;
                }
                else{
                    Node location = recursive_search(path);
                    if(location == current){
                        if(tree.searchNode(current, filename)!=null)
                            oldname = filename;
                    }
                    else{
                        System.out.println("Cant rename files from other directory.");
                        flag = true;
                    }
                }
            }
            if(args[2].contains("/")){
                String[] s = args[2].split("/");
                List<String> node_path = new ArrayList<String>(Arrays.asList(s));
                node_path.removeAll(Collections.singleton(""));

                String filename = node_path.remove(node_path.size()-1);
                newname=filename;
            }
            
            if(flag == false){
                if(tree.searchNode(current, oldname)!=null){
                    if(tree.searchNode(current, newname)!=null){
                       System.out.println(newname + " already exists.");
                    }
                    else{
                       tree.searchNode(current, oldname).item.name = newname;
                    }
                }
                else{
                    System.out.println(oldname + " does not exist.");
                }
            }
        }
        
    }

    private static void mv() throws ClassNotFoundException {
        String[] args = input.split(" ");
        if(args.length < 3)
            System.out.println("Missing arguments.");
        else{
            Node file;
            
            String[] s = args[1].split("/");
            List<String> node_path = new ArrayList<String>(Arrays.asList(s));
            node_path.removeAll(Collections.singleton(""));
            String fname = node_path.remove(node_path.size()-1);
            
            String path = "";
            
            if(node_path.contains("..")){
                path = findPath2(args[1]);
                String[] tmp = path.split("/");

                path = "";
                for(int i=0; i<tmp.length-1; i++){
                    path += tmp[i] + "/";
                }
            }     
            else {
                for(int i=0; i<node_path.size(); i++){
                    path += node_path.get(i) + "/";
                }

                path = appendPath(path);
            }
            
            file = tree.searchNode(recursive_search(path), fname);
            
            if(file == null){
                System.out.println(args[1] + " does not exist.");
            }
            
            else{
                
                if(!args[2].contains("root")){
                    String tmp = args[2].replace(current.item.name, "");
                    args[2] = currentpath + "/" + tmp; 
                }
                 
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
                if(new_node.item.isDirectory){
                    copyNodes(tobecopied, new_node);
                }
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
        if(args.length < 2){
            for(int i=0, ctr=0; i<current.children.size(); i++){
                Node tmp = current.children.get(i);
//                String f = (tmp.item.isDirectory) ? "directory" : "file";
//                System.out.print("Name: " + tmp.item.name + " (" + f + ")\n" + "Date Created: " + 
//                        tmp.item.created.toGMTString().replace("GMT", "") + "\n" + "Last Modified: " + tmp.item.last_modified.toGMTString().replace("GMT", ""));
                
               String f = (tmp.item.isDirectory) ? "/" : "";
               System.out.println(tmp.item.name + f);
               // System.out.println("\n");
            }
        }
        
        else if (args[1].contains("*")){
            String[] s = args[1].split("/");
            List<String> node_path = new ArrayList<String>(Arrays.asList(s));
            node_path.removeAll(Collections.singleton("")); 
            String search = node_path.remove(node_path.size()-1).replaceAll("\\*", "\\\\w*");
            
            ArrayList<Node> match = new ArrayList<>();
            
            if(node_path.isEmpty()){
                for(int i=0; i<current.children.size(); i++){
                    Node tmp = current.children.get(i);
                    if(tmp.item.name.matches(search)){
                        match.add(tmp);
                    }
                }
            }
            else{
                String path = "";
                for(int i=0; i<node_path.size(); i++){
                    path += node_path.get(i) + "/";
                }

                path = appendPath(path);

                if (checkPathExists(path)){
                    Node n = recursive_search(path);
                    for(int i=0; i<n.children.size(); i++){
                        Node tmp = n.children.get(i);
                        if(tmp.item.name.matches(search)){
                            match.add(tmp);
                        }
                    }
                }
                else{
                    System.out.println(path + " does not exist.");
                }
            }
            for(int i=0; i<match.size(); i++){
                System.out.print(match.get(i).item.name + " ");
            }
            System.out.println("");
        }
        else {
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
                System.out.println(args[1] + " does not exist in " + current.item.name);
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
        searchAll(root, args[1].replace("/", ""));
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
            } catch (ClassNotFoundException | IOException ex) {
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
        commands.put(">", () ->  {
            try {
                edit();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        commands.put(">>", () ->  {
            try {
                edit();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }
    
    public static String getCommand(String input){
        return input.split(" ")[0];
    }
    
    static String currentpath = "";
    private static void getAbsolutePath(Node cur){
        Node p = cur;
        if(p.parent!=null){
           getAbsolutePath(p.parent);
           currentpath += "/" + p.item.name;
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
        
        if(!n.item.content.isEmpty()){
            StringBuilder x = new StringBuilder(n.item.content);

            x.setLength(x.length()-1);
            n.item.content = x.toString();
        }
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
    
    private static String findPath2(String arg) throws ClassNotFoundException {
        String[] s = arg.split("/");
        List<String> node_path = new ArrayList<String>(Arrays.asList(s));
        node_path.removeAll(Collections.singleton(""));
        
        String path = "";
        
        if(node_path.size() == 1){
           path = current.parent.item.name;
        }
        else {
            Stack address = new Stack();
            Node tmp_node = current;
            for(int i=s.length-1; i>=0; i--){
                String tmp = node_path.get(i);
                if(!tmp.equals("..")){
                    address.add(tmp);
                }
                else{
                    address.add(tmp_node.parent.item.name);
                    tmp_node = tmp_node.parent;
                }
            }
            
            path += address.get(address.size()-1)+ "/";
            for(int i=0; i<s.length; i++){
                String tmp = node_path.get(i);
                if(!tmp.equals("..")){
                    path+= tmp + "/";
                }
            }
        }
        return path;
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

    private static void copyNodes(Node tobecopied, Node new_node) {
        for(int i=0; i<tobecopied.children.size(); i++){
            Node tmp = new Node(new FileDescriptor(tobecopied.children.get(i).item.name, getDate(), tobecopied.children.get(i).item.isDirectory));
            tmp.item.content = tobecopied.children.get(i).item.content;
            if(tmp.item.isDirectory){
                //fix this
            }
            tree.insertNode(new_node, tmp);
        }
    }

    private static String appendPath(String path) {
        if(!path.contains("root")){
            String tmp = path.replace(current.item.name, "");
            path = currentpath + "/" + tmp; 
        }
        return path;
    }

    private static void deserialize() {
        Node r = null;
        try {
            FileInputStream fileIn = new FileInputStream("filesystem.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            r = (Node) in.readObject();
            root = r;
            current = root;
            in.close();
            fileIn.close();
        } catch(IOException | ClassNotFoundException i ) {
            root  = new Node(new FileDescriptor("root", getDate(), true));
            current = root;
        }
    }

    private static void serialize(Node n) throws IOException {
        out.writeObject(n);
        for(int i=0; i<n.children.size(); i++){
           Node tmp = n.children.get(i);
           if(tmp.item.isDirectory){
               serialize(tmp);
           }
           else{
               out.writeObject(tmp);
           }
       }
    }

    private static void readFromFile() throws FileNotFoundException {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter filename: ");
            String filename = sc.nextLine();
            in = new Scanner(new FileReader(filename));
        } catch (Exception e){
            System.out.println("File does not exist.\n\n");
        }
    }   
}