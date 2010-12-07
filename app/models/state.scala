package models

import java.lang.reflect.Type
import com.google.gson._
import java.util.{Date,TreeSet,Set=>JSet,List=>JList,ArrayList}
import play.db.jpa._
import play.data.Validators._

@Entity
class State(
    @ManyToOne
    var user: User,

    var mood: Boolean
    
) extends Model {
    var created: Date = new Date
    
    
    override def toString() = moodToString(mood) + " " + created

    def moodToString(mood: Boolean) = if(mood) "Happy" else "Sad"

}

class StateSerializer extends JsonSerializer[State] {
    override def serialize(state: State, objType: Type, context: JsonSerializationContext): JsonElement = {
        return new JsonPrimitive(state.mood)
    }
}
