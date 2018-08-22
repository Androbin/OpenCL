package de.androbin.opencl;

import de.androbin.lwjgl.util.BufferUtil;
import static de.androbin.io.util.FileReaderUtil.*;
import static org.lwjgl.BufferUtils.*;
import static org.lwjgl.opencl.CL.*;
import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.opencl.CL10GL.*;
import static org.lwjgl.opencl.CLPlatform.*;
import static org.lwjgl.opencl.Util.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.*;
import org.lwjgl.opencl.*;
import org.lwjgl.opengl.*;

public final class CLExecutor {
  public static CLContext context;
  public static CLDevice device;
  public static CLCommandQueue queue;
  
  public final CLProgram program;
  public final CLKernel kernel;
  
  public CLExecutor( final String name ) throws IOException {
    this( name, "" );
  }
  
  public CLExecutor( final String name, final CharSequence options ) throws IOException {
    this( name, "cls/" + name + ".cls", options );
  }
  
  public CLExecutor( final String name, final String path, final CharSequence options )
      throws IOException {
    final String source = read( path );
    
    if ( source == null ) {
      throw new IOException( "File '" + path + "' could not be read" );
    }
    
    final IntBuffer error = createIntBuffer( 1 );
    program = clCreateProgramWithSource( context, source, error );
    checkCLError( error.get( 0 ) );
    
    final int buildError = clBuildProgram( program, device, options, null );
    
    final PointerBuffer logSize = createPointerBuffer( 1 );
    clGetProgramBuildInfo( program, device, CL10.CL_PROGRAM_BUILD_LOG, null, logSize );
    
    final ByteBuffer log = createByteBuffer( (int) logSize.get() );
    clGetProgramBuildInfo( program, device, CL10.CL_PROGRAM_BUILD_LOG, log, null );
    
    final byte[] logRaw = new byte[ log.capacity() ];
    BufferUtil.getBuffer( log, logRaw );
    
    final String logString = new String( logRaw ).trim();
    
    if ( !logString.isEmpty() ) {
      System.err.println( logString );
    }
    
    checkCLError( buildError );
    
    kernel = clCreateKernel( program, name, error );
    checkCLError( error.get( 0 ) );
  }
  
  public static void acquireGL( final CLMem mem ) {
    checkCLError( clEnqueueAcquireGLObjects( queue, mem, null, null ) );
  }
  
  public void cleanup() {
    checkCLError( clReleaseKernel( kernel ) );
    checkCLError( clReleaseProgram( program ) );
  }
  
  public static void destroyCL() {
    if ( queue != null ) {
      checkCLError( clReleaseCommandQueue( queue ) );
      queue = null;
    }
    
    device = null;
    
    if ( context != null ) {
      checkCLError( clReleaseContext( context ) );
      context = null;
    }
    
    destroy();
  }
  
  public void execute( final int ... dim ) {
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkSize.put( i, dim[ i ] );
    }
    
    checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
        null, globalWorkSize, null, null, null ) );
  }
  
  public void executeBorder( final int ... dim ) {
    final PointerBuffer globalWorkOffset = createPointerBuffer( dim.length );
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkOffset.put( i, 1 );
      globalWorkSize.put( i, dim[ i ] - 2 );
    }
    
    checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
        globalWorkOffset, globalWorkSize, null, null, null ) );
  }
  
  public void executeN( final int n, final int ... dim ) {
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkSize.put( i, dim[ i ] );
    }
    
    for ( int i = 0; i < n; i++ ) {
      checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
          null, globalWorkSize, null, null, null ) );
    }
  }
  
  public void executeBorderN( final int n, final int ... dim ) {
    final PointerBuffer globalWorkOffset = createPointerBuffer( dim.length );
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkOffset.put( i, 1 );
      globalWorkSize.put( i, dim[ i ] - 2 );
    }
    
    for ( int i = 0; i < n; i++ ) {
      checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
          globalWorkOffset, globalWorkSize, null, null, null ) );
    }
  }
  
  public void executeI( final int n, final int index, final int ... dim ) {
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkSize.put( i, dim[ i ] );
    }
    
    for ( int i = 0; i < n; i++ ) {
      kernel.setArg( index, i );
      checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
          null, globalWorkSize, null, null, null ) );
    }
  }
  
  public void executeBorderI( final int n, final int index, final int ... dim ) {
    final PointerBuffer globalWorkOffset = createPointerBuffer( dim.length );
    final PointerBuffer globalWorkSize = createPointerBuffer( dim.length );
    
    for ( int i = 0; i < dim.length; i++ ) {
      globalWorkOffset.put( i, 1 );
      globalWorkSize.put( i, dim[ i ] - 2 );
    }
    
    for ( int i = 0; i < n; i++ ) {
      kernel.setArg( index, i );
      checkCLError( clEnqueueNDRangeKernel( queue, kernel, dim.length,
          globalWorkOffset, globalWorkSize, null, null, null ) );
    }
  }
  
  public static void finish() {
    checkCLError( clFinish( queue ) );
  }
  
  public static void flush() {
    checkCLError( clFlush( queue ) );
  }
  
  public static void initCL( final int platformId, final int deviceId, final Drawable drawable )
      throws LWJGLException {
    create();
    
    final CLPlatform platform = getPlatforms().get( platformId );
    final List<CLDevice> devices = platform.getDevices( CL_DEVICE_TYPE_GPU );
    
    final IntBuffer error = createIntBuffer( 1 );
    context = CLContext.create( platform, devices, null, drawable, error );
    device = devices.get( deviceId );
    queue = clCreateCommandQueue( context, device, 0, error );
    checkCLError( error.get( 0 ) );
    
    Runtime.getRuntime().addShutdownHook( new Thread( CLExecutor::destroyCL ) );
  }
  
  public static void releaseGL( final CLMem mem ) {
    checkCLError( clEnqueueReleaseGLObjects( queue, mem, null, null ) );
  }
}