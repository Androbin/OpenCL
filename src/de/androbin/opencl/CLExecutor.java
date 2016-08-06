package de.androbin.opencl;

import static de.androbin.io.util.FileUtil.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CLContext.create;
import static org.lwjgl.opencl.CLPlatform.*;
import static org.lwjgl.opencl.Util.*;
import com.sun.xml.internal.ws.api.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.opencl.*;

public final class CLExecutor
{
	public static CLContext			context;
	public static CLDevice			device;
	public static CLCommandQueue	queue;
	
	public final CLProgram			program;
	public final CLKernel			kernel;
	
	public CLExecutor( final String name ) throws IOException
	{
		this( name, name + ".cls" );
	}
	
	public CLExecutor( final String name, final String path ) throws IOException
	{
		final InputStream input = ResourceLoader.class.getResourceAsStream( "/cls/" + path );
		
		if ( input == null )
		{
			throw new FileNotFoundException( "File '/cls/" + path + "' cannot be found" );
		}
		
		final IntBuffer error = createIntBuffer( 1 );
		program = clCreateProgramWithSource( context, read( input ), error );
		input.close();
		
		checkCLError( error.get( 0 ) );
		checkCLError( clBuildProgram( program, device, "", null ) );
		
		kernel = clCreateKernel( program, name, null );
	}
	
	public void cleanup()
	{
		clReleaseKernel( kernel );
		clReleaseProgram( program );
	}
	
	public static void destroyCL()
	{
		clReleaseCommandQueue( queue );
		clReleaseContext( context );
		destroy();
	}
	
	public void execute( final int size )
	{
		execute( size, 1 );
	}
	
	public void execute( final int size, final int n )
	{
		final PointerBuffer globalWorkSize = createPointerBuffer( 1 );
		globalWorkSize.put( 0, size );
		
		for ( int i = 0; i < n; i++ )
		{
			clEnqueueNDRangeKernel( queue, kernel, 1, null, globalWorkSize, null, null, null );
		}
	}
	
	public static void finish()
	{
		clFinish( queue );
	}
	
	public static void flush()
	{
		clFlush( queue );
	}
	
	public static void initCL() throws LWJGLException
	{
		initCL( 0 );
	}
	
	public static void initCL( final int platformId ) throws LWJGLException
	{
		initCL( platformId, 0 );
	}
	
	public static void initCL( final int platformId, final int deviceId ) throws LWJGLException
	{
		create();
		
		final CLPlatform platform = getPlatforms().get( platformId );
		final List<CLDevice> devices = platform.getDevices( CL_DEVICE_TYPE_GPU );
		
		context = create( platform, devices, null );
		device = devices.get( deviceId );
		
		final IntBuffer error = createIntBuffer( 1 );
		queue = clCreateCommandQueue( context, devices.get( deviceId ), 0, error );
		checkCLError( error.get( 0 ) );
	}
}
