package LeetCode100;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Solution {
    class Node {
        String str;
        String sortedStr;

        Node(String str, String sortedStr) {
            this.str = str;
            this.sortedStr = sortedStr;
        }
    }

    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> res = new ArrayList<List<String>>();
        List<Node> nodes = new ArrayList<Node>();
        for (String str : strs) {
            char[] s = str.toCharArray();
            Arrays.sort(s);
            String sortedStr = new String(s);
            nodes.add(new Node(str, sortedStr));
        }
        Map<String, List<String>> map = new HashMap<>();
        for (Node node : nodes) {
            if (map.containsKey(node.sortedStr)) {
                map.get(node.sortedStr).add(node.str);
            } else {
                List<String> list = new ArrayList<>();
                list.add(node.str);
                map.put(node.sortedStr, list);
            }
        }
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            res.add(entry.getValue());
        }

        return res;
    }

    public static void main(String[] args) {
        String a[] = {"eat", "tea", "tan", "ate", "nat", "bat"};
        Solution s = new Solution();
        System.out.println(s.groupAnagrams(a));
    }
}