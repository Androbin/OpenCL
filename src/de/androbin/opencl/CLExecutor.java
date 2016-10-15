package de.androbin.opencl;

import static de.androbin.io.util.FileReaderUtil.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL.create;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.*;
import static org.lwjgl.opencl.CLContext.create;
import static org.lwjgl.opencl.CLPlatform.*;
import static org.lwjgl.opencl.Util.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;
import org.lwjgl.*;
import org.lwjgl.opencl.*;
import org.lwjgl.opengl.*;

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
		final URL res = ClassLoader.getSystemResource( "cls/" + path );
		
		if ( res == null )
		{
			throw new FileNotFoundException( "File '/cls/" + path + "' cannot be found" );
		}
		
		final InputStream input = res.openStream();
		final IntBuffer error = createIntBuffer( 1 );
		program = clCreateProgramWithSource( context, read( input ), error );
		input.close();
		
		checkCLError( error.get( 0 ) );
		checkCLError( clBuildProgram( program, device, "", null ) );
		
		kernel = clCreateKernel( program, name, null );
	}
	
	public static void acquire( final CLMem mem )
	{
		clEnqueueAcquireGLObjects( queue, mem, null, null );
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
		final PointerBuffer globalWorkSize = createPointerBuffer( 1 );
		globalWorkSize.put( 0, size );
		
		clEnqueueNDRangeKernel( queue, kernel, 1, null, globalWorkSize, null, null, null );
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
	
	public void execute( final int size, final int n, final IntConsumer foo )
	{
		final PointerBuffer globalWorkSize = createPointerBuffer( 1 );
		globalWorkSize.put( 0, size );
		
		for ( int i = 0; i < n; i++ )
		{
			foo.accept( i );
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
	
	public static void initCL( final int platformId, final int deviceId, final Drawable drawable ) throws LWJGLException
	{
		create();
		
		final CLPlatform platform = getPlatforms().get( platformId );
		final List<CLDevice> devices = platform.getDevices( CL_DEVICE_TYPE_GPU );
		
		context = create( platform, devices, null, drawable, null );
		device = devices.get( deviceId );
		
		final IntBuffer error = createIntBuffer( 1 );
		queue = clCreateCommandQueue( context, devices.get( deviceId ), 0, error );
		checkCLError( error.get( 0 ) );
	}
	
	public static void release( final CLMem mem )
	{
		clEnqueueReleaseGLObjects( queue, mem, null, null );
	}
}