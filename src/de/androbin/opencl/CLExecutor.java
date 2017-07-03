package de.androbin.opencl;

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
    this( name, name + ".cls" );
  }
  
  public CLExecutor( final String name, final String path ) throws IOException {
    this( name, path, "" );
  }
  
  public CLExecutor( final String name, final String path, final CharSequence options )
      throws IOException {
    final String source = read( "cls/" + path );
    
    if ( source == null ) {
      throw new IOException( "File 'cls/" + path + "' could not be read" );
    }
    
    final IntBuffer error = createIntBuffer( 1 );
    program = clCreateProgramWithSource( context, source, error );
    checkCLError( clBuildProgram( program, device, options, null ) );
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
    checkCLError( clReleaseCommandQueue( queue ) );
    checkCLError( clReleaseContext( context ) );
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
  }
  
  public static void releaseGL( final CLMem mem ) {
    checkCLError( clEnqueueReleaseGLObjects( queue, mem, null, null ) );
  }
}