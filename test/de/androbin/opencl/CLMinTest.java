package de.androbin.opencl;

import java.nio.*;
import java.util.function.*;

public final class CLMinTest implements CLTest {
  @ Override
  public String getName() {
    return "clmin";
  }
  
  @ Override
  public IntPredicate getPredicate( final int[] dataA, final int[] dataB, final IntBuffer dataC ) {
    return i -> Math.min( dataA[ i ], dataB[ i ] ) == dataC.get( i );
  }
}