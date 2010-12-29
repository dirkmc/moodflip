package api

import scala.collection.mutable.HashMap
import java.util.Date
import java.lang.reflect.{Type, ParameterizedType}
import com.google.gson._
import org.scalatest.matchers.ShouldMatchers
import com.google.gson.reflect.TypeToken
import java.io.File
import play.mvc.Http._ 
import play.test._
import org.junit._
import models._
import java.util.{Map=>JMap, HashMap=>JHashMap}

abstract class ApiTestCase extends FunctionalTestCase with Matchers with ShouldMatchers {

    def checkUser(user: UserResponse, username: String, name: String, time: Long, mood: String,
            password: String, friendCount: Long = 0) {
        user.username should be (username)
        user.name should be (name)
        user.created.getTime should be (time plusOrMinus 2000)
        user.mood should be (mood)
        user.password should be (password)
        user.friendCount should be (friendCount)
    }
    
    def parseUsers(r: Response): Array[UserResponse] = parseResponse(r, classOf[Array[UserResponse]])
    
    def parseUser(r: Response): UserResponse = parseResponse(r, classOf[UserResponse])
    
    def parseUserExists(r: Response): UserExistsResponse = parseResponse(r, classOf[UserExistsResponse])
    
    def parseUpdates(r: Response): Array[UpdateResponse] = parseResponse(r, classOf[Array[UpdateResponse]])
    
    def parseError(r: Response): ErrorResponse = parseResponse(r, classOf[ErrorResponse])
    
    def parseErrors(response: Response): Map[String, Array[ErrorResponse]] = {
        val classType = new TypeToken[JHashMap[String, Array[ErrorResponse]]]() {}.getType()
        val content = response.out.toString("utf-8")
        val res: JHashMap[String, Array[ErrorResponse]] = gson().fromJson(content, classType)
        val map = new HashMap[String, Array[ErrorResponse]]()
        res.keySet.toArray.foreach{ key => map += key.toString -> res.get(key) }
        Map[String, Array[ErrorResponse]]() ++ map
    }
    
    def parseResponse[T](response: Response, classType: java.lang.reflect.Type): T = {
        val content = response.out.toString("utf-8")
        return gson().fromJson(content, classType).asInstanceOf[T]
    }
    
    def gson() = {
        val builder = new GsonBuilder()
        builder.registerTypeAdapter(classOf[UserResponse], new UserResponseIC)
        builder.registerTypeAdapter(classOf[UserExistsResponse], new UserExistsResponseIC)
        builder.registerTypeAdapter(classOf[UpdateResponse], new UpdateResponseIC)
        builder.registerTypeAdapter(classOf[ErrorResponse], new ErrorResponseIC)
        builder.registerTypeAdapter(classOf[List[_]], new ListIC)
        builder.create
    }
    
    case class UserResponse(id: String, username: String, password: String, name: String,
            mood: String, created: Date, friendCount: Long)
    class UserResponseIC extends InstanceCreator[UserResponse] {
        def createInstance(classType: Type): UserResponse = new UserResponse(null, null, null, null, null, null, 0)
    }
    
    case class UserExistsResponse(exists: String)
    class UserExistsResponseIC extends InstanceCreator[UserExistsResponse] {
        def createInstance(classType: Type): UserExistsResponse = new UserExistsResponse(null)
    }
    
    case class UpdateResponse(id: Long, userId: Long, username: String, name: String, created: Date, mood: Boolean)
    class UpdateResponseIC extends InstanceCreator[UpdateResponse] {
        def createInstance(classType: Type): UpdateResponse = new UpdateResponse(0, 0, null, null, null, false)
    }
    
    case class ErrorResponse(field: String, code: String, message: String, variables: Array[String])
    class ErrorResponseIC extends InstanceCreator[ErrorResponse] {
        def createInstance(classType: Type): ErrorResponse = new ErrorResponse(null, null, null, null)
    }
    
    class ListIC extends InstanceCreator[List[_]] {
        def createInstance(classType: Type): List[_] = Nil
    }
}
