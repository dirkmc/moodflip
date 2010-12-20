package json

import models._
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import com.google.gson._
import play.data.validation.{Error=>PlayError}


object MFSerializer {
    val builder = new GsonBuilder()
    builder.registerTypeAdapter(classOf[User], new UserSerializer)
    builder.registerTypeAdapter(classOf[List[_]], new ListSerializer)
    builder.registerTypeAdapter(classOf[scala.collection.immutable.$colon$colon[_]], new CollectionSerializer)
    builder.registerTypeAdapter(classOf[State], new StateSerializer)
    builder.registerTypeAdapter(classOf[PlayError], new PlayErrorSerializer)
    val gson = builder.create
    
    def toJson(obj: Any) = gson.toJson(obj)
}

class PlayErrorSerializer extends JsonSerializer[PlayError] {
    override def serialize(obj: PlayError, objType: Type, context: JsonSerializationContext): JsonElement = {
        // This is really nasty, but necessary - getting private field values
        val message = getPrivateFieldValue(obj, "message").asInstanceOf[String]
        val variables = getPrivateFieldValue(obj, "variables").asInstanceOf[Array[String]]
        
        val json = new JsonObject
        json.addProperty("field", obj.getKey)
        json.addProperty("code", message)
        json.add("variables", context.serialize(variables))
        json.addProperty("message", obj.message())
        
        json
    }
    
    def getPrivateFieldValue(error: PlayError, fieldName: String): Any = {
        val field = classOf[PlayError].getDeclaredField(fieldName)
        field.setAccessible(true)
        field.get(error)
    }

}

class ListSerializer extends JsonSerializer[List[_]] {
    override def serialize(items: List[_], objType: Type, context: JsonSerializationContext): JsonElement = {
        val json = new JsonArray()
        items.foreach(item => json.add(context.serialize(item)))
        json
    }
}

class CollectionSerializer extends JsonSerializer[scala.collection.immutable.$colon$colon[_]] {
    override def serialize(items: scala.collection.immutable.$colon$colon[_], objType: Type, context: JsonSerializationContext): JsonElement = {
        val json = new JsonArray()
        items.foreach(item => json.add(context.serialize(item)))
        json
    }
}
