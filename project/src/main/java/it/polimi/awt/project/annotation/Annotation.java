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

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.polimi.awt.project.annotationlocalizedname.AnnotationLocalizedName;
import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.config.CustomEnumType;
import it.polimi.awt.project.peak.Peak;
import it.polimi.awt.project.user.Worker;

@Entity
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })
@Table(name = "annotation", schema = "public")
@TypeDef(name = "postgresql_enumeration", typeClass = CustomEnumType.class)
public class Annotation {

	@Column(columnDefinition = "serial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int id;
	@JoinColumn(name = "campaign_id")
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	private Campaign campaign;
	@Temporal(value = TemporalType.DATE)
	private Date date;
	private float elevation;
	@JsonManagedReference
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "annotation", orphanRemoval = true)
	private List<AnnotationLocalizedName> localizedNames;
	private String name;
	@JoinColumn(name = "peak_id")
	@JsonBackReference
	@ManyToOne
	private Peak peak;
	@Column(name = "peak_validity")
	private boolean peakValidity;
	@Column(columnDefinition = "status")
	@Enumerated(EnumType.STRING)
	@Type(type = "postgresql_enumeration")
	private AnnotationStatus status;
	@Temporal(value = TemporalType.TIME)
	private Date time;
	@JoinColumn(name = "worker_name")
	@ManyToOne
	private Worker worker;

	public Annotation() {
	}

	public Annotation(Builder builder) {
		this.campaign = builder.campaign;
		this.date = builder.date;
		this.elevation = builder.elevation;
		this.id = builder.id;
		this.localizedNames = builder.localizedNames;
		this.name = builder.name;
		this.peak = builder.peak;
		this.peakValidity = builder.peakValidity;
		this.status = builder.status;
		this.time = builder.time;
		this.worker = builder.worker;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getElevation() {
		return elevation;
	}

	public void setElevation(float elevation) {
		this.elevation = elevation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<AnnotationLocalizedName> getLocalizedNames() {
		return localizedNames;
	}

	public void setLocalizedNames(List<AnnotationLocalizedName> localizedNames) {
		this.localizedNames = localizedNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Peak getPeak() {
		return peak;
	}

	public void setPeak(Peak peak) {
		this.peak = peak;
	}

	public boolean isPeakValidity() {
		return peakValidity;
	}

	public void setPeakValidity(boolean peakValidity) {
		this.peakValidity = peakValidity;
	}

	public AnnotationStatus getStatus() {
		return status;
	}

	public void setStatus(AnnotationStatus status) {
		this.status = status;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	public static class Builder {

		private Campaign campaign;
		private Date date;
		private float elevation;
		private int id;
		private List<AnnotationLocalizedName> localizedNames;
		private String name;
		private Peak peak;
		private boolean peakValidity;
		private AnnotationStatus status;
		private Date time;
		private Worker worker;

		public Builder() {
		}

		public Builder withCampaign(Campaign campaign) {
			this.campaign = campaign;
			return this;
		}

		public Builder withDate(Date date) {
			this.date = date;
			return this;
		}

		public Builder withElevation(float elevation) {
			this.elevation = elevation;
			return this;
		}

		public Builder withId(int id) {
			this.id = id;
			return this;
		}

		public Builder withLocalizedNames(List<AnnotationLocalizedName> localizedNames) {
			this.localizedNames = localizedNames;
			return this;
		}

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withPeak(Peak peak) {
			this.peak = peak;
			return this;
		}

		public Builder withPeakValidity(boolean peakValidity) {
			this.peakValidity = peakValidity;
			return this;
		}

		public Builder withStatus(AnnotationStatus status) {
			this.status = status;
			return this;
		}

		public Builder withTime(Date time) {
			this.time = time;
			return this;
		}

		public Builder withWorker(Worker worker) {
			this.worker = worker;
			return this;
		}

		public Annotation build() {
			return new Annotation(this);
		}

	}

}
