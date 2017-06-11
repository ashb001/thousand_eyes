package challenge.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import challenge.pojo.Message;
import challenge.pojo.Person;

@Repository
public class TweetDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    @Transactional(readOnly=true)
    public List<Message> relatedMessagesOf(String currentUser, Optional<String> keyword) {
        
        List<Message> messages;
        
        if (keyword.isPresent()) {
            messages = jdbcTemplate.query(GET_MESSAGES_WITH_KEYWORD_SQL,
                    new MapSqlParameterSource("userHandle", currentUser)
                        .addValue("key", "%" + keyword.get().toLowerCase() + "%"), 
                    (resultSet, i) -> {
                        return toMessage(resultSet);
                    });
            
        } else {
            messages = jdbcTemplate.query(GET_MESSAGES_SQL,
                    new MapSqlParameterSource("userHandle", currentUser), 
                    (resultSet, i) -> {
                        return toMessage(resultSet);
                    });
        }
        
        
        return messages;
    }

    @Transactional(readOnly=true)
    public List<Person> followersOf(String currentUser) {
        
        List<Person> followers = jdbcTemplate.query(GET_FOLLOWERS_OF_USER_SQL,
                new MapSqlParameterSource("userHandle", currentUser), 
                (resultSet, i) -> {
                    return toPerson(resultSet);
                });
        
        return followers;
    }

    @Transactional(readOnly=true)
    public List<Map<String, String>> mostPopularFollowersReport() {
        
        return jdbcTemplate.query(MOST_POPULAR_FOLLOWER_SQL,
                (MapSqlParameterSource) null, 
                (resultSet, i) -> {
                    Map<String, String> map = new HashMap<>();
                    map.put(resultSet.getString("USER"), resultSet.getString("MOST_POPULAR_FOLLOWER"));
                    return map;
                });
        
    }
    
    @Transactional(readOnly=true)
    public List<Person> followedBy(String currentUser) {
        
        List<Person> followers = jdbcTemplate.query(GET_FOLLOWED_BY_USER_SQL,
                new MapSqlParameterSource("userHandle", currentUser), 
                (resultSet, i) -> {
                    return toPerson(resultSet);
                });
        
        return followers;
    }

    @Transactional
    public int startFollowing(String leaderHandle, String followerHandle) throws Exception {
        
        Person leader = findPersonByHandle(leaderHandle);
        if (leader == null) {
            throw new IllegalArgumentException("Cannot unfollow " + leaderHandle + ". Person doesn't exist.");
        }
        
        Person follower = findPersonByHandle(followerHandle);
        
        if (leader.equals(follower)) {
            throw new IllegalArgumentException("Cannot follow oneself.");
        }

        List<Person> peopleFollowed = followedBy(followerHandle);
        if (peopleFollowed.contains(findPersonByHandle(leaderHandle))) {
            return 0; //already following this user.
        }
        
        return jdbcTemplate.update(INSERT_FOLLOWER_RELATIONSHIP_SQL, 
                new MapSqlParameterSource("userId", leader.getId())
                .addValue("followerId", follower.getId()));
    }

    @Transactional
    public int stopFollowing(String leaderHandle, String followerHandle) throws Exception {
        
        Person leader = findPersonByHandle(leaderHandle);
        if (leader == null) {
            throw new IllegalArgumentException("Cannot unfollow " + leaderHandle + ". Person doesn't exist.");
        }
        
        Person follower = findPersonByHandle(followerHandle);
        
        return jdbcTemplate.update(DELETE_FOLLOWER_RELATIONSHIP_SQL, 
                new MapSqlParameterSource("userId", leader.getId())
                    .addValue("followerId", follower.getId()));
    }
    
    private Person toPerson(ResultSet resultSet) throws SQLException {
        Person person = new Person();
        person.setId(resultSet.getLong("ID"));
        person.setHandle(resultSet.getString("HANDLE"));
        person.setName(resultSet.getString("NAME"));
        return person;
    }

    private Message toMessage(ResultSet resultSet) throws SQLException {
        Message message = new Message();        
        message.setId(resultSet.getLong("ID"));
        message.setPersonId(resultSet.getLong("PERSON_ID"));
        message.setContent(resultSet.getString("CONTENT"));
        return message;
    }
    
    private Person findPersonByHandle(String userHandle) {
        List<Person> people = jdbcTemplate.query(
                GET_USER_FROM_HANDLE_SQL,
                new MapSqlParameterSource("userHandle", userHandle),  
                (resultSet, i) -> {
                    return toPerson(resultSet);
                });

        return CollectionUtils.isEmpty(people) ? null : people.get(0);
    }
    
    private final static String GET_FOLLOWERS_OF_USER_SQL = "select * from People where id in "
            + "(select f.follower_person_id from people p, followers f "
            + "where p.handle = :userHandle and p.id = f.person_id) "
            + "order by id ";

    private final static String GET_FOLLOWED_BY_USER_SQL = "select * from People where id in "
            + "(select f.person_id from people p, followers f "
            + "where p.handle = :userHandle and p.id = f.follower_person_id) "
            + "order by id ";
    
    private final static String INSERT_FOLLOWER_RELATIONSHIP_SQL = 
            "insert into followers (person_id, follower_person_id) values(:userId, :followerId)";

    private final static String DELETE_FOLLOWER_RELATIONSHIP_SQL = 
            "delete from followers where person_id = :userId and follower_person_id = :followerId";
 
    private final static String GET_USER_FROM_HANDLE_SQL = 
            "select * from people p where p.handle = :userHandle";
    
    private final static String GET_USER_ID_FROM_HANDLE = "(select id from people where handle = :userHandle)";
    
    private final static String GET_MESSAGES_SQL = 
            "select * from messages where person_id IN (" +
            "select person_id from followers where follower_person_id IN " + GET_USER_ID_FROM_HANDLE
            + " union " + GET_USER_ID_FROM_HANDLE + ") order by person_id";

    private final static String GET_MESSAGES_WITH_KEYWORD_SQL = 
            "select * from messages where person_id IN (" +
            "select person_id from followers where follower_person_id IN " + GET_USER_ID_FROM_HANDLE
            + " union " + GET_USER_ID_FROM_HANDLE + ") "
            + "and lower(content) like :key order by person_id";
    
    private final static String MOST_POPULAR_FOLLOWER_SQL = 
            "select P.handle as USER, "
                    + "(select  pl.handle from followers f, people pl "
                    + "where f.person_id = pl.id "
                    + "and f.person_id in (select follower_person_id from followers where person_id = P.id) "
                    + "group by f.person_id "
                    + "order by count(*) desc limit 1) as MOST_POPULAR_FOLLOWER "
            + "from people P";
    
}
