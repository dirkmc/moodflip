package captcha

import scala.reflect.BeanProperty
import play.db.jpa.GenericModel
import play.cache.Cache
import play.libs.Images

object CaptchaManager {
    class CaptchaAuth(var id: String, var value: String) {
        def this() = this(null, null) // necessary for binding to work
    }
    
    var enabled = true
    
    // This is just used for unit testing
    var lastValue:String = null
    
    def generate(id: String) = {
        val captcha = Images.captcha
        val value = captcha.getText
        lastValue = value
        Cache.set(getCaptchaId(id), value, "10min")
        captcha
    }
    
    def check(captcha: CaptchaAuth) = {
        val captchaId = getCaptchaId(captcha.id)
        val code = Cache.get[String](captchaId)
        Cache.delete(captchaId)
        
        code match {
            case Some(value) => value.length > 0 && value.equals(captcha.value)
            case None => false
        }
    }
    
    def getCaptchaId(id: String) = "captcha-" + id
}
