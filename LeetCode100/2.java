package LeetCode100;
public class Solution {
    public static void main(String[] args) {
        int[] a = {2, 7, 11, 15};
        Solution s = new Solution();
        System.out.println(s.twoSum(a, 9));
    }
    public int[] twoSum(int[] nums, int target) {
        for(int i = 0; i < nums.length; i++) {
            for(int j = i+1; j < nums.length; j++) {
                if(nums[i] + nums[j] == target) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[] {};
    }

}
