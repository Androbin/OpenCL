package de.androbin.opencl;

import static de.androbin.util.FileUtil.*;
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
	private static CLContext		context;
	private static CLDevice			device;
	private static CLCommandQueue	queue;
	
	public final CLProgram			program;
	public final CLKernel			kernel;
	
	public CLExecutor( final String name ) throws IOException
	{
		this( name, name + ".cls" );
	}
	
	public CLExecutor( final String name, final String path ) throws IOException
	{
		final IntBuffer error = createIntBuffer( 1 );
		final InputStream input = ResourceLoader.class.getResourceAsStream( "/cls/" + path );
		
		if ( input == null )
		{
			throw new FileNotFoundException( "File '/cls/" + path + "' cannot be found" );
		}
		
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
	
	public static CLMem createBuffer( final Buffer buffer, final long flags )
	{
		final IntBuffer error = createIntBuffer( 1 );
		final CLMem mem;
		
		/**/ if ( buffer instanceof ByteBuffer )
		{
			mem = clCreateBuffer( context, flags, (ByteBuffer) buffer, error );
		}
		else if ( buffer instanceof DoubleBuffer )
		{
			mem = clCreateBuffer( context, flags, (DoubleBuffer) buffer, error );
		}
		else if ( buffer instanceof FloatBuffer )
		{
			mem = clCreateBuffer( context, flags, (FloatBuffer) buffer, error );
		}
		else if ( buffer instanceof IntBuffer )
		{
			mem = clCreateBuffer( context, flags, (IntBuffer) buffer, error );
		}
		else if ( buffer instanceof LongBuffer )
		{
			mem = clCreateBuffer( context, flags, (LongBuffer) buffer, error );
		}
		else if ( buffer instanceof ShortBuffer )
		{
			mem = clCreateBuffer( context, flags, (ShortBuffer) buffer, error );
		}
		else
		{
			throw new InternalError();
		}
		
		checkCLError( error.get( 0 ) );
		return mem;
	}
	
	public static CLMem createReadAndWriteBuffer( final Buffer buffer )
	{
		return createBuffer( buffer, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR );
	}
	
	public static CLMem createReadOnlyBuffer( final int size, final int bytes )
	{
		final IntBuffer error = createIntBuffer( 1 );
		final CLMem mem = clCreateBuffer( context, CL_MEM_READ_ONLY, size * bytes, error );
		checkCLError( error.get( 0 ) );
		return mem;
	}
	
	public static CLMem createWriteOnlyBuffer( final Buffer buffer )
	{
		return createBuffer( buffer, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR );
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
	
	public static void enqueueCopyBuffer( final CLMem src, final CLMem dst, final int size )
	{
		clEnqueueCopyBuffer( queue, src, dst, 0, 0, size, null, null );
	}
	
	public static ByteBuffer enqueueMapBuffer( final CLMem memory, final PointerBuffer buffer )
	{
		return clEnqueueMapBuffer( queue, memory, CL_FALSE, 0, 0, buffer.limit(), null, null, null );
	}
	
	public static void enqueueReadBuffer( final CLMem memory, final Buffer buffer )
	{
		/**/ if ( buffer instanceof ByteBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (ByteBuffer) buffer, null, null );
		}
		else if ( buffer instanceof DoubleBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (DoubleBuffer) buffer, null, null );
		}
		else if ( buffer instanceof FloatBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (FloatBuffer) buffer, null, null );
		}
		else if ( buffer instanceof IntBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (IntBuffer) buffer, null, null );
		}
		else if ( buffer instanceof LongBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (LongBuffer) buffer, null, null );
		}
		else if ( buffer instanceof ShortBuffer )
		{
			clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (ShortBuffer) buffer, null, null );
		}
	}
	
	public static void enqueueWriteBuffer( final CLMem memory, final Buffer buffer )
	{
		/**/ if ( buffer instanceof ByteBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (ByteBuffer) buffer, null, null );
		}
		else if ( buffer instanceof DoubleBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (DoubleBuffer) buffer, null, null );
		}
		else if ( buffer instanceof FloatBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (FloatBuffer) buffer, null, null );
		}
		else if ( buffer instanceof IntBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (IntBuffer) buffer, null, null );
		}
		else if ( buffer instanceof LongBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (LongBuffer) buffer, null, null );
		}
		else if ( buffer instanceof ShortBuffer )
		{
			clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (ShortBuffer) buffer, null, null );
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
