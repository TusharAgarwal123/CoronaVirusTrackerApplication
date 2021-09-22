package com.corona.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.corona.demo.model.LocationStats;
import com.corona.demo.service.CoronaVirusDataService;

@Controller
public class HomeController {

	@Autowired
	private CoronaVirusDataService cs;

	@GetMapping("/")
	public String home(Model m) {
		List<LocationStats> allStats = cs.getAllStats();
		int totalCase = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCase = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		m.addAttribute("list", cs.getAllStats());
		m.addAttribute("totalRepotedCases", totalCase);
		m.addAttribute("totalNewCases", totalNewCase);
		return "home";
	}

}
