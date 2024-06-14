package LeetCode100;

import java.util.Arrays;

public class Solution {
    public static void main(String[] args) {
        int[] a = {9,1,4,7,3,-1,0,5,8,-1,6};
        Solution s = new Solution();
        System.out.print(s.longestConsecutive(a));
    }

    public int longestConsecutive(int[] nums) {
        if (nums.length == 0)
            return 0;
        int l = 1;
        Arrays.sort(nums);
        for (int j = 0; j < nums.length; j++) {
            if (j + 1 < nums.length) {
                if(nums[j + 1] - nums[j] == 1) {
                    System.out.println(nums[j] + "-" + nums[j+1]);
                    l++;
                }
            }
        }

        return l;
    }

}