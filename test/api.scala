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

class ApiTest extends FunctionalTestCase with Matchers with ShouldMatchers {
    var userId = 0l
    val username = "Aladdin"
    val password = "open sesame"
    val name = "Test Guy"

    case class UserResponse(username: String, password: String, name: String, state: String)
    class UserResponseIC extends InstanceCreator[UserResponse] {
        def createInstance(classType: Type): UserResponse = new UserResponse(null, null, null, null)
    }
    
    @Before
    def init = {
        Fixtures.delete()
        /*
        val user = new User(username, password, name)
        user.save
        user.setState(true)
        user.save
        userId = user.id
        */
    }
    
    @Test
    def addUser = {
        val parameters: JMap[String, String] = new JHashMap[String, String]()
        parameters.put("user.username", username)
        parameters.put("user.password", password)
        parameters.put("user.name", name)
        val files: JMap[String, File] = new JHashMap[String, File]()
        val response = POST("/api/user", parameters, files)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        user.username should be (this.username)
        user.name should be (this.name)
        // The user should not yet have a state 
        user.state should be (null)
        // The password should not be exposed
        user.password should be (null)
    }
    
    def parseUser(response: Response): UserResponse = {
        val classType = classOf[UserResponse]
        val builder = new GsonBuilder().registerTypeAdapter(classType, new UserResponseIC)
        val gson = builder.create
        val content = response.out.toString("utf-8");
        val user: UserResponse = gson.fromJson(content, classType)
        return user
    }
}
