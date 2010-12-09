package api

import org.junit.Test
import java.io.File
import java.util.{Map=>JMap, HashMap=>JHashMap}


class AddUserTestCase extends ApiTestCase {
    
    @Test
    def addUser = {
        val newUsername = "bob"
        val newPassword = "newpass"
        val newName = "Test Bob"
        val parameters: JMap[String, String] = new JHashMap[String, String]()
        parameters.put("user.username", newUsername)
        parameters.put("user.password", newPassword)
        parameters.put("user.name", newName)
        val files: JMap[String, File] = new JHashMap[String, File]()
        val response = POST("/api/user", parameters, files)
        
        response shouldBeOk()
        val user = parseUser(response)
        
        // The user should not yet have a state or friends, and the password
        // should not be exposed
        checkUser(user, newUsername, newName, System.currentTimeMillis, null, null, Nil)
    }
}
