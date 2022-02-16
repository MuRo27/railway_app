package core.dao.interfaces;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
 
import javax.inject.Qualifier;
 
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
 
/**
 * The JPA DAO Qualifier.
 * 
 * @author Buhake Sindi
 * @since 11 March 2014
 *
 * https://blogs.oracle.com/arungupta/totd-161:-java-ee-6-cdi-qualifiers-explained-default,-any,-new,-named
 * 
 * @Named is an instance of a @Qualifier which assigns a specific name to a bean. 
 * That name has to be unique across all beans, regardless of type.
 * 
 * Qualifier as a concept is a way of having multiple beans of the same type that 
 * are differentiated based on the various @Qualifier annotations they have.
 * 
 */
@Qualifier
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
public @interface JPADAO {
 
}
