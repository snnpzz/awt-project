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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polimi.awt.project.annotation.Annotation;
import it.polimi.awt.project.config.CustomUserDetails;
import it.polimi.awt.project.load.Load;
import it.polimi.awt.project.load.Load.LoadId;
import it.polimi.awt.project.peak.Peak;
import it.polimi.awt.project.peak.PeakRepository;
import it.polimi.awt.project.peaklocalizedname.PeakLocalizedName;
import it.polimi.awt.project.user.Manager;
import it.polimi.awt.project.user.UserNotFoundException;
import it.polimi.awt.project.user.Worker;
import it.polimi.awt.project.user.WorkerRepository;

public class CampaignService {

	private CampaignRepository campaignRepository;
	private PeakRepository peakRepository;
	private WorkerRepository workerRepository;

	@Autowired
	public CampaignService(CampaignRepository campaignRepository, PeakRepository peakRepository,
			WorkerRepository workerRepository) {
		this.campaignRepository = campaignRepository;
		this.peakRepository = peakRepository;
		this.workerRepository = workerRepository;
	}

	public void addPeaks(Campaign campaign, String content, boolean toBeAnnotated) throws Exception {
		if (content == null) {
			throw new Exception();
		}
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(content);
		List<Peak> peaks = deserializePeaks(jsonNode);
		for (Peak peak : peaks) {
			Optional<Peak> maybePeak = peakRepository.findById(peak.getId());
			if (maybePeak.isPresent()) {
				boolean present = false;
				for (Load load : campaign.getPeaks()) {
					if (load.getCampaign().getId() == campaign.getId()) {
						if (load.getPeak().getId().equals(peak.getId())) {
							if (load.isToBeAnnotated() == toBeAnnotated) {
								present = true;
							}
						}
					}
				}
				if (!(present)) {
					Load load = new Load.Builder().withId(new LoadId(campaign.getId(), peak.getId()))
							.withCampaign(campaign).withPeak(peak).withToBeAnnotated(toBeAnnotated).build();
					campaign.addPeak(load);
				}
			} else {
				peakRepository.save(peak);
				Load load = new Load.Builder().withId(new LoadId(campaign.getId(), peak.getId())).withCampaign(campaign)
						.withPeak(peak).withToBeAnnotated(toBeAnnotated).build();
				campaign.addPeak(load);
			}
		}
		campaignRepository.save(campaign);
	}

	public void closeCampaign(Integer id) throws Exception {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new CampaignNotFoundException(id));
		if (!(campaign.getStatus().equals(CampaignStatus.STARTED))) {
			// TODO
			throw new Exception();
		}
		campaign.setEndDate(new Date());
		campaign.setStatus(CampaignStatus.CLOSED);
		campaignRepository.save(campaign);
	}

	public int insertCampaign(String name) throws Exception {
		if ((name.contains("\\")) || (name.contains("/")) || (name.contains(":")) || (name.contains("*"))
				|| (name.contains("?")) || (name.contains("\"")) || (name.contains("<")) || (name.contains(">"))
				|| (name.contains("|"))) {
			// TODO
			throw new Exception();
		}
		Manager manager = (Manager) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		Campaign campaign = new Campaign.Builder().withManager(manager).withName(name)
				.withStatus(CampaignStatus.CREATED).build();
		campaignRepository.save(campaign);
		return campaign.getId();
	}

	private List<PeakLocalizedName> deserializeLocalizedNames(JsonNode jsonNodes, Peak peak) {
		List<PeakLocalizedName> localizedNames = new ArrayList<PeakLocalizedName>();
		if (!(jsonNodes.isNull())) {
			if (!(jsonNodes.isArray())) {
				JsonNode jsonNode = jsonNodes;
				String language = jsonNode.get(0).textValue();
				String name = jsonNode.get(1).textValue();
				language = language.substring(0, Math.min(3, language.length()));
				PeakLocalizedName localizedName = new PeakLocalizedName.Builder().withLanguage(language).withName(name)
						.withPeak(peak).build();
				localizedNames.add(localizedName);
			}
			if (jsonNodes.isArray()) {
				for (JsonNode jsonNode : jsonNodes) {
					String language = jsonNode.get(0).textValue();
					String name = jsonNode.get(1).textValue();
					language = language.substring(0, Math.min(3, language.length()));
					PeakLocalizedName localizedName = new PeakLocalizedName.Builder().withLanguage(language)
							.withName(name).withPeak(peak).build();
					localizedNames.add(localizedName);
				}
			}
		}
		return localizedNames;
	}

	private List<Peak> deserializePeaks(JsonNode jsonNodes) {
		List<Peak> peaks = new ArrayList<Peak>();
		if (!(jsonNodes.isArray())) {
			JsonNode jsonNode = jsonNodes;
			String id = jsonNode.get("id").asText();
			String provenance = jsonNode.get("provenance").textValue();
			String elevation = jsonNode.get("elevation").asText();
			String longitude = jsonNode.get("longitude").asText();
			String latitude = jsonNode.get("latitude").asText();
			String name = jsonNode.get("name").textValue();
			id = id.substring(0, Math.min(7, id.length()));
			Peak.Builder builder = new Peak.Builder().withId(id).withProvenance(provenance);
			if (!(elevation.equals("null"))) {
				builder.withElevation(Float.parseFloat(elevation));
			}
			if (!(longitude.equals("null"))) {
				builder.withLongitude(Float.parseFloat(longitude));
			}
			if (!(latitude.equals("null"))) {
				builder.withLatitude(Float.parseFloat(latitude));
			}
			builder.withName(name);
			Peak peak = builder.build();
			List<PeakLocalizedName> localizedNames = deserializeLocalizedNames(jsonNode.get("localized_names"), peak);
			peak.setLocalizedNames(localizedNames);
			peaks.add(peak);
		}
		if (jsonNodes.isArray()) {
			for (JsonNode jsonNode : jsonNodes) {
				String id = jsonNode.get("id").asText();
				String provenance = jsonNode.get("provenance").textValue();
				String elevation = jsonNode.get("elevation").asText();
				String longitude = jsonNode.get("longitude").asText();
				String latitude = jsonNode.get("latitude").asText();
				String name = jsonNode.get("name").textValue();
				id = id.substring(0, Math.min(7, id.length()));
				Peak.Builder builder = new Peak.Builder().withId(id).withProvenance(provenance);
				if (!(elevation.equals("null"))) {
					builder.withElevation(Float.parseFloat(elevation));
				}
				if (!(longitude.equals("null"))) {
					builder.withLongitude(Float.parseFloat(longitude));
				}
				if (!(latitude.equals("null"))) {
					builder.withLatitude(Float.parseFloat(latitude));
				}
				builder.withName(name);
				Peak peak = builder.build();
				List<PeakLocalizedName> localizedNames = deserializeLocalizedNames(jsonNode.get("localized_names"),
						peak);
				peak.setLocalizedNames(localizedNames);
				peaks.add(peak);
			}
		}
		return peaks;
	}

	public List<Campaign> getCampaigns(String managerName) {
		return campaignRepository.findByManagerName(managerName);
	}

	public List<Campaign> selectStartedCampaigns(boolean enrolled, String workerName) {
		List<Campaign> startedCampaigns = campaignRepository.findByStatus(CampaignStatus.STARTED);
		Worker worker = workerRepository.findById(workerName).orElseThrow(() -> new UserNotFoundException(workerName));
		if (!(enrolled)) {
			startedCampaigns.removeIf(campaign -> campaign.getWorkers().contains(worker));
		}
		if (enrolled) {
			startedCampaigns.removeIf(campaign -> !(campaign.getWorkers().contains(worker)));
		}
		return startedCampaigns;
	}

	public boolean isAnnotated(Campaign campaign, Peak peak, Worker worker) {
		List<Annotation> annotations = peak.getAnnotations();
		annotations.removeIf(annotation -> annotation.getCampaign().getId() != campaign.getId());
		for (Annotation annotation : annotations) {
			if (annotation.getWorker().getName().equals(worker.getName())) {
				return true;
			}
		}
		return false;
	}

	public void joinCampaign(Integer id) {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new CampaignNotFoundException(id));
		Worker worker = (Worker) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		List<Worker> workers = campaign.getWorkers();
		if (!(workers.contains(worker))) {
			campaign.getWorkers().add(worker);
			campaignRepository.save(campaign);
		}
	}

	public void leaveCampaign(Integer id) {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new CampaignNotFoundException(id));
		Worker worker = (Worker) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		List<Worker> workers = campaign.getWorkers();
		if (workers.contains(worker)) {
			campaign.getWorkers().remove(worker);
			campaignRepository.save(campaign);
		}
	}

	public void startCampaign(Integer id) throws Exception {
		Campaign campaign = campaignRepository.findById(id).orElseThrow(() -> new CampaignNotFoundException(id));
		Manager manager = (Manager) ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal()).getUser();
		if (!(campaign.getManager().getName().equals(manager.getName()))) {
			// TODO
			throw new Exception();
		}
		if (!(campaign.getStatus().equals(CampaignStatus.CREATED))) {
			// TODO
			throw new Exception();
		}
		campaign.setStartDate(new Date());
		campaign.setStatus(CampaignStatus.STARTED);
		campaignRepository.save(campaign);
	}

}
