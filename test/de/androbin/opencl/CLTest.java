package de.androbin.opencl;

import java.nio.*;
import java.util.function.*;

public interface CLTest {
  String getName();
  
  IntPredicate getPredicate( final int[] dataA, final int[] dataB, final IntBuffer dataC );
}