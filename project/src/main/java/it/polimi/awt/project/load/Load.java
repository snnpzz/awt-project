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

package it.polimi.awt.project.load;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.peak.Peak;

@Entity
@Table(name = "load", schema = "public")
public class Load {

	@EmbeddedId
	private LoadId id;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("campaignId")
	@JsonBackReference
	private Campaign campaign;
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("peakId")
	@JsonManagedReference
	private Peak peak;
	@Column(name = "to_be_annotated")
	private boolean toBeAnnotated;

	public Load() {
	}

	public Load(Campaign campaign, Peak peak) {
		this.id = new LoadId(campaign.getId(), peak.getId());
		this.campaign = campaign;
		this.peak = peak;
	}

	public Load(Builder builder) {
		this.id = builder.id;
		this.campaign = builder.campaign;
		this.peak = builder.peak;
		this.toBeAnnotated = builder.toBeAnnotated;
	}

	public LoadId getId() {
		return id;
	}

	public void setId(LoadId id) {
		this.id = id;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Peak getPeak() {
		return peak;
	}

	public void setPeak(Peak peak) {
		this.peak = peak;
	}

	public boolean isToBeAnnotated() {
		return toBeAnnotated;
	}

	public void setToBeAnnotated(boolean toBeAnnotated) {
		this.toBeAnnotated = toBeAnnotated;
	}

	@Embeddable
	public static class LoadId implements Serializable {

		private static final long serialVersionUID = -8650611816048044820L;

		@Column(name = "campaign_id")
		private int campaignId;
		@Column(name = "peak_id")
		private String peakId;

		public LoadId() {
		}

		public LoadId(int campaignId, String peakId) {
			this.campaignId = campaignId;
			this.peakId = peakId;
		}

		public int getCampaignId() {
			return campaignId;
		}

		public void setCampaignId(int campaignId) {
			this.campaignId = campaignId;
		}

		public String getPeakId() {
			return peakId;
		}

		public void setPeakId(String peakId) {
			this.peakId = peakId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + campaignId;
			result = prime * result + ((peakId == null) ? 0 : peakId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LoadId other = (LoadId) obj;
			if (campaignId != other.campaignId)
				return false;
			if (peakId == null) {
				if (other.peakId != null)
					return false;
			} else if (!peakId.equals(other.peakId))
				return false;
			return true;
		}

	}

	public static class Builder {

		private LoadId id;
		private Campaign campaign;
		private Peak peak;
		private boolean toBeAnnotated;

		public Builder() {
		}

		public Builder withId(LoadId id) {
			this.id = id;
			return this;
		}

		public Builder withCampaign(Campaign campaign) {
			this.campaign = campaign;
			return this;
		}

		public Builder withPeak(Peak peak) {
			this.peak = peak;
			return this;
		}

		public Builder withToBeAnnotated(boolean toBeAnnotated) {
			this.toBeAnnotated = toBeAnnotated;
			return this;
		}

		public Load build() {
			return new Load(this);
		}

	}

}
