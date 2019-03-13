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

package it.polimi.awt.project.campaign;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import it.polimi.awt.project.annotation.Annotation;
import it.polimi.awt.project.annotation.AnnotationStatus;
import it.polimi.awt.project.config.CustomUserDetails;
import it.polimi.awt.project.load.Load;
import it.polimi.awt.project.peak.Peak;
import it.polimi.awt.project.user.Worker;

@Controller
public class CampaignController {

	private final CampaignRepository campaignRepository;
	private final CampaignService campaignService;

	@Autowired
	public CampaignController(CampaignRepository campaignRepository, CampaignService campaignService) {
		this.campaignRepository = campaignRepository;
		this.campaignService = campaignService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/campaigns")
	public String get(Model model) {
		Campaign campaign = new Campaign();
		model.addAttribute("campaign", campaign);
		return "manager/campaign";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/campaigns/{campaignId}")
	public String get(@PathVariable int campaignId, Model model) {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
		model.addAttribute("campaign", campaign);
		List<Load> loads = campaign.getPeaks();
		List<Conflict> conflicts = new ArrayList<Conflict>();
		List<Peak> yellowPeaks = new ArrayList<Peak>();
		List<Peak> orangePeaks = new ArrayList<Peak>();
		List<Peak> redPeaks = new ArrayList<Peak>();
		List<Peak> greenPeaks = new ArrayList<Peak>();
		for (Load load : loads) {
			Peak peak = load.getPeak();
			boolean toBeAnnotated = load.isToBeAnnotated();
			List<Annotation> annotations = peak.getAnnotations();
			annotations.removeIf(annotation -> annotation.getCampaign().getId() != campaign.getId());
			int notValid = 0;
			int valid = 0;
			for (Annotation annotation : annotations) {
				// if (annotation.getStatus().equals(AnnotationStatus.VALID)) {
					if (!(annotation.isPeakValidity())) {
						++notValid;
						continue;
					}
					if (annotation.isPeakValidity()) {
						++valid;
						continue;
					}
				// }
			}
			if ((notValid > 0) && (valid > 0)) {
				Conflict conflict = new Conflict.Builder().withCampaign(campaign).withPeak(peak).withNotValid(notValid)
						.withValid(valid).build();
				conflicts.add(conflict);
			}
			if (!(toBeAnnotated)) {
				greenPeaks.add(peak);
			}
			if (toBeAnnotated) {
				if (annotations.size() == 0) {
					yellowPeaks.add(peak);
					continue;
				}
				boolean orange = false;
				boolean red = false;
				for (Annotation annotation : annotations) {
					if (annotation.getStatus().equals(AnnotationStatus.REJECTED)) {
						red = true;
						redPeaks.add(peak);
						break;
					}
				}
				orange = !red;
				if (orange) {
					orangePeaks.add(peak);
				}
			}
		}
		model.addAttribute("yellowPeaks", yellowPeaks);
		model.addAttribute("orangePeaks", orangePeaks);
		model.addAttribute("redPeaks", redPeaks);
		model.addAttribute("greenPeaks", greenPeaks);
		model.addAttribute("conflicts", conflicts);
		return "manager/campaign";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/worker/campaigns/{campaignId}")
	public String select(@PathVariable int campaignId, Model model) {
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Worker worker = (Worker) customUserDetails.getUser();
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
		model.addAttribute("campaign", campaign);
		List<Load> loads = campaign.getPeaks();
		List<Peak> yellowPeaks = new ArrayList<Peak>();
		List<Peak> orangePeaks = new ArrayList<Peak>();
		List<Peak> redPeaks = new ArrayList<Peak>();
		List<Peak> greenPeaks = new ArrayList<Peak>();
		for (Load load : loads) {
			Peak peak = load.getPeak();
			boolean toBeAnnotated = load.isToBeAnnotated();
			if (!(toBeAnnotated)) {
				greenPeaks.add(peak);
				continue;
			}
			List<Annotation> annotations = peak.getAnnotations();
			annotations.removeIf(annotation -> annotation.getCampaign().getId() != campaign.getId());
			boolean annotated = campaignService.isAnnotated(campaign, peak, worker);
			if (annotated) {
				continue;
			}
			if (toBeAnnotated) {
				if (annotations.size() == 0) {
					yellowPeaks.add(peak);
					continue;
				}
				boolean orange = false;
				boolean red = false;
				for (Annotation annotation : annotations) {
					if (annotation.getStatus().equals(AnnotationStatus.REJECTED)) {
						red = true;
						redPeaks.add(peak);
						break;
					}
				}
				orange = !red;
				if (orange) {
					orangePeaks.add(peak);
				}
			}
		}
		model.addAttribute("yellowPeaks", yellowPeaks);
		model.addAttribute("orangePeaks", orangePeaks);
		model.addAttribute("redPeaks", redPeaks);
		model.addAttribute("greenPeaks", greenPeaks);
		return "worker/campaign";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/worker/campaigns/{campaignId}/join")
	public String joinCampaign(@PathVariable int campaignId) {
		campaignService.joinCampaign(campaignId);
		return "redirect:/worker/campaigns/" + campaignId;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/worker/campaigns/{campaignId}/leave")
	public String leaveCampaign(@PathVariable int campaignId) {
		campaignService.leaveCampaign(campaignId);
		return "redirect:/";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/manager/campaigns")
	public String insertCamapaign(@RequestParam(required = true) String name) throws Exception {
		int id = campaignService.insertCampaign(name);
		return "redirect:/manager/campaigns/" + id;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/campaigns/{campaignId}/close")
	public String closeCampaign(@PathVariable int campaignId) throws Exception {
		campaignService.closeCampaign(campaignId);
		return "redirect:/manager/campaigns/" + campaignId;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/campaigns/{campaignId}/start")
	public String startCampaign(@PathVariable int campaignId) throws Exception {
		campaignService.startCampaign(campaignId);
		return "redirect:/manager/campaigns/" + campaignId;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/manager/campaigns/{campaignId}")
	public String updateCampaign(@PathVariable int campaignId,
			@RequestParam(value = "name", required = false) String name,
			@RequestPart(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "to-be-annotated", required = false) boolean toBeAnnotated) throws Exception {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
		if (!(campaign.getStatus().equals(CampaignStatus.CREATED))) {
			// TODO
			throw new Exception();
		}
		toBeAnnotated = toBeAnnotated ? true : false;
		byte[] bytes = file.getBytes();
		String content = new String(bytes);
		campaignService.addPeaks(campaign, content, toBeAnnotated);
		return "redirect:/manager/campaigns/" + campaignId;
	}

}
