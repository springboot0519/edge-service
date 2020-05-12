package com.springcloud.filters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class SimplePostFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		InputStream responseStream = ctx.getResponseDataStream();
		
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream(); //will use this to log it
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream(); //will use this to set it back on the response

		int numBytes = 0;
	    byte[] bufferArr = new byte[1024];
		try {
		    while ((numBytes = responseStream.read(bufferArr)) != -1) {
		        baos1.write(bufferArr, 0, numBytes);
		        baos2.write(bufferArr, 0, numBytes);
		    }

		    System.out.println("Zuul: response is  " + baos1.toString());
		    
			ctx.setResponseDataStream(new ByteArrayInputStream(baos2.toByteArray())); //set it back as the response

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(baos1 != null) {
				try {
					baos1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(baos2 != null) {
				try {
					baos2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 2;
	}

}
