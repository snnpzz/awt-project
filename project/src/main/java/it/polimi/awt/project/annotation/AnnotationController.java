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

package it.polimi.awt.project.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnnotationController {

	private final AnnotationRepository annotationRepository;
	private final AnnotationService annotationService;

	@Autowired
	public AnnotationController(AnnotationRepository annotationRepository, AnnotationService annotationService) {
		this.annotationRepository = annotationRepository;
		this.annotationService = annotationService;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/worker/campaigns/{campaignId}/peaks/{peakId}/annotate")
	public String post(@PathVariable int campaignId, @PathVariable String peakId,
			@RequestParam(required = true, value = "custom-radio-inline") String customRadioInline,
			@RequestParam(required = false) Float elevation, @RequestParam(required = false) String name,
			@RequestParam(required = false, value = "localized-names") String content) throws Exception {
		boolean peakValidity = false;
		if (customRadioInline.equals("valid")) {
			peakValidity = true;
		}
		if (customRadioInline.equals("invalid")) {
			peakValidity = false;
		}
		annotationService.insertAnnotation(campaignId, peakId, peakValidity, elevation, name, content);
		return "redirect:/worker/campaigns/" + campaignId;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/manager/annotations/{annotationId}/reject")
	public String rejectAnnotation(@PathVariable int annotationId) throws Exception {
		annotationService.rejectAnnotation(annotationId);
		Annotation annotation = annotationRepository.findById(annotationId)
				.orElseThrow(() -> new AnnotationNotFoundException(annotationId));
		return "redirect:/manager/campaigns/" + annotation.getCampaign().getId() + "/peaks/"
				+ annotation.getPeak().getId();
	}

}
