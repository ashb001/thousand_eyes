package challenge.dao;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import challenge.pojo.Message;
import challenge.pojo.Person;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication
public class TweetDaoTest
{
    @Autowired
    private TweetDao tweetDao;
 
    @Test
    public void testFollowers() {

        // Expected followers of "batman"
           Set<String> expectedFollowers = 
                new HashSet<String>(Arrays.asList("catwoman", "alfred", "dococ", "spiderman", "ironman", "profx"));
 
        List<Person> followers = tweetDao.followersOf("batman");
        
        Set<String> actualFollowers = new HashSet<>();
        
        for (Person person : followers) {
            actualFollowers.add(person.getHandle());
        }
        
        Assert.assertTrue(expectedFollowers.equals(actualFollowers));
        
    }

    @Test
    public void testFollowing() {

        // Expected list of people that "batman" is following
           Set<String> expectedFollowing = 
                new HashSet<String>(Arrays.asList("superman", "daredevil", "alfred", "spiderman", "profx"));
 
        List<Person> followers = tweetDao.followedBy("batman");
        
        Set<String> actualFollowers = new HashSet<>();
        
        for (Person person : followers) {
            actualFollowers.add(person.getHandle());
        }
        
        Assert.assertTrue(expectedFollowing.equals(actualFollowers));
        
    }
    
    @Test
    public void beginAndStopFollowing() throws Exception {
        
        // Let "batman" start following "zod", and then validate the list 
        // of people that batman is following.
        
        tweetDao.startFollowing("zod", "batman");
        
        // Expected list of people that "batman" is following
           Set<String> expectedFollowing = 
                new HashSet<String>(Arrays.asList("zod", "superman", "daredevil", "alfred", "spiderman", "profx"));
 
        List<Person> followers = tweetDao.followedBy("batman");
        
        Set<String> actualFollowers = new HashSet<>();
        
        for (Person person : followers) {
            actualFollowers.add(person.getHandle());
        }
        
        Assert.assertTrue(expectedFollowing.equals(actualFollowers));

        // Now test stopFollowing
        tweetDao.stopFollowing("zod", "batman");
  
        followers = tweetDao.followedBy("batman");
        
        actualFollowers.clear();
        for (Person person : followers) {
            actualFollowers.add(person.getHandle());
        }

        expectedFollowing.remove("zod");
        Assert.assertTrue(expectedFollowing.equals(actualFollowers));
        
    }
    
    @Test
    public void testMessages() throws Exception {
        
        String searchKey = null;
        List<Message> messageList = tweetDao.relatedMessagesOf("zod", Optional.ofNullable(searchKey));
        Assert.assertNotNull(messageList);

        searchKey = "Maecenas ornare egestas";
        messageList = tweetDao.relatedMessagesOf("zod", Optional.ofNullable(searchKey));
        Assert.assertEquals(2, messageList.size());
        
    }

    @Test
    public void testPopularFollowers() {

        // For batman (person_id = 1), the most popular follower should be spiderman
        // For catwoman (person_id = 3), the most popular follower should be zod
        // For spiderman (person_id = 8), the most popular follower should be alfred

    	List<Map<String, String>> report = tweetDao.mostPopularFollowersReport();
        Assert.assertTrue(report.get(0).get("batman").equals("spiderman"));
        Assert.assertTrue(report.get(2).get("catwoman").equals("zod"));
        Assert.assertTrue(report.get(7).get("spiderman").equals("alfred"));

        
        
    }
    
}