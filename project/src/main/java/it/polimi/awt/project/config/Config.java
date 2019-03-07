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

package it.polimi.awt.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.databind.Module;

import it.polimi.awt.project.annotation.AnnotationRepository;
import it.polimi.awt.project.annotation.AnnotationService;
import it.polimi.awt.project.campaign.CampaignRepository;
import it.polimi.awt.project.campaign.CampaignService;
import it.polimi.awt.project.peak.PeakRepository;
import it.polimi.awt.project.peak.PeakService;
import it.polimi.awt.project.user.ManagerRepository;
import it.polimi.awt.project.user.ManagerService;
import it.polimi.awt.project.user.WorkerRepository;
import it.polimi.awt.project.user.WorkerService;

@Configuration
public class Config {

	@Autowired
	private AnnotationRepository annotationRepository;
	@Autowired
	private CampaignRepository campaignRepository;
	@Autowired
	private ManagerRepository managerRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private PeakRepository peakRepository;
	@Autowired
	private WorkerRepository workerRepository;

	@Bean
	public AnnotationService annotationService() {
		return new AnnotationService(annotationRepository, campaignRepository, peakRepository);
	}

	@Bean
	public CampaignService campaignService() {
		return new CampaignService(campaignRepository, peakRepository, workerRepository);
	}

	@Bean
	public ManagerService managerService() {
		return new ManagerService(managerRepository, passwordEncoder);
	}

	@Bean
	public Module hibernate5Module() {
		return new Hibernate5Module();
	}

	@Bean
	public PeakService peakService() {
		return new PeakService(peakRepository);
	}

	@Bean
	public WorkerService workerService() {
		return new WorkerService(passwordEncoder, workerRepository);
	}

}
