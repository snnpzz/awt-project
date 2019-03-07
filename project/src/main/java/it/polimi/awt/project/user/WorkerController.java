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
public class WorkerController {

	private CampaignService campaignService;
	private WorkerRepository workerRepository;
	private WorkerService workerService;

	@Autowired
	public WorkerController(CampaignService campaignService, WorkerRepository workerRepository,
			WorkerService workerService) {
		this.campaignService = campaignService;
		this.workerRepository = workerRepository;
		this.workerService = workerService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/workers/{workerName}")
	public String get(Model model, @PathVariable String workerName) {
		Worker worker = workerRepository.findById(workerName).orElseThrow(() -> new UserNotFoundException(workerName));
		model.addAttribute("worker", worker);
		return "/worker/worker";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/worker/")
	public String index(Model model) {
		String name = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getName();
		List<Campaign> oldCampaigns = campaignService.selectStartedCampaigns(true, name);
		model.addAttribute("oldCampaigns", oldCampaigns);
		List<Campaign> newCampaigns = campaignService.selectStartedCampaigns(false, name);
		model.addAttribute("newCampaigns", newCampaigns);
		return "/worker/index";
	}

	@RequestMapping(method = RequestMethod.PATCH, value = "/workers/{workerName}/change-password")
	public String updatePassword(@PathVariable String workerName,
			@RequestParam(required = true, value = "old-password") String oldPassword,
			@RequestParam(required = true, value = "new-password") String newPassword,
			@RequestParam(required = true, value = "confirm-new-password") String confirmNewPassword) throws Exception {
		workerService.updatePassword(oldPassword, newPassword, confirmNewPassword);
		return "redirect:/workers/" + workerName;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/workers/{workerName}")
	public String put(@PathVariable String workerName, @RequestParam(required = true) String name,
			@RequestParam(required = true) String email) throws Exception {
		workerService.updateEmail(email);
		try {
			workerService.updateName(name);
			workerName = name;
		} catch (Exception e) {
			throw e;
		}
		return "redirect:/workers/" + workerName;
	}

}
