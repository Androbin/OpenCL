package de.androbin.opencl;

import java.nio.*;
import java.util.function.*;

public final class CLAddTest implements CLTest
{
	@ Override
	public String getName()
	{
		return "cladd";
	}

	@ Override
	public IntPredicate getPredicate( final int[] dataA, final int[] dataB, final IntBuffer dataC )
	{
		return i -> dataA[ i ] + dataB[ i ] == dataC.get( i );
	}
}