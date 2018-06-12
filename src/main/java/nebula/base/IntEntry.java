/*
 * copyright 2016-2018 ueyudiud
 */
package nebula.base;

/**
 * @author ueyudiud
 */
public interface IntEntry<E>
{
	E getKey();
	
	int getValue();
	
	int setValue(int i);
}
