package web.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;


public class StringResourceLoader extends ResourceLoader {

	public long getLastModified(Resource resource) {
		return 0;
	}

	public InputStream getResourceStream(String source)
			throws ResourceNotFoundException {
		InputStream result = null;
		if(source == null) throw new ResourceNotFoundException ("No template content provided");
		try {
			result = new ByteArrayInputStream(source.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void init(ExtendedProperties configuration) {
	}

	public boolean isSourceModified(Resource resource) {
		return false;
	}

}
