package com.alexfoglia.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AWebController {

	@Autowired
	private IGridService service;
	private static final String USERCONTENT="usercontent";

	@RequestMapping("/")
	public String welcome(Model model) {
		return "database";
	}

	@RequestMapping("/viewdb")
	public String viewdb(Model model) {
		List<DatabaseGrid> allGrids = service.findAllGridsInDb();
		model.addAttribute("allGrids", allGrids);
		return "dbview";
	}

	@GetMapping("/addtable")
	public String addtableForm(Model model) {
		model.addAttribute(USERCONTENT, new UserContent());
		return "tableadd";
	}

	@PostMapping("/addtable") 
	public String addtable(@ModelAttribute UserContent content, Model mod) {
		service.storeInDb(content.getNumber(),content.parseMatrix());
		return "redirect:/viewdb";
	}

	@RequestMapping(value = "/remtable", method = RequestMethod.GET)
	public String handleDeleteUser(@RequestParam(name="id")String id) {
		service.deleteOneById(id);
		return "redirect:/viewdb";
	}

}
