package api

import java.util.Date
import java.lang.reflect.Type
import com.google.gson.InstanceCreator
import org.scalatest.matchers.ShouldMatchers
import com.google.gson.reflect.TypeToken
import com.google.gson.GsonBuilder
import java.io.File
import play.mvc.Http._ 
import play.test._
import org.junit._
import models._
import java.util.{Map=>JMap, HashMap=>JHashMap}

class GetUserTestCase extends ApiTestCase {
    var userId = 0l
    var createTime = 0l
    val username = "Aladdin"
    val password = "open sesame"
    val name = "Test Guy"
    val mood = true
    
    var userId2 = 0l
    var createTime2 = 0l
    val username2 = "nomood"
    val password2 = "password"
    val name2 = "No mood"
    
    var userId3 = 0l
    var createTime3 = 0l
    val username3 = "sadguy"
    val password3 = "pass"
    val name3 = "Sad Guy"
    val mood3 = false
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        
        val user = new User(username, password, name)
        user.save
        user.setState(mood)
        user.save
        
        userId = user.id
        createTime = user.created.getTime
        
        
        val user2 = new User(username2, password2, name2)
        user2.save
        
        userId2 = user2.id
        createTime2 = user2.created.getTime
        
        
        val user3 = new User(username3, password3, name3)
        user3.save
        user3.setState(mood3)
        user3.friends.add(user)
        user3.friends.add(user2)
        user3.save
        
        userId3 = user3.id
        createTime3 = user3.created.getTime
        
    }
    
    @Test
    def getUser = {
        val response = GET("/api/user/" + userId)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The password should not be exposed
        checkUser(user, username, name, createTime, mood.toString, null)
    }
    
    @Test
    def getUserNoState = {
        val response = GET("/api/user/" + userId2)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The mood should not be set
        checkUser(user, username2, name2, createTime2, null, null)
    }
    
    @Test
    def getUserWithFriends = {
        val response = GET("/api/user/" + userId3)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The user should have friends
        checkUser(user, username3, name3, createTime3, mood3.toString, null, Array(userId, userId2))
    }
    
    @Test
    def getUserDoesntExist = {
        val response = GET("/api/user/999999")
        response shouldNotBeFound()
    }
    
}
