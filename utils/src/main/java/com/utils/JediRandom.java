package com.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机
 */
public class JediRandom {
    /**
     * 在[min,max)中随机一个整数, 包括min,不包括max
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max){
        return ThreadLocalRandom.current().nextInt(max-min)+ min;
    }
    /**
     * 在[min,max]中随机一个整数, 包括min和max
     * @param min
     * @param max
     * @return randInt
     */
    public static int randomInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(max-min + 1)+ min;
    }

    /**
     * 随机乱序一个列表
     * @param ranges
     * @return
     */
    public static <T> T[] shuffle_list(T[] ranges) {
		for (int i = ranges.length; i > 0; i--) {
			int p = random(0,i);
			T tmpObject = ranges[i-1];
			ranges[i-1] = ranges[p];
			ranges[p] = tmpObject;
		}
		return ranges;
	}
    /**
     * 得到一个相对平均的掉落队列
     * @param rate 万分比（正整数）
     * @param maxCount 单次计算的次数（即在多少次中计算概率）
     * @return 掉落队列，从1开始
     */
    public static JSONArray getRandSeq(int rate, int maxCount) {
    	if (rate <= 0) {
			rate = 10000;
		}
    	double expect =  (double)rate / 10000 * maxCount;
    	int minGet = (int)Math.max(Math.floor(expect), 1);
    	int maxGet = (int)Math.max(Math.ceil(expect), 1);
    	int doget = randomInt(minGet, maxGet);
    	int step = (int) Math.max(Math.floor(maxCount / doget), 1);
    	int begin = 1;
    	JSONArray retJsonArray = new JSONArray();
    	for (int i = 0; i < doget; i++,begin += step) {
			int bingo = randomInt(begin, begin + step);
			retJsonArray.add(bingo);
		}
    	return retJsonArray;
    }

    /**
     * 得到一个相对平均的掉落队列
     * @param randCount 随机数量（正整数）
     * @param maxCount 最大的数（即在多少次中计算概率）
     * @return 掉落队列，从1开始
     */
    public static List<Integer> getRandSeq2(int randCount,int maxCount) {
    	int step = (int) Math.max(Math.floor(maxCount / randCount), 1);
    	int begin = 1;
    	List<Integer> retJsonArray = new ArrayList<Integer>();
    	for (int i = 0; i < randCount && begin <= maxCount; i++,begin += step) {
    		int bingo = random(begin, begin + step);
    		retJsonArray.add(bingo);
    	}
    	return retJsonArray;
    }
}
