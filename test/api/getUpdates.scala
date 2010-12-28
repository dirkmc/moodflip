package api

import java.text.SimpleDateFormat
import play.data.binding.types.DateBinder
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

class GetUpdatesTestCase extends ApiTestCase {
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
    }
    
    @Test
    def getUpdates = {
        var userId = 0l
        var createTime = 0l
        val username = "testguy"
        val password = "testicle"
        val name = "Test Guy"
        
        var userId2 = 0l
        var createTime2 = 0l
        val username2 = "fatguy"
        val password2 = "password"
        val name2 = "Fat Guy"
        
        var userId3 = 0l
        var createTime3 = 0l
        val username3 = "thinguy"
        val password3 = "password"
        val name3 = "Thin Guy"
        
        
        val user = new User(username, password, name)
        user.save
        
        // Set state of first user to false. This should be ignored because it
        // happens before the "since" date
        user.setState(false)
        user.save
        Thread.sleep(100)
        
        val sinceDate = new Date();
        
        // Set state of first user to true (first update)
        user.setState(true)
        user.save
        
        // Set state of first user to true again (this should be ignored because
        // the state wasn't actually changed)
        user.setState(true)
        user.save
        
        userId = user.id
        createTime = user.created.getTime
        
        
        val user2 = new User(username2, password2, name2)
        user2.save
        // Set state of second user to false (second update)
        user2.setState(false)
        user2.save
        
        userId2 = user2.id
        createTime2 = user2.created.getTime
        
        // Set state of first user to false (second update)
        user.setState(false)
        user.save
        
        
        val user3 = new User(username3, password3, name3)
        user3.save
        user3.setState(true)
        user3.friends.add(user)
        user3.friends.add(user2)
        user3.save
        
        userId3 = user3.id
        createTime3 = user3.created.getTime
        
        
        val isoFormat = "'ISO8601:'yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        val since = new SimpleDateFormat(isoFormat).format(sinceDate)
        val response = GET("/api/user/" + userId3 + "/updates/" + since)
        
        response shouldBeOk()
        val updates: Array[UpdateResponse] = parseUpdates(response)
        
        updates should have size (3)
        
        updates(0).userId should be (userId)
        updates(0).name should be (name)
        updates(0).username should be (username)
        updates(0).mood should be (true)
        
        updates(1).userId should be (userId2)
        updates(1).name should be (name2)
        updates(1).username should be (username2)
        updates(1).mood should be (false)
        
        updates(2).userId should be (userId)
        updates(2).mood should be (false)
    }
    
    @Test
    def noUser = {
        val date = new Date();
        val since = new SimpleDateFormat(DateBinder.ISO8601).format(date)
        val response = GET("/api/user/9999999999/updates/" + since)
        
        response shouldNotBeFound()
    }
    
}
