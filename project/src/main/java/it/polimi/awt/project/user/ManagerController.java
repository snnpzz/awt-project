/**
 * awt-project
 * Copyright (C) 2019  Susanna Pozzoli
 *
 * This file is part of awt-project.
 *
 * awt-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * awt-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with awt-project.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.polimi.awt.project.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.campaign.CampaignService;
import it.polimi.awt.project.config.CustomUserDetails;

@Controller
public class ManagerController {

	private CampaignService campaignService;
	private ManagerRepository managerRepository;
	private ManagerService managerService;

	@Autowired
	public ManagerController(CampaignService campaignService, ManagerRepository managerRepository,
			ManagerService managerService) {
		this.campaignService = campaignService;
		this.managerRepository = managerRepository;
		this.managerService = managerService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/managers/{managerName}")
	public String get(Model model, @PathVariable String managerName) {
		Manager manager = managerRepository.findById(managerName)
				.orElseThrow(() -> new UserNotFoundException(managerName));
		model.addAttribute("manager", manager);
		return "/manager/manager";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/")
	public String index(Model model) {
		String name = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getName();
		List<Campaign> campaigns = campaignService.getCampaigns(name);
		model.addAttribute("campaigns", campaigns);
		return "/manager/index";
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/managers/{managerName}/change-password")
	public String patch(@PathVariable String managerName,
			@RequestParam(required = true, value = "old-password") String oldPassword,
			@RequestParam(required = true, value = "new-password") String newPassword,
			@RequestParam(required = true, value = "confirm-new-password") String confirmNewPassword) throws Exception {
		managerService.updatePassword(oldPassword, newPassword, confirmNewPassword);
		return "redirect:/managers/" + managerName;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/managers/{managerName}")
	public String put(@PathVariable String managerName, @RequestParam(required = true) String name,
			@RequestParam(required = true) String email) throws Exception {
		managerService.updateEmail(email);
		try {
			managerService.updateName(name);
			managerName = name;
		} catch (Exception e) {
			throw e;
		}
		return "redirect:/managers/" + managerName;
	}

}
