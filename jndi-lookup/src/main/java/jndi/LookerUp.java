package jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
 
public class LookerUp {

	
	public Object findLocalSessionBean(String moduleName, String beanName, String interfaceFullQualifiedName) throws NamingException{
		 
		final Context context = new InitialContext();
		Object object = context.lookup("java:global/"+moduleName+"/"+beanName+"!"+interfaceFullQualifiedName);
		context.close();
 
		return object;
	}
 
 
	public Object findSessionBean(String jndiName) throws NamingException{
 
		final Context context = new InitialContext();
		Object object = context.lookup(jndiName);
		context.close();
 
		return object;
	}
 
}