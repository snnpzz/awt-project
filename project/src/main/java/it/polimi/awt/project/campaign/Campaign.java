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

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.polimi.awt.project.config.CustomEnumType;
import it.polimi.awt.project.load.Load;
import it.polimi.awt.project.user.Manager;
import it.polimi.awt.project.user.Worker;

@Entity
@Table(name = "campaign", schema = "public")
@TypeDef(name = "postgresql_enumeration", typeClass = CustomEnumType.class)
public class Campaign {

	@Column(columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;
	@Column(name = "end_date")
	@Temporal(value = TemporalType.DATE)
	private Date endDate;
	@JoinColumn(name = "manager_name")
	@ManyToOne
	private Manager manager;
	private String name;
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "campaign", orphanRemoval = true)
	@JsonManagedReference
	private List<Load> peaks;
	@Column(name = "start_date")
	@Temporal(value = TemporalType.DATE)
	private Date startDate;
	@Column(columnDefinition = "status")
	@Enumerated(EnumType.STRING)
	@Type(type = "postgresql_enumeration")
	private CampaignStatus status;
	@JoinTable(inverseJoinColumns = @JoinColumn(name = "worker_name"), joinColumns = @JoinColumn(name = "campaign_id"), name = "enroll")
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<Worker> workers;

	public Campaign() {
	}

	public Campaign(Builder builder) {
		this.endDate = builder.endDate;
		this.id = builder.id;
		this.manager = builder.manager;
		this.name = builder.name;
		this.startDate = builder.startDate;
		this.status = builder.status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Load> getPeaks() {
		return peaks;
	}

	public void setPeaks(List<Load> peaks) {
		this.peaks = peaks;
	}

	public void addPeak(Load peak) {
		this.peaks.add(peak);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public CampaignStatus getStatus() {
		return status;
	}

	public void setStatus(CampaignStatus status) {
		this.status = status;
	}

	public List<Worker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<Worker> workers) {
		this.workers = workers;
	}

	public static class Builder {

		private Date endDate;
		private int id;
		private Manager manager;
		private String name;
		// TODO peaks;
		private Date startDate;
		private CampaignStatus status;
		// TODO workers;

		public Builder() {
		}

		public Builder withEndDate(Date endDate) {
			this.endDate = endDate;
			return this;
		}

		public Builder withId(int id) {
			this.id = id;
			return this;
		}

		public Builder withManager(Manager manager) {
			this.manager = manager;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withStartDate(Date startDate) {
			this.startDate = startDate;
			return this;
		}

		public Builder withStatus(CampaignStatus status) {
			this.status = status;
			return this;
		}

		public Campaign build() {
			return new Campaign(this);
		}

	}

}
