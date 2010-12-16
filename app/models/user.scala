package models

import com.google.gson.reflect.TypeToken
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type
import com.google.gson._
import java.util.{Date,TreeSet,Set=>JSet,List=>JList,ArrayList,Map=>JMap,HashMap}
import play.db.jpa._
import play.data.Validators._

@Entity
class User(
    @Required
    var username: String,
    
    @Required
    var password: String,
    
    @Required
    var name: String
    
) extends Model {
    
    var created: Date = new Date
    
    @OneToMany(mappedBy="user", cascade=Array(CascadeType.ALL))
    var states: JList[State] = new ArrayList[State]

    @ManyToMany
    var friends: JList[User] = new ArrayList[User]

    override def toString() = username

    def setState(mood: Boolean): User = {
        val state:State = new State(this, mood).save()
        states.add(state)
        return this
    }
    
    def state(): State = if(states.size > 0) states.get(states.size() - 1) else null
    
    def this() = this(null, null, null)
}

object User extends QueryOn[User] {
    def search(query: String): List[User] = {
        if(query == null || query.trim.isEmpty)
            return List[User]()
        
        val like = "%"+query+"%"
        find("username like :query or name like :query", Map("query" -> like)).fetch
    }
}

case class UserJson(id: Long, username: String, name: String, created: Date, state: State, friends: List[Long])

class UserSerializer extends JsonSerializer[User] {
    override def serialize(user: User, objType: Type, context: JsonSerializationContext): JsonElement = {
        var friends: List[Long] = asScala.asList(user.friends).map(friend => friend.id)
        val json = UserJson(user.id, user.username, user.name, user.created, user.state(), friends)
        context.serialize(json)
    }
}

