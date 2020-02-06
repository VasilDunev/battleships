package bg.sofia.uni.fmi.mjt.battleships.server;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderBoard {

	// class for statistics

	private static Map<String, Integer> winners = new HashMap<>();
	private static Map<String, Integer> losers = new HashMap<>();

	static void addWinner(String name){
		if (!winners.containsKey(name)){
			winners.put(name, 1);
		}
		else{
			winners.put(name, winners.get(name) + 1);
		}
	}

	static void addLoser(String name){
		if (!losers.containsKey(name)){
			losers.put(name, 1);
		}
		else{
			losers.put(name, losers.get(name) + 1);
		}
	}

	static String getTop5(String winnerOrLoser){
		List<String> top5;
		if ("winners".equals(winnerOrLoser)){
			top5 = top5(winners);
		}
		else{
			top5 = top5(losers);
		}
		StringBuilder sb = new StringBuilder();

		for(String str: top5){
			sb.append(str).append("\n");
		}

		if (sb.toString().isEmpty()){
			return " ";
		}

		return sb.toString();
	}

	private static List<String> top5( Map<String, Integer> map){
		return map.entrySet()
				       .stream()
				       .sorted((entryOne, entryTwo) -> entryTwo.getValue().compareTo(entryOne.getValue()))
				       .map(Map.Entry::getKey)
				       .limit(5).collect(Collectors.toList());
	}

}
