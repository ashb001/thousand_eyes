package challenge;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import challenge.pojo.Message;
import challenge.pojo.Person;
import challenge.service.TweetProviderService;

@RestController
public class TweetController {
    
    @Autowired
    private TweetProviderService tweetProviderService;

    /** Returns a list of all users, paired with their most "popular" follower, ordered by person_id **/
    @RequestMapping(method = RequestMethod.GET, value = "/popularFollowers")    
    public List<Map<String, String>> popularFollowers() {
        return tweetProviderService.popularFollowers();
    }
    
    /** Gets messages the user sent, plus the messages sent by users they follow **/
    /** The (optional) search param does a case-insensitive search thru messages **/
    @RequestMapping(method = RequestMethod.GET, value = "/messages")    
    public List<Message> relatedMessagesOfCurrentUser(@RequestParam("search") Optional<String> keyword) {
        return tweetProviderService.relatedMessagesOfCurrentUser(keyword);
    }

    /** Gets the list of followers for the user **/
    @RequestMapping(method = RequestMethod.GET, value = "/followers")    
    public List<Person> followersOfCurrentUser() {
        return tweetProviderService.followersOfCurrentUser();
    }

    /** Gets the list of people that the user is currently following **/
    @RequestMapping(method = RequestMethod.GET, value = "/following")
    public List<Person> followedByCurrentUser() {
        return tweetProviderService.followedByCurrentUser();
    }

    /** Follows a user, and echoes the entire list of people that the user is now following **/
    @RequestMapping("/follow/{userhandle}")
    public List<Person> followUser(@PathVariable String userhandle) throws Exception {
        return tweetProviderService.followUser(userhandle);
    }
    
    /** Unfollows a user, and echoes the entire list of people that the user is now following **/
    @RequestMapping("/unfollow/{userhandle}")
    public List<Person> unfollowUser(@PathVariable String userhandle) throws Exception {
        return tweetProviderService.unfollowUser(userhandle);
    }
    
}