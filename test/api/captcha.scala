package api

import play.cache.Cache
import play.mvc.Http.Response
import captcha.CaptchaManager
import models.User
import play.test.Fixtures
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.{Map=>JMap, HashMap=>JHashMap}


class CaptchaTestCase extends ApiTestCase {
    
    @Before
    def init(): Unit = {
        Fixtures.deleteAll()
        CaptchaManager.enabled = true
        Cache.clear
        
        val user = new User("testguy", "password", "Test Guy")
        user.save
    }
    
    @Test
    def getCaptcha = {
        val response = GET("/api/captcha/12345678")
        response shouldBeOk()
    }
    
    @Test
    def checkCaptcha = {
        val id = "captchaid";
        val captchaResponse = GET("/api/captcha/"+id)
        captchaResponse shouldBeOk()
        
        val response = createNewUser("new guy", "new pass", "New Guy", id, CaptchaManager.lastValue)
        response shouldBeOk()
    }
    
    @Test
    def badCaptchaFails = {
        val id = "captchaid";
        val captchaResponse = GET("/api/captcha/"+id)
        captchaResponse shouldBeOk()
        
        val response = createNewUser("new guy", "new pass", "New Guy", id, "bad captcha")
        captchaError(response)
    }
    
    @Test
    def sameCaptchaTwiceFails = {
        val id = "captchaid";
        val captchaResponse = GET("/api/captcha/"+id)
        captchaResponse shouldBeOk()
        
        val response = createNewUser("new guy", "new pass", "New Guy", id, CaptchaManager.lastValue)
        response shouldBeOk()
        val response2 = createNewUser("some guy", "some pass", "Some Guy", id, CaptchaManager.lastValue)
        
        captchaError(response2)
    }
    
    def createNewUser(username: String, password: String, name: String,
            captchaId: String, captchaValue: String) = {
        val parameters: JMap[String, String] = new JHashMap[String, String]()
        parameters.put("user.username", username)
        parameters.put("user.password", password)
        parameters.put("user.name", name)
        parameters.put("captcha.id", captchaId)
        parameters.put("captcha.value", captchaValue)
        val files: JMap[String, File] = new JHashMap[String, File]()
        POST("/api/user", parameters, files)
    }
    
    def captchaError(response: Response) {
        response statusShouldBe(400)
        val errors = parseErrors(response)
        errors should have size 1
        
        val errorList: Array[ErrorResponse] = errors("captcha")
        errorList.foreach{ error => {
            error.field should equal ("captcha")
            error.code should equal ("validation.captcha")
        }}
    }
}
