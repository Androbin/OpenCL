package de.androbin.opencl;

import static de.androbin.io.util.FileReaderUtil.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.*;
import static org.lwjgl.opencl.CLContext.create;
import static org.lwjgl.opencl.CLPlatform.*;
import java.io.*;
import java.net.*;
import java.util.*;
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
		
		program = clCreateProgramWithSource( context, read( res ), null );
		clBuildProgram( program, device, "", null );
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
	
	public void execute( final int ... dim )
	{
		final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
		
		for ( int i = 0; i < dim.length; i++ )
		{
			globalWorkSize.put( i, dim[ i ] );
		}
		
		clEnqueueNDRangeKernel( queue, kernel, dim.length, null, globalWorkSize, null, null, null );
	}
	
	public void executeN( final int n, final int ... dim )
	{
		final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
		
		for ( int i = 0; i < dim.length; i++ )
		{
			globalWorkSize.put( i, dim[ i ] );
		}
		
		for ( int i = 0; i < n; i++ )
		{
			clEnqueueNDRangeKernel( queue, kernel, dim.length, null, globalWorkSize, null, null, null );
		}
	}
	
	public void executeI( final int n, final int index, final int ... dim )
	{
		final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
		
		for ( int i = 0; i < dim.length; i++ )
		{
			globalWorkSize.put( i, dim[ i ] );
		}
		
		for ( int i = 0; i < n; i++ )
		{
			kernel.setArg( index, i );
			clEnqueueNDRangeKernel( queue, kernel, dim.length, null, globalWorkSize, null, null, null );
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
		queue = clCreateCommandQueue( context, device, 0, null );
	}
	
	public static void release( final CLMem mem )
	{
		clEnqueueReleaseGLObjects( queue, mem, null, null );
	}
}
