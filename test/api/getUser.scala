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
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        
        val user = new User(username, password, name)
        user.save
        user.setState(mood)
        user.save
        
        userId = user.id
        createTime = user.created.getTime
    }
    
    @Test
    def getUser = {
        val response = GET("/api/user/" + userId)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The password should not be exposed
        checkUser(user, this.username, this.name, createTime, this.mood.toString, null, Nil)
    }
}
