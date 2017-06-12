### Mini Twitter

##### Instructions to clone, build and start the server (Requires Gradle 3.x and Java 8+)

- git clone git@github.com:ashb001/thousand_eyes.git
- cd thousand_eyes/challenge
- ./gradlew build && java -jar build/libs/challenge-0.0.1-SNAPSHOT.jar

##### Example usage (Note: The password of each user is the same as their username) 

##### Get the list of superman's followers

curl http://localhost:8080/followers --user superman:superman

##### Get the list of people that superman is following:

curl http://localhost:8080/following --user superman:superman

##### Start following catwoman

curl http://localhost:8080/follow/catwoman --user superman:superman

##### Unfollow catwoman

curl http://localhost:8080/unfollow/catwoman --user superman:superman

##### Retrieve the list of related messages for superman

curl http://localhost:8080/messages?search=consequat --user superman:superman

##### Retrieve a list of all users, paired with their most "popular" follower

curl http://localhost:8080/popularFollowers --user superman:superman
