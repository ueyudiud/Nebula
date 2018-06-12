/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base.register;

/**
 * @author ueyudiud
 */
public interface IRegisterElement<T> extends IRegisteredNameable
{
	void setRegistryName(String name);
	
	Class<T> getTargetClass();
}