kernel void clmax( global const int* a, global const int* b, global int* result, const unsigned int size )
{
  const int id = get_global_id( 0 );
  
  if ( id < size )
  {
    result[ id ] = max( a[ id ], b[ id ] );
  }
}