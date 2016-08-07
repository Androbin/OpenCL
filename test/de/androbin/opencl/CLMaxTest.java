package de.androbin.opencl;

import java.nio.*;
import java.util.function.*;

public final class CLMaxTest implements CLTest
{
	@ Override
	public String getName()
	{
		return "clmax";
	}
	
	@ Override
	public IntPredicate getPredicate( final int[] dataA, final int[] dataB, final IntBuffer dataC )
	{
		return i -> Math.max( dataA[ i ], dataB[ i ] ) == dataC.get( i );
	}
}