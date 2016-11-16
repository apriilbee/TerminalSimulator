///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package GeneralTree;
//
//import GeneralTree.FileDescriptor;
//import GeneralTree.Node;
//import GeneralTree.Tree;
//import static GeneralTree.Tree.getDate;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//
///**
// *
// * @author testuser
// */
//public class Tester {
//    static Tree t = new Tree();
////    public static void main(String[] args) throws ClassNotFoundException {
////        int ch = 0;
////        System.out.print("Menu\n1. Insert\n2. Delete\n3. Search\n4. Display\n5. Change Directory\n"
////                + "6. Back\n7. Exit");
////        do{
////            Scanner sc = new Scanner(System.in);
////            System.out.print("\n\nChoice: ");
////            ch = sc.nextInt();
////            
////            switch(ch){
////                case 1: {
////                    insert();
////                    break;
////                }
////                case 2: {
////                    delete();
////                    break;
////                }
////                case 3: {
////                    search();
////                    break;
////                }
////                case 4: {
////                    display();
////                    break;
////                } 
////                case 5: {
////                    chdir();
////                    
////                    break;
////                }
////                case 6: {
////                    goBack();
////                    break;
////                }
////                default:
////                    ch = sc.nextInt();
////            }
////            getAbsolutePath(t.current);
////            System.out.println("");
////        }while(ch!=7);
////    }
//
//    
//    private static void insert(){
//        Scanner sc = new Scanner(System.in);
//      
//        System.out.print("Menu\n1. Insert File\n2. Insert Directory\n3.Cancel\nChoice: ");
//        int ch = sc.nextInt();
//        
//        Scanner sc2 = new Scanner(System.in);
//        System.out.print("Title: ");
//        String title = sc2.nextLine();
//        switch(ch){
//                case 1: {
//                    //make file lang 
//                    //kuwang pa ni ug automatic open
//                    Node n = new Node(new FileDescriptor(title, getDate(), false));
//                    if(!(t.insertNode(t.current,n)))
//                        System.out.println(n.item.name + " already exists in " + t.current.item.name);
//                    break;
//                }
//                case 2: {
//                    //mkdir
//                    Node n = new Node(new FileDescriptor(title, getDate(), true));
//                     if(!(t.insertNode(t.current,n)))
//                        System.out.println(n.item.name + " already exists in " + t.current.item.name);
//                    break;
//                }
//                case 3: break;
//                default:
//                    ch = sc.nextInt();
//        }
//            
//    }
//    
//    private static void delete() throws ClassNotFoundException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Delete: ");
//        String del = sc.nextLine();
//        if(!(t.deleteNode(t.current,t.searchNode(t.current, del))))
//            System.out.println("not in here");
//    }
//
//    private static void search() throws ClassNotFoundException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Regex: ");
//        String r = sc.nextLine();
//        ArrayList<Node> n = t.search(t.current, r);
//        for(int i=0; i<n.size(); i++){
//            System.out.println(n.get(i).item.name);
//        }
//        
//    }
//
//    private static void display() {
//        if(t.current.children.isEmpty()){
//            System.out.println("");
//        }
//        else{
//            t.displayAll(t.current, "");
//        }
//    }
//
//    private static void chdir() throws ClassNotFoundException {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Dir name: ");
//        String dir = sc.nextLine();
//        Node n = t.searchNode(t.current, dir);
//        if(dir.equals("root"))
//            t.current = t.root;
//        else if(n==null)
//            System.out.println("Directory does not exist");
//        else if (!n.item.isDirectory)
//            System.out.println(dir + " is not a directory");
//        else{
//            t.current = n;
//            System.out.println(t.current.item.name + " thishere");
//        }
//    }
//    
//    private static void goBack(){
//        if(t.current.parent!=null)
//            t.current = t.current.parent;
//        else {
//            System.out.println("Already in root node.");
//        }
//    }
//    
//   
//
//}
