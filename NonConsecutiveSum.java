// Carlos Ricoveri

import java.util.*;
import java.lang.*;

public class NonConsecutiveSum
{
  // This method determines the max sum you can get from a sequence of
  // numbers using dynamic programming in O(n) time.
  public static int maxSum(int [] blocks)
  {
    int length = blocks.length;
    int [] dpArr = new int[length];

    // Before we initialize initial conditions in our dp array, we have to do
    // a couple of out of bound checks.
    if (length == 0)
      return 0;
    else if (length == 1)
      return blocks[0];
    else if (length == 2)
      return Math.max(blocks[0], blocks[1]);
    else if (length == 3)
      return Math.max(blocks[0] + blocks[2], blocks[1]);

    dpArr[0] = blocks[0];
    dpArr[1] = blocks[1];
    dpArr[2] = blocks[0] + blocks[2];

    // After the third element in the dp array, every subsequent cell is filled
    // depending on the previous 2 and 3 cells--whichever yields a greater sum.
    for (int i = 3; i < length; i++)
      dpArr[i] = Math.max(blocks[i] + dpArr[i - 2], blocks[i] + dpArr[i - 3]);

    // The solution will either be in the second to last cell or the last cell.
    return Math.max(dpArr[length - 1], dpArr[length - 2]);
  }
}
