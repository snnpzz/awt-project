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

package it.polimi.awt.project.peak;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.polimi.awt.project.annotation.Annotation;
import it.polimi.awt.project.annotation.AnnotationRepository;
import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.campaign.CampaignNotFoundException;
import it.polimi.awt.project.campaign.CampaignRepository;

@Controller
public class PeakController {

	private final AnnotationRepository annotationRepository;
	private final CampaignRepository campaignRepository;
	private final PeakRepository peakRepository;

	@Autowired
	public PeakController(AnnotationRepository annotationRepository, CampaignRepository campaignRepository,
			PeakRepository peakRepository) {
		this.annotationRepository = annotationRepository;
		this.campaignRepository = campaignRepository;
		this.peakRepository = peakRepository;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/campaigns/{campaignId}/peaks/{peakId}")
	public String get(@PathVariable int campaignId, @PathVariable String peakId, Model model) {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
		Peak peak = peakRepository.findById(peakId).orElseThrow(() -> new PeakNotFoundException(peakId));
		Optional<List<Annotation>> maybeAnnotations = annotationRepository.findByCampaignIdAndPeakId(campaignId,
				peakId);
		List<Annotation> annotations;
		if (maybeAnnotations.isPresent()) {
			annotations = maybeAnnotations.get();
		} else {
			annotations = new ArrayList<Annotation>();
		}
		model.addAttribute("campaign", campaign);
		model.addAttribute("peak", peak);
		model.addAttribute("annotations", annotations);
		return "manager/peak";
	}

}
