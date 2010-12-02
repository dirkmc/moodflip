package models

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
/*
object States extends QueryOn[User] {
}
*/