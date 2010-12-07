package json

import models._
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import com.google.gson._

object MFSerializer {
    val builder = new GsonBuilder()
    builder.registerTypeAdapter(classOf[User], new UserSerializer)
    builder.registerTypeAdapter(classOf[List[_]], new ListSerializer)
    builder.registerTypeAdapter(classOf[scala.collection.immutable.$colon$colon[_]], new CollectionSerializer)
    builder.registerTypeAdapter(classOf[State], new StateSerializer)
    val gson = builder.create
    
    def toJson(obj: Any) = gson.toJson(obj)
}

class ListSerializer extends JsonSerializer[List[_]] {
    override def serialize(items: List[_], objType: Type, context: JsonSerializationContext): JsonElement = {
        val json = new JsonArray()
        items.foreach(item => json.add(context.serialize(item)))
        return json
    }
}

class CollectionSerializer extends JsonSerializer[scala.collection.immutable.$colon$colon[_]] {
    override def serialize(items: scala.collection.immutable.$colon$colon[_], objType: Type, context: JsonSerializationContext): JsonElement = {
        val json = new JsonArray()
        items.foreach(item => json.add(context.serialize(item)))
        return json
    }
}
