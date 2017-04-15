package de.androbin.opencl;

import static de.androbin.lwjgl.util.BufferUtil.*;
import static de.androbin.opencl.CLBufferUtil.*;
import static de.androbin.opencl.CLExecutor.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import org.lwjgl.*;
import org.lwjgl.opencl.*;

public final class Test {
  private Test() {
  }
  
  public static void main( final String[] args ) {
    try {
      initCL( 0, 0, null );
    } catch ( final LWJGLException e ) {
      e.printStackTrace();
      return;
    }
    
    runTest( new CLAddTest(), 1000000 );
    runTest( new CLMinTest(), 1000000 );
    runTest( new CLMaxTest(), 1000000 );
    runTest( new CLSubtractTest(), 1000000 );
    
    destroyCL();
  }
  
  private static int[] generateData( final int size ) {
    final int[] data = new int[ size ];
    Arrays.parallelSetAll( data, i -> ThreadLocalRandom.current().nextInt() );
    return data;
  }
  
  private static void runTest( final CLTest test, final int size ) {
    final CLExecutor executor;
    
    try {
      executor = new CLExecutor( test.getName() );
    } catch ( final IOException | OpenCLException e ) {
      System.err.println( e.getClass().getSimpleName() + " in Test '" + test.getName() + "': "
          + e.getLocalizedMessage() );
      return;
    }
    
    final int[] dataA = generateData( size );
    final int[] dataB = generateData( size );
    
    final CLMem memA = createWriteOnlyBuffer( wrapIntBuffer( dataA ) );
    final CLMem memB = createWriteOnlyBuffer( wrapIntBuffer( dataB ) );
    
    final CLMem memResult = createReadOnlyBuffer( size, Integer.BYTES );
    
    final CLKernel kernel = executor.kernel;
    kernel.setArg( 0, memA );
    kernel.setArg( 1, memB );
    kernel.setArg( 2, memResult );
    kernel.setArg( 3, size );
    
    executor.execute( size );
    
    final IntBuffer resultBuff = BufferUtils.createIntBuffer( size );
    readBuffer( memResult, resultBuff );
    finish();
    
    if ( test( test.getPredicate( dataA, dataB, resultBuff ), size ) ) {
      System.out.println( "Test '" + test.getName() + "' passed with " + size + " elements" );
    } else {
      System.out.println( "Test '" + test.getName() + "' failed with " + size + " elements" );
    }
    
    executor.cleanup();
  }
  
  private static boolean test( final IntPredicate predicate, final int size ) {
    return IntStream.range( 0, size ).parallel().allMatch( predicate );
  }
}