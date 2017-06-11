package challenge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import challenge.dao.TweetDao;
import challenge.pojo.Message;
import challenge.pojo.Person;

@RestController
public class TweetController {
    
    @Autowired
    private TweetDao tweetDao;

    /** Returns a list of all users, paired with their most "popular" follower **/
    @RequestMapping(method = RequestMethod.GET, value = "/popularFollowers")    
    public List<Map<String, String>> popularFollowers() {
        return tweetDao.mostPopularFollowersReport();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/messages")    
    public List<Message> relatedMessagesOfCurrentUser(HttpServletRequest request, 
            @RequestParam("search") Optional<String> keyword) {
        String currentUser = getUsernameFromAuthHeader(request);
        return tweetDao.relatedMessagesOf(currentUser, keyword);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/followers")    
    public List<Person> followersOfCurrentUser(HttpServletRequest request) {
        String currentUser = getUsernameFromAuthHeader(request);
        return tweetDao.followersOf(currentUser);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/following")
    public List<Person> followedByCurrentUser(HttpServletRequest request) {
        String currentUser = getUsernameFromAuthHeader(request);
        return tweetDao.followedBy(currentUser);
    }

    @RequestMapping("/follow/{userhandle}")
    public List<Person> followUser(HttpServletRequest request, @PathVariable String userhandle) throws Exception {
        String currentUser = getUsernameFromAuthHeader(request);
        tweetDao.startFollowing(userhandle, currentUser);
        return tweetDao.followedBy(currentUser);
    }
    
    @RequestMapping("/unfollow/{userhandle}")
    public List<Person> unfollowUser(HttpServletRequest request, @PathVariable String userhandle) throws Exception {
        String currentUser = getUsernameFromAuthHeader(request);
        tweetDao.stopFollowing(userhandle, currentUser);
        return tweetDao.followedBy(currentUser);
    }
    
    @ExceptionHandler
    private void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
    
    private String getUsernameFromAuthHeader(HttpServletRequest httpRequest) {
        
        final String authorization = httpRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
            final String[] values = credentials.split(":");
            return values[0];
        }
        
        return null;
    }

    
}