package challenge.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import challenge.dao.TweetDao;
import challenge.pojo.Message;
import challenge.pojo.Person;

@Service
public class TweetProviderServiceImpl implements TweetProviderService {

    @Autowired
    private TweetDao tweetDao;
    
    @Override
    /** Returns a list of all users, paired with their most "popular" follower, ordered by person_id **/
    public List<Map<String, String>> popularFollowers() {
        return tweetDao.mostPopularFollowersReport();
    }

    @Override
    /** Gets messages the user sent, plus the messages sent by users they follow **/
    /** The (optional) search param does a case-insensitive search thru messages **/
    public List<Message> relatedMessagesOfCurrentUser(Optional<String> keyword) {
        return tweetDao.relatedMessagesOf(getLoggedInUser(), keyword);
    }

    @Override
    /** Gets the list of followers for the user **/
    public List<Person> followersOfCurrentUser() {
        return tweetDao.followersOf(getLoggedInUser());
    }

    @Override
    /** Gets the list of people that the user is currently following **/
    public List<Person> followedByCurrentUser() {
        return tweetDao.followedBy(getLoggedInUser());
    }

    @Override
    /** Follows a user, and echoes the entire list of people that the user is now following **/
    public List<Person> followUser(String userhandle) throws IllegalArgumentException {
        tweetDao.startFollowing(userhandle, getLoggedInUser());
        return tweetDao.followedBy(getLoggedInUser());
    }
    
    @Override
    /** Unfollows a user, and echoes the entire list of people that the user is now following **/
    public List<Person> unfollowUser(String userhandle) throws IllegalArgumentException {
        tweetDao.stopFollowing(userhandle, getLoggedInUser());
        return tweetDao.followedBy(getLoggedInUser());
    }
    
    private String getLoggedInUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    
    @ExceptionHandler
    private void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
