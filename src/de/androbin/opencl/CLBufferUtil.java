package de.androbin.opencl;

import static de.androbin.opencl.CLExecutor.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.*;
import static org.lwjgl.opencl.Util.*;
import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.opencl.*;

public final class CLBufferUtil
{
	private CLBufferUtil()
	{
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
	
	public static CLMem createFromGLBuffer( final int bufferId, final long flags )
	{
		return clCreateFromGLBuffer( context, flags, bufferId, null );
	}
	
	public static CLMem createFromGLReadAndWriteBuffer( final int bufferId )
	{
		return createFromGLBuffer( bufferId, CL_MEM_READ_WRITE );
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
}