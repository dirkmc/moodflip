package validation

import play.exceptions.UnexpectedException
import net.sf.oval.context.FieldContext
import net.sf.oval.Validator
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck
import net.sf.oval.context.OValContext

import play.db.jpa._

class UniqueCheck extends AbstractAnnotationCheck[Unique] {
    override def isSatisfied(validatedObject: Object, value: Object, context: OValContext, validator: Validator) = {
        if(!validatedObject.isInstanceOf[play.db.jpa.Model] || !context.isInstanceOf[FieldContext]) {
            throw new UnexpectedException("@Unique annotation can only be applied to fields of a Model object")
        }
        
        val model = validatedObject.asInstanceOf[play.db.jpa.Model]
        val fieldName = context.asInstanceOf[FieldContext].getField().getName
        val count = JPQL.instance.count(validatedObject.getClass().getName, fieldName + " = ?", Array(value))
        count == 0
    }
}
