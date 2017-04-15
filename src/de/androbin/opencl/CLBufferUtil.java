package de.androbin.opencl;

import static org.lwjgl.opencl.Util.*;
import static org.lwjgl.BufferUtils.*;
import static de.androbin.opencl.CLExecutor.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.*;
import java.nio.*;
import org.lwjgl.opencl.*;

public final class CLBufferUtil {
  private CLBufferUtil() {
  }
  
  public static CLMem createBuffer( final long flags, final Buffer buffer ) {
    final CLMem mem;
    final IntBuffer error = createIntBuffer( 1 );
    
    if ( buffer instanceof ByteBuffer ) {
      mem = clCreateBuffer( context, flags, (ByteBuffer) buffer, error );
    } else if ( buffer instanceof DoubleBuffer ) {
      mem = clCreateBuffer( context, flags, (DoubleBuffer) buffer, error );
    } else if ( buffer instanceof FloatBuffer ) {
      mem = clCreateBuffer( context, flags, (FloatBuffer) buffer, error );
    } else if ( buffer instanceof IntBuffer ) {
      mem = clCreateBuffer( context, flags, (IntBuffer) buffer, error );
    } else if ( buffer instanceof LongBuffer ) {
      mem = clCreateBuffer( context, flags, (LongBuffer) buffer, error );
    } else if ( buffer instanceof ShortBuffer ) {
      mem = clCreateBuffer( context, flags, (ShortBuffer) buffer, error );
    } else {
      throw new InternalError();
    }
    
    checkCLError( error.get( 0 ) );
    return mem;
  }
  
  public static CLMem createBuffer( final long flags, final int size, final int bytes ) {
    final IntBuffer error = createIntBuffer( 1 );
    final CLMem mem = clCreateBuffer( context, flags, size * bytes, error );
    checkCLError( error.get( 0 ) );
    return mem;
  }
  
  public static CLMem createFromGLBuffer( final int buffer, final long flags ) {
    final IntBuffer error = createIntBuffer( 1 );
    final CLMem mem = clCreateFromGLBuffer( context, flags, buffer, error );
    checkCLError( error.get( 0 ) );
    return mem;
  }
  
  public static CLMem createFromGLReadOnlyBuffer( final int buffer ) {
    return createFromGLBuffer( buffer, CL_MEM_READ_ONLY );
  }
  
  public static CLMem createFromGLWriteOnlyBuffer( final int buffer ) {
    return createFromGLBuffer( buffer, CL_MEM_WRITE_ONLY );
  }
  
  public static CLMem createFromGLReadAndWriteBuffer( final int buffer ) {
    return createFromGLBuffer( buffer, CL_MEM_READ_WRITE );
  }
  
  public static CLMem createReadAndWriteBuffer( final Buffer buffer ) {
    return createBuffer( CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, buffer );
  }
  
  public static CLMem createReadAndWriteBuffer( final int size, final int bytes ) {
    return createBuffer( CL_MEM_READ_WRITE, size, bytes );
  }
  
  public static CLMem createReadOnlyBuffer( final Buffer buffer ) {
    return createBuffer( CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, buffer );
  }
  
  public static CLMem createReadOnlyBuffer( final int size, final int bytes ) {
    return createBuffer( CL_MEM_READ_ONLY, size, bytes );
  }
  
  public static CLMem createWriteOnlyBuffer( final Buffer buffer ) {
    return createBuffer( CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, buffer );
  }
  
  public static CLMem createWriteOnlyBuffer( final int size, final int bytes ) {
    return createBuffer( CL_MEM_WRITE_ONLY, size, bytes );
  }
  
  public static void copyBuffer( final CLMem src, final CLMem dst, final int size ) {
    checkCLError( clEnqueueCopyBuffer( queue, src, dst, 0, 0, size, null, null ) );
  }
  
  public static ByteBuffer mapBuffer( final CLMem memory, final int size ) {
    final IntBuffer error = createIntBuffer( 1 );
    final ByteBuffer buffer = clEnqueueMapBuffer( queue, memory, CL_FALSE, 0, 0, size, null, null, error );
    checkCLError( error.get( 0 ) );
    return buffer;
  }
  
  public static void readBuffer( final CLMem memory, final Buffer buffer ) {
    final int error;
    
    if ( buffer instanceof ByteBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (ByteBuffer) buffer, null, null );
    } else if ( buffer instanceof DoubleBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (DoubleBuffer) buffer, null, null );
    } else if ( buffer instanceof FloatBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (FloatBuffer) buffer, null, null );
    } else if ( buffer instanceof IntBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (IntBuffer) buffer, null, null );
    } else if ( buffer instanceof LongBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (LongBuffer) buffer, null, null );
    } else if ( buffer instanceof ShortBuffer ) {
      error = clEnqueueReadBuffer( queue, memory, CL_FALSE, 0, (ShortBuffer) buffer, null, null );
    } else {
      throw new InternalError();
    }
    
    checkCLError( error );
  }
  
  public static void writeBuffer( final CLMem memory, final Buffer buffer ) {
    final int error;
    
    if ( buffer instanceof ByteBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (ByteBuffer) buffer, null, null );
    } else if ( buffer instanceof DoubleBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (DoubleBuffer) buffer, null, null );
    } else if ( buffer instanceof FloatBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (FloatBuffer) buffer, null, null );
    } else if ( buffer instanceof IntBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (IntBuffer) buffer, null, null );
    } else if ( buffer instanceof LongBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (LongBuffer) buffer, null, null );
    } else if ( buffer instanceof ShortBuffer ) {
      error = clEnqueueWriteBuffer( queue, memory, CL_FALSE, 0, (ShortBuffer) buffer, null, null );
    } else {
      throw new InternalError();
    }
    
    checkCLError( error );
  }
}