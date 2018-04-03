package com.alexfoglia.server;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		int n = content.getNumber();
		System.out.println("-------------------------"+n);
		int[][] matrix=content.parseMatrix();
		System.out.println("-------------------------"+content.getContent());
		service.storeInDb(n,matrix);
		return "redirect:/viewdb";
	}

	/*@GetMapping("/remtable")
    public String remtableForm(Model model) {
        model.addAttribute(USERCONTENT, new UserContent());
        return "tablerem";
    }

	@PostMapping("/remtable")
	public String remtable(@ModelAttribute UserContent content) {
	service.deleteOneById(content.getContent());
	return "redirect:/";
	}*/

	@RequestMapping(value = "/remtable", method = RequestMethod.GET)
	public String handleDeleteUser(@RequestParam(name="id")String id) {
		service.deleteOneById(id);
		return "redirect:/viewdb";
	}

}
