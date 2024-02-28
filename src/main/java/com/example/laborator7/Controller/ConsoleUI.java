//package com.example.laborator7.Controller;
//
//
//
//import com.example.laborator7.domain.CreatedTuple;
//import com.example.laborator7.domain.Friendship;
//import com.example.laborator7.domain.User;
//import com.example.laborator7.service.Service;
//import com.example.laborator7.validator.ValidationException;
//
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class ConsoleUI extends AbstractUI {
//    private Scanner scan;
//    public ConsoleUI(Service<Long> service){
//        super(service);
//        this.scan=new Scanner(System.in);
//    }
////    public void toIntroduce(){
////        User user1=new User("Ana","Ana");
////        user1.setId(1l);
////        User user2=new User("Dan","Dan");
////        user2.setId(2l);
////        User user3=new User("Ale","Ale");
////        user3.setId(3l);
////        User user4=new User("Sanda","Sanda");
////        user4.setId(4l);
////        service.addServiceUser(user1);
////        service.addServiceUser(user2);
////        service.addServiceUser(user3);
////        service.addServiceUser(user4);
////        Long userId1= user1.getId();
////        Long userId2= user2.getId();
////        Friendship friendship=new Friendship(userId1,userId2);
////        friendship.setId(new CreatedTuple<>(userId1,userId2));
////        service.addServiceFriendship(friendship);
////        Long userId3=user3.getId();
////        Friendship friendship1=new Friendship(userId1,userId3);
////        friendship1.setId(new CreatedTuple<>(userId1,userId3));
////        service.addServiceFriendship(friendship1);
////        int number=service.numberOfCommunity();
////        assert(number==2);
////        ArrayList<User> longestCommunity=service.theLongestComponent();
////        assert(longestCommunity.size()==3);
////
////        User user5=new User("V","V");
////        user5.setId(5l);
////        User user6=new User("S","S");
////        user6.setId(6l);
////        User user7=new User("U","U");
////        user7.setId(7l);
////        User user8=new User("P","P");
////        user8.setId(8l);
////        User user9=new User("Z","Z");
////        user9.setId(9l);
////        service.addServiceUser(user5);
////        service.addServiceUser(user6);
////        service.addServiceUser(user7);
////        service.addServiceUser(user8);
////        service.addServiceUser(user9);
////        Long userId4=user4.getId();
////        Friendship friendship2=new Friendship(userId3,userId4);
////        friendship2.setId(new CreatedTuple<>(userId3,userId4));
////        service.addServiceFriendship(friendship2);
////        Long userId5=user5.getId();
////        Long userId6=user6.getId();
////        Long userId7=user7.getId();
////        Long userId8=user8.getId();
////        Long userId9=user9.getId();
////        Friendship friendship3=new Friendship(userId5,userId6);
////        friendship3.setId(new CreatedTuple<>(userId5,userId6));
////        service.addServiceFriendship(friendship3);
////        Friendship friendship4=new Friendship(userId5,userId7);
////        friendship4.setId(new CreatedTuple<>(userId5,userId7));
////        service.addServiceFriendship(friendship4);
////        Friendship friendship5=new Friendship(userId5,userId8);
////        friendship5.setId(new CreatedTuple<>(userId5,userId8));
////        service.addServiceFriendship(friendship5);
////        Friendship friendship6=new Friendship(userId5,userId9);
////        friendship6.setId(new CreatedTuple<>(userId5,userId9));
////        service.addServiceFriendship(friendship6);
////    }
//    @Override
//    public void start(){
//        System.out.println("The functionalities are : ");
//        System.out.println("0 - exit");
//        System.out.println("1 - Add a user");
//        System.out.println("2 - Remove a user by his id");
//        System.out.println("3 - Add a friendship");
//        System.out.println("4 - Remove a friendship");
//        System.out.println("5 - Show all users");
//        System.out.println("6 - Show all friendships");
//        System.out.println("7 - Show number of community ");
//        System.out.println("8 - Show longest community");
//        System.out.println("9 - toIntroduce");
//        System.out.println("10 - Show friends for one user");
//        while(true){
//            System.out.println("The command is : ");
//            int command=scan.nextInt();
//            switch (command) {
//                case 0 -> {
//                    scan.close();
//                    scan.ioException();
//                }
//                case 1 -> {
//                    try {
//                        System.out.println("FirstName");
//                        String firstName = scan.next().toString();
//                        System.out.println("LastName");
//                        String lastName = scan.next().toString();
//                        User userForAdded = new User(firstName, lastName);
//                        service.addServiceUser(userForAdded);
//
//                    } catch (IllegalArgumentException | ValidationException v) {
//                        System.out.println(v.getMessage());
//                        break;
//                    }
//                    ;
//                }
//                case 2 -> {
//                    try {
//                        System.out.println("Please introduce an id: ");
//                        Long id = scan.nextLong();
//                        service.deleteServiceUser(id);
//                    } catch (IllegalArgumentException | ValidationException v) {
//                        System.out.println(v.getMessage());
//                        break;
//                    }
//                }
//                case 3 -> {
//                    try {
//                        System.out.println("Give the first id ");
//                        Long userId1 = scan.nextLong();
//                        System.out.println("Give the second id  ");
//                        Long userId2 = scan.nextLong();
//                        Friendship friendship = new Friendship(userId1, userId2);
//                        friendship.setId(new CreatedTuple<>(userId1, userId2));
//                        service.addServiceFriendship(friendship);
//
//
//                    } catch (ValidationException | IllegalArgumentException v) {
//                        System.out.println(v.getMessage());
//                        break;
//                    }
//                }
//                case 4 -> {
//                    try {
//                        System.out.println("Give the first id: ");
//                        Long userId1 = scan.nextLong();
//                        System.out.println("Give the second id:");
//                        Long userId2 = scan.nextLong();
//
//                        service.deleteServiceFriendship(userId1, userId2);
//                    } catch (IllegalArgumentException | ValidationException v) {
//                        System.out.println(v.getMessage());
//                        break;
//                    }
//                }
//                case 5 -> {
//                    Iterable<User> allUser = service.getAllUser();
//                    allUser.forEach(System.out::println);
//                }
//                case 6 -> {
//                    Iterable<Friendship> allFriendship = service.getAllFriendship();
//                    allFriendship.forEach(System.out::println);
//                }
//                case 7 -> {
//                    Iterable<Friendship> allFriendship1 = service.getAllFriendship();
//                    int number = service.numberOfCommunity();
//                    System.out.println(number);
//                }
//                case 8 -> {
//                    Iterable<Friendship> allFriendship2 = service.getAllFriendship();
//                    ArrayList<User> longest = service.theLongestComponentWithBfs();
//                    longest.forEach(System.out::println);
//                }
////                case 9->{
////                    toIntroduce();
////                }
//                case 10->{
//                    System.out.println("Give the id for user: ");
//                    Long userId1 = scan.nextLong();
//                    System.out.println("localtime: ");
//
//                    Integer month=scan.nextInt();
//
//
//                    ArrayList<String>strings=service.allStrings(userId1,month);
//                    strings.forEach(System.out::println);
//                }
//
//                default -> throw new IllegalStateException("Unexpected value: " + command);
//            }
//        }
//
//    }
//}
