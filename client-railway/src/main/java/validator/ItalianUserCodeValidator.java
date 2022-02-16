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

@FacesValidator("italianUserCodeValidator")
public class ItalianUserCodeValidator implements Validator {
    private static final String CODE_PATTERN = "^[0-9A-Z]{16}$";

    private Pattern pattern;
    private Matcher matcher;
    private Integer userCodeLength = 16;

    public ItalianUserCodeValidator() {
        pattern = Pattern.compile(CODE_PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

    	String userCode = value.toString();
        matcher = pattern.matcher(userCode);
        if (!matcher.matches()) {

            FacesMessage msg = new FacesMessage("User Code validation failed.", "Italian user code MUST be composed by 16 characters.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
        
        List<Integer> map= Arrays.asList(1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13, 15, 17,
        		19, 21, 2, 4, 18, 20, 11, 3, 6, 8, 12, 14, 16, 10, 22, 25, 24, 23);
        
        int s = 0;
        
        for(int i = 0; i < userCodeLength-1; i++) {
        	int codeChar = Character.codePointAt(userCode, i);
        	if(codeChar < 65)
        		codeChar = codeChar - 48;
        	else
        		codeChar = codeChar - 55;
        	if(i%2 == 0)
        		s += map.get(codeChar);
        	else
        		s += codeChar < 10? codeChar : codeChar - 10;
        }
        
        s = 65 + s % 26;
        char c = (char)s;
        String expected = Character.toString(c);
        
        if(expected.charAt(0) != userCode.charAt(userCodeLength-1)) {
        	FacesMessage msg = new FacesMessage("User Code validation failed.", "Invalid italian user code.");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }
}