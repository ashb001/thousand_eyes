package challenge.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import challenge.pojo.Message;
import challenge.pojo.Person;

public interface TweetProviderService {
    
    List<Map<String, String>> popularFollowers();
    
    List<Message> relatedMessagesOfCurrentUser(Optional<String> keyword);
    
    List<Person> followersOfCurrentUser();
    
    List<Person> followedByCurrentUser();
    
    List<Person> followUser(String userhandle) throws IllegalArgumentException;
    
    List<Person> unfollowUser(String userhandle) throws IllegalArgumentException;
   
}