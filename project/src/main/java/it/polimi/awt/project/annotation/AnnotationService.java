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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polimi.awt.project.annotationlocalizedname.AnnotationLocalizedName;
import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.campaign.CampaignNotFoundException;
import it.polimi.awt.project.campaign.CampaignRepository;
import it.polimi.awt.project.config.CustomUserDetails;
import it.polimi.awt.project.load.Load;
import it.polimi.awt.project.peak.Peak;
import it.polimi.awt.project.peak.PeakNotFoundException;
import it.polimi.awt.project.peak.PeakRepository;
import it.polimi.awt.project.peaklocalizedname.PeakLocalizedName;
import it.polimi.awt.project.user.Worker;

public class AnnotationService {

	private AnnotationRepository annotationRepository;
	private CampaignRepository campaignRepository;
	private PeakRepository peakRepository;

	@Autowired
	public AnnotationService(AnnotationRepository annotationRepository, CampaignRepository campaignRepository,
			PeakRepository peakRepository) {
		this.annotationRepository = annotationRepository;
		this.campaignRepository = campaignRepository;
		this.peakRepository = peakRepository;
	}

	public void acceptAnnotation(int annotationId) throws Exception {
		Annotation annotation = annotationRepository.findById(annotationId)
				.orElseThrow(() -> new AnnotationNotFoundException(annotationId));
		if (annotation.getStatus().equals(AnnotationStatus.VALID)) {
			// TODO
			throw new Exception();
		}
		annotation.setStatus(AnnotationStatus.VALID);
		annotationRepository.save(annotation);
	}

	public void insertAnnotation(int campaignId, String peakId, boolean peakValidity, Float elevation, String name,
			String content) throws Exception {
		Campaign campaign = campaignRepository.findById(campaignId)
				.orElseThrow(() -> new CampaignNotFoundException(campaignId));
		Peak peak = peakRepository.findById(peakId).orElseThrow(() -> new PeakNotFoundException(peakId));
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Worker worker = (Worker) customUserDetails.getUser();
		for (Load load : campaign.getPeaks()) {
			if (load.getPeak().getId().equals(peak.getId())) {
				if (!(load.isToBeAnnotated())) {
					// TODO
					throw new Exception();
				}
			}
		}
		if (peakValidity) {
			elevation = peak.getElevation();
			name = peak.getName();
		}
		if (elevation != null) {
			if (elevation < 0) {
				// TODO
				throw new Exception();
			}
		}
		if ((name.contains("\\")) || (name.contains("/")) || (name.contains(":")) || (name.contains("*"))
				|| (name.contains("?")) || (name.contains("\"")) || (name.contains("<")) || (name.contains(">"))
				|| (name.contains("|"))) {
			// TODO
			throw new Exception();
		}
		Annotation.Builder builder = new Annotation.Builder().withCampaign(campaign).withDate(new Date());
		if (elevation != null) {
			builder.withElevation(elevation);
		}
		builder.withName(name).withPeak(peak).withPeakValidity(peakValidity).withStatus(AnnotationStatus.VALID)
				.withTime(new Date()).withWorker(worker);
		Annotation annotation = builder.build();
		annotationRepository.save(annotation);
		List<AnnotationLocalizedName> localizedNames = new ArrayList<AnnotationLocalizedName>();
		if (!(peakValidity)) {
			if (content == null) {
				throw new Exception();
			}
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNodes = objectMapper.readTree(content);
			if (!(jsonNodes.isArray())) {
				JsonNode jsonNode = jsonNodes;
				String language = jsonNode.get("language").textValue();
				language = language.substring(0, Math.min(3, language.length()));
				AnnotationLocalizedName localizedName = new AnnotationLocalizedName.Builder().withAnnotation(annotation)
						.withLanguage(language).withName(jsonNode.get("name").textValue()).build();
				localizedNames.add(localizedName);
			}
			if (jsonNodes.isArray()) {
				for (JsonNode jsonNode : jsonNodes) {
					String language = jsonNode.get("language").textValue();
					language = language.substring(0, Math.min(3, language.length()));
					AnnotationLocalizedName localizedName = new AnnotationLocalizedName.Builder()
							.withAnnotation(annotation).withLanguage(language)
							.withName(jsonNode.get("name").textValue()).build();
					localizedNames.add(localizedName);
				}
			}
		}
		if (peakValidity) {
			for (PeakLocalizedName localizedName : peak.getLocalizedNames()) {
				localizedNames.add(new AnnotationLocalizedName.Builder().withAnnotation(annotation)
						.withLanguage(localizedName.getLanguage()).withName(localizedName.getName()).build());
			}
		}
		annotation.setLocalizedNames(localizedNames);
		annotationRepository.save(annotation);
	}

	public void rejectAnnotation(int annotationId) throws Exception {
		Annotation annotation = annotationRepository.findById(annotationId)
				.orElseThrow(() -> new AnnotationNotFoundException(annotationId));
		if (annotation.getStatus().equals(AnnotationStatus.REJECTED)) {
			// TODO
			throw new Exception();
		}
		annotation.setStatus(AnnotationStatus.REJECTED);
		annotationRepository.save(annotation);
	}

}
