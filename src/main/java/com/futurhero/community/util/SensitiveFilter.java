package com.futurhero.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger log = LoggerFactory.getLogger(SensitiveFilter.class);
    private TreeNode root = new TreeNode();

    @PostConstruct  // construct -> @AutoWired -> @PostConstruct
    public void init(){
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加到前缀树
                add(keyword);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败: " + e.getMessage());
        }

    }

    private void add(String word) {
        TreeNode temp = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!temp.children.containsKey(c)) {
                TreeNode node = new TreeNode();
                temp.children.put(c, node);
                temp = node;
            } else {
                temp = temp.children.get(c);
            }

            if (i == word.length() - 1) {
                temp.isEnd = true;
            }
        }
    }

    public String filter(String content) {
        StringBuilder sb = new StringBuilder();
        TreeNode temp = root;
        int t1 = 0;
        int t2 = 0;

        while (t2 < content.length()) {
            char c = content.charAt(t2);
            if (!temp.children.containsKey(c)) {
                sb.append(content.substring(t1, t2 + 1));
                temp = root;
                t2++;
                t1 = t2;
            } else {
                if (temp.children.get(c).isEnd) {
                    sb.append("***");
                    temp = root;
                    t2++;
                    t1 = t2;
                } else {
                    t2++;
                    temp = temp.children.get(c);
                }
            }
        }
        if (t1 != t2) {
            sb.append(content.substring(t1, t2 + 1));
        }

        return sb.toString();
    }


    class TreeNode {
        public Map<Character, TreeNode> children = new HashMap<>();
        public boolean isEnd;
    }
}
