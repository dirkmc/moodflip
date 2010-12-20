package api

import org.junit.After
import play.mvc.Http.Response
import captcha.CaptchaManager
import models.User
import play.test.Fixtures
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.{Map=>JMap, HashMap=>JHashMap}


class AddUserTestCase extends ApiTestCase {
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        CaptchaManager.enabled = false
        
        val user = new User("sadguy", "password", "Sad Guy")
        user.save
        user.setState(false)
        user.save
    }
    
    @After
    def tearDown(): Unit = {
        Fixtures.deleteAll()
        CaptchaManager.enabled = true
    }
    
    def addUserPOST(username: String, password: String, name: String, captcha: String = null): Response = {
        val parameters: JMap[String, String] = new JHashMap[String, String]()
        parameters.put("user.username", username)
        parameters.put("user.password", password)
        parameters.put("user.name", name)
        if(captcha == null) parameters.put("captcha", "captcha")
        val files: JMap[String, File] = new JHashMap[String, File]()
        return POST("/api/user", parameters, files)
    }
    
    def expectError(username: String, password: String, name: String, field: String,
                    code: String, captcha: String = null): Response = {
        val response = addUserPOST(username, password, name, captcha)
        response statusShouldBe(400)
        val errors = parseErrors(response)
        errors should have size 1
        
        val errorList: Array[ErrorResponse] = errors(field)
        errorList.foreach{ error => {
            error.field should equal (field)
            error.code should equal (code)
        }}
        
        response
    }
    
    @Test
    def addUser = {
        val response = addUserPOST("bob", "newpass", "Test Bob")
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The user should not yet have a state or friends, and the password
        // should not be exposed
        checkUser(user, "bob", "Test Bob", System.currentTimeMillis, null, null, Nil)
    }
    
    @Test
    def addUserNoCaptcha: Unit = {
        CaptchaManager.enabled = true
        expectError("bob", "newpass", "Test Bob", "captcha", "validation.required", "")
    }
    
    @Test
    def addUserBadCaptcha: Unit = {
        CaptchaManager.enabled = true
        expectError("bob", "newpass", "Test Bob", "captcha", "validation.captcha", "badcaptcha")
    }
    
    @Test
    def addUserNoUsername: Unit = {
        expectError("", "newpass", "Test Bob", "user.username", "validation.required")
    }
    
    @Test
    def addUserNoPassword: Unit = {
        expectError("bob", "", "Test Bob", "user.password", "validation.required")
    }
    
    @Test
    def addUserUsernameExists: Unit = {
        expectError("bob", "newpass", "Test Bob", "user.username", "validation.unique")
    }
    
}
