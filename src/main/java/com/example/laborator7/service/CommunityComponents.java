package com.example.laborator7.service;


import com.example.laborator7.domain.User;
import com.example.laborator7.repository.CrudRepository;
import com.example.laborator7.repository.RepositoryWithOptional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CommunityComponents {
    private CrudRepository<Long, User> userRepository;

    //private Map<Long,User> entities;
    private int number;
    private int numberOfComponentsMax;
    private int numberOfPerson;

    private ArrayList<User> theLongestFriendsForOneUser;
    private boolean[] visited;

    static class Pair<T, V> {
        T first; // maximum distance Node
        V second; // distance of maximum distance node

        //Constructor
        Pair(T first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    public CommunityComponents(CrudRepository<Long, User> userRepository) {
        AtomicInteger count = new AtomicInteger();
//        for (User user : userRepository.findAll()) {
//            count++;
//        }
        userRepository.findAll().forEach(user -> {
            count.getAndIncrement();
        });

        this.numberOfPerson = count.get();
        this.userRepository = userRepository;
        this.numberOfComponentsMax = 0;
        this.number = 0;
        this.theLongestFriendsForOneUser = new ArrayList<>();
        // Alte inițializări
        visited = new boolean[numberOfPerson];
    }

    public ArrayList<User> getTheLongestFriendsForOneUser() {
        return theLongestFriendsForOneUser;
    }

    public void setTheLongestFriendsForOneUser(ArrayList<User> theLongestFriendsForOneUser) {
        this.theLongestFriendsForOneUser = theLongestFriendsForOneUser;
    }

    public ArrayList<User> theLongestCommunity() {

        var ref = new Object() {
            ArrayList<User> longest = new ArrayList<>();
        };
//        for(User user: userRepository.findAll()){
//
//            if (!visited[Math.toIntExact(user.getId())-1]) {
//                longest.add(user);
//                 number=0;
//                dfs(user,longest,number);
//                if(number>numberOfComponentsMax){
//                    numberOfComponentsMax=number;
//                    this.setTheLongestFriendsForOneUser(longest);
//
//                }
//                longest=new ArrayList<>();
//            }
//        }
        List<User> collect = StreamSupport.stream(userRepository.findAll().spliterator(), false).filter(user -> !visited[Math.toIntExact(user.getId()) - 1]).toList();
        collect.forEach(user -> {
            ref.longest.add(user);
            number = 0;
            dfs(user, ref.longest, number);
            Comparator<Integer> integerComparator = Integer::compare;
            //Predicate<Boolean> boolComparing=x->integerComparator.compare(number,numberOfComponentsMax)>0;
            if (integerComparator.compare(number, numberOfComponentsMax) > 0) {
                numberOfComponentsMax = number;
                this.setTheLongestFriendsForOneUser(ref.longest);

            }
            ref.longest = new ArrayList<>();
        });

        return this.theLongestFriendsForOneUser;
    }

    //    public ArrayList<User> theLongestCommunityWithBfs(){
//
//        ArrayList<User> longest=new ArrayList<>();
//        for(User user: userRepository.findAll()){
//
//            if (!visited[Math.toIntExact(user.getId())-1]) {
//                //longest.add(user);
//                number=0;
//                Pair<User,Integer> pair=bfs(user,longest,number);
//                shorter_lenght(user,pair.first,longest);
//                setNumber(pair.second);
//                if(number>numberOfComponentsMax){
//                    numberOfComponentsMax=number;
//                    this.setTheLongestFriendsForOneUser(longest);
//                }
//                longest=new ArrayList<>();
//            }
//        }
//        return this.theLongestFriendsForOneUser;
//    }
    public ArrayList<User> theLongestCommunityWithBfs() {

        var ref = new Object() {
            ArrayList<User> longest = new ArrayList<>();
        };
        List<User> collect = StreamSupport.stream(userRepository.findAll().spliterator(), false).filter(user -> !visited[Math.toIntExact(user.getId()) - 1]).toList();
        collect.forEach(user -> {
            //longest.add(user);
            number = 0;
            Pair<User, Integer> pair = bfs(user, ref.longest, number);
            shorter_lenght(user, pair.first, ref.longest);
            setNumber(pair.second);
            Comparator<Integer> integerComparator = Integer::compare;
            if (integerComparator.compare(number, numberOfComponentsMax) > 0) {
                numberOfComponentsMax = number;
                this.setTheLongestFriendsForOneUser(ref.longest);
            }
            ref.longest = new ArrayList<>();

        });
        return this.theLongestFriendsForOneUser;
    }

    private void shorter_lenght(User user, User theLostFriend, ArrayList<User> longest) {
        Queue<User> queue = new LinkedList<>();
        boolean[] visit = new boolean[numberOfPerson];
        int[] parent = new int[numberOfPerson];
        Arrays.fill(parent, -1);
        queue.add(user);
        visit[Math.toIntExact(user.getId()) - 1] = true;
        parent[Math.toIntExact(user.getId()) - 1] = -1;

        while (!queue.isEmpty()) {
            User current = queue.poll();
            if (current == theLostFriend) {
                // Am găsit destinația, construiește cel mai scurt drum
                //List<Integer> shortestPath = new ArrayList<>();
                User z = null;
                z = theLostFriend;
                while (z != null) {
                    longest.add(z);
                    Long idVertex = z.getId();
                    int index = parent[Math.toIntExact(z.getId()) - 1];
                    User x = userRepository.findOne(1L + index);
                    if (x!= null){
                        z = x;
                    } else {
                        z = null;
                    }
                }
                Collections.reverse(longest);

            }
            ArrayList<User> friendsForOneUser = (ArrayList<User>) current.getFriends();
            for (User frienOfUser : friendsForOneUser) {
                if (!visit[Math.toIntExact(frienOfUser.getId()) - 1]) {
                    queue.add(frienOfUser);
                    visit[Math.toIntExact(frienOfUser.getId()) - 1] = true;
                    int x = Math.toIntExact(current.getId()) - 1;
                    parent[Math.toIntExact(frienOfUser.getId()) - 1] = x;
                }
            }
        }
    }

    public int numberOfCommunities() {

        AtomicInteger number_community = new AtomicInteger();
        List<User> collect = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .toList();
        collect.forEach(user -> {
            if(!visited[Math.toIntExact(user.getId()) - 1]) {
                dfs(user, new ArrayList<>(), 0);
                number_community.getAndIncrement();
            }
        });
        return number_community.get();
    }

    public void setNumber(int number) {
        this.number = number;
    }

    Pair<User, Integer> bfs(User user, ArrayList<User> longest, int number) {
        boolean[] visit = new boolean[numberOfPerson];
        int[] dist = new int[numberOfPerson];
        Arrays.fill(dist, -1);
        Queue<User> queue = new LinkedList<>();
        visit[Math.toIntExact(user.getId()) - 1] = true;
        visited[Math.toIntExact(user.getId()) - 1] = true;
        queue.add(user);
        dist[Math.toIntExact(user.getId()) - 1] = 0;
        while (!queue.isEmpty()) {
            User current = queue.poll();
            ArrayList<User> friendsForOneUser = (ArrayList<User>) current.getFriends();
            friendsForOneUser.forEach( frienOfUser ->  {
                if (dist[Math.toIntExact(frienOfUser.getId()) - 1] == -1 && !visit[Math.toIntExact(frienOfUser.getId()) - 1]) {
                    queue.add(frienOfUser);
                    dist[Math.toIntExact(frienOfUser.getId()) - 1] = dist[Math.toIntExact(current.getId()) - 1] + 1;

                }
            });
        }
        User friend = null;
        for (int i = 0; i < dist.length; i++) {
            if (dist[i] > number) {
                number = dist[i];
                User opt = userRepository.findOne((long) i + 1);
                if (opt!= null) {
                    friend = opt;
                }
            }
        }
        return new Pair<User, Integer>(friend, number);
    }

    //Pair<User, Integer> bfs(User user, ArrayList<User>longest, int number) {
//    boolean[] visit=new boolean[numberOfPerson];
//        int[] dist =new int[numberOfPerson];
//        Arrays.fill(dist, -1);
//        Queue<User> queue = new LinkedList<>();
//        visit[Math.toIntExact(user.getId())-1] = true;
//        visited[Math.toIntExact(user.getId())-1] = true;
//        queue.add(user);
//        dist[Math.toIntExact(user.getId())-1]=0;
//        while (!queue.isEmpty()) {
//            User current = queue.poll();
//            //System.out.print(current + " ");
//            ArrayList<User>friendsForOneUser= (ArrayList<User>) current.getFriends();
//            List<User> collect =StreamSupport.stream(friendsForOneUser.spliterator(), false)
//                    .filter(friendOfUser -> (visited[Math.toIntExact(friendOfUser.getId()) - 1]==false) && (dist[Math.toIntExact(friendOfUser.getId()) - 1] == -1)).collect(Collectors.toList());
//                    collect.forEach(friendOfUser -> {
//                        //longest.add(friendOfUser);
//                        //visited[Math.toIntExact(friendOfUser.getId()) - 1] = true;
//                        queue.add(friendOfUser);
//                        dist[Math.toIntExact(friendOfUser.getId()) - 1] = dist[Math.toIntExact(current.getId()) - 1] + 1;
//                    });
//        }
//        User friend=null;
//        for(int i=0;i< dist.length;i++){
//            if(dist[i]>number){
//                number=dist[i];
//                Optional<User> opt=userRepository.findOne((long) i+1);
//                if(opt.isPresent()){
//                    friend=opt.get();
//                }
//            }
//        }
//        return new Pair<User,Integer>(friend,number);
//}
    private void dfs(User user, ArrayList<User> longest, int number) {

        visited[Math.toIntExact(user.getId()) - 1] = true;
        ArrayList<User> friendsForOneUser = (ArrayList<User>) user.getFriends();
        AtomicInteger count = new AtomicInteger();
        count.set(number);
        List<User> collect = friendsForOneUser.stream()
                .filter(friendOfUser -> (!visited[Math.toIntExact(friendOfUser.getId()) - 1])).toList();
        collect.forEach(friendOfUser -> {
            longest.add(friendOfUser);
            count.getAndIncrement();
            setNumber(count.get());
            setNumber(count.get());
            dfs(friendOfUser, longest, number);
        });

    }
}

