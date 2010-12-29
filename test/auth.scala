import play.mvc.Http._ 
import play.test._
import org.junit._
import models._

class AuthTest extends FunctionalTestCase with Matchers {
    var userId = 0l
    val username = "Aladdin"
    val password = "open sesame"
    val name = "Test Guy"

    @Before
    def init = {
        Fixtures.deleteAll()
        
        val user = new User(username, password, name)
        user.save
        user.setState(true)
        user.save
        userId = user.id
    }
    
    @Test
    def testNoAuth = {
        val response = GET("/api/user/" + userId)
        response shouldBeOk()
    }
    
    @Test
    def testBasicAuthFailure = {
        val response = POST("/api/user/auth/mood/true")
        response statusShouldBe(StatusCode.UNAUTHORIZED)
    }
    
    @Test
    def testBasicAuthSuccess = {
        val request = newRequest()
        val url = "/api/user/auth/mood/true"
        request.headers.put("authorization", new Header("authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ=="))
        request._init();
        val response = POST(request, url)
        response shouldBeOk()
    }
}
