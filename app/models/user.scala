package models

import validation.Unique
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonPrimitive
import java.lang.reflect.Type
import com.google.gson._
import java.util.{Date,TreeSet,Set=>JSet,List=>JList,ArrayList,Map=>JMap,HashMap}
import scala.collection.JavaConversions._
import play.db.jpa._
import play.data.Validators._

import annotation.target.field

@Entity
class User(
    @(Unique @field)
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
        // If the state doesn't actually change, ignore the setState request
        val currentState = this.state()
        if(currentState != null && mood == currentState.mood) return this
        
        val state:State = new State(this, mood).save()
        states.add(state)
        return this
    }
    
    def state(): State = if(states.size > 0) states.get(states.size() - 1) else null
    
    def updates(since: Date) = {
        val updates = friends.flatMap(friend => friend.states.filter(state => state.created.getTime >= since.getTime))
        updates.sortWith{(first, second) => first.created.before(second.created)}.toArray
    }
    
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

case class UserJson(id: Long, username: String, name: String, created: Date, mood: State, friends: List[FriendJson])
case class FriendJson(id: Long, username: String, name: String, created: Date, mood: State)

class UserSerializer extends JsonSerializer[User] {
    override def serialize(user: User, objType: Type, context: JsonSerializationContext): JsonElement = {
        var friends: List[FriendJson] = asScala.asList(user.friends).map(friend => {
            new FriendJson(friend.id, friend.username, friend.name, friend.created, friend.state)
        })
        val json = UserJson(user.id, user.username, user.name, user.created, user.state, friends)
        context.serialize(json)
    }
}

case class UserUpdate(id: Long, userId: Long, username: String, name: String, created: Date, mood: Boolean)
class UserUpdateSerializer extends JsonSerializer[State] {
    override def serialize(update: State, objType: Type, context: JsonSerializationContext): JsonElement = {
        val user = update.user
        val json = UserUpdate(update.id, user.id, user.username, user.name, update.created, update.mood)
        context.serialize(json)
    }
}
