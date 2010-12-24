package api

import play.mvc.Http.Response
import captcha.CaptchaManager
import models.User
import play.test.Fixtures
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.{Map=>JMap, HashMap=>JHashMap}


class UsernameExistsTestCase extends ApiTestCase {
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        
        val user = new User("testguy", "password", "Test Guy")
        user.save
    }
    
    @Test
    def usernameExists = {
        val response = GET("/api/user/testguy/exists")
        response shouldBeOk()
        val userExists = parseUserExists(response)
        userExists.exists should be ("true")
    }
    
    @Test
    def usernameDoesntExist = {
        val response = GET("/api/user/newguy/exists")
        response shouldBeOk()
        val userExists = parseUserExists(response)
        userExists.exists should be ("false")
    }
    
}
