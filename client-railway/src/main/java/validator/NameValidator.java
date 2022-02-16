package validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("nameValidator")
public class NameValidator implements Validator{
	
	private static final String PATTERN = "[a-zA-Z]+";
	
	private Pattern pattern;
    private Matcher matcher;
    
    public NameValidator() {
        pattern = Pattern.compile(PATTERN);
    }
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    	
    	String name = value.toString();
        matcher = pattern.matcher(name); 
        
    	if (!matcher.matches()) {
    		 FacesMessage msg = new FacesMessage("Name validation failed.", "It should contain only alphabetic characters.");
             msg.setSeverity(FacesMessage.SEVERITY_ERROR);
             throw new ValidatorException(msg);
    	 }
    }

}
