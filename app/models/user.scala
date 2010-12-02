package models
 
import java.util.{Date,TreeSet,Set=>JSet,List=>JList,ArrayList}
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
}

object User extends QueryOn[User] {
    def search(query: String): List[User] = {
        if(query == null || query.trim.isEmpty)
            return List[User]()
        
        val like = "%"+query+"%"
        find("username like :query or name like :query", Map("query" -> like)).fetch
    }
}
