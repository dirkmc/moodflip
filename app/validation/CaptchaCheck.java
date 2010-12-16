package validation;


import java.util.HashMap;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import captcha.CaptchaManager;

public class CaptchaCheck extends AbstractAnnotationCheck<Captcha> {
    public static final String mes = "validation.captcha";
    
    @Override
    public void configure(Captcha unique) {
        //System.out.println("configure");
        //this.min = range.min();
        //this.max = range.max();
        //setMessage(range.message());
        setMessage(unique.message());
    }

    @Override
    public boolean isSatisfied(Object validatedObject, Object value, OValContext context, Validator validator) {
        if(!CaptchaManager.enabled()) return true;
        
        String captcha = (String)value;
        if(captcha == null || captcha.equals("")) return true;
        
        //System.out.println("satisfied?");
        return false;
    }
    

    @Override
    public Map<String, String> createMessageVariables() {
        Map<String, String> messageVariables = new HashMap<String, String>();
        return messageVariables;
    }
}
