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

class SearchTestCase extends ApiTestCase {
    var userId = 0l
    var createTime = 0l
    val username = "Aladdin"
    val password = "open sesame"
    val name = "Test Guy"
    val mood = true
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        
        val user = new User(username, password, name)
        user.save
        user.setState(mood)
        user.save
        
        userId = user.id
        createTime = user.created.getTime
        
        val user2 = new User("sadguy", "password", "Sad Guy")
        user2.save
        user2.setState(false)
        user2.save
    }
    
    @Test
    def search = {
        val response = GET("/api/user/search/Guy")
        
        response shouldBeOk()
        val users = parseUsers(response)
        
        users should have size (2)
        var foundUser = false
        users.foreach(user => {
            if(user.username.equals(this.username)) {
                foundUser = true
                checkUser(user, this.username, this.name, createTime, this.mood.toString, null, Nil)
            }
        })
        
        foundUser should be (true)
    }
}
