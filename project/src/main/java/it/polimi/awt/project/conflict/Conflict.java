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

package it.polimi.awt.project.conflict;

import it.polimi.awt.project.campaign.Campaign;
import it.polimi.awt.project.peak.Peak;

public class Conflict {

	private Campaign campaign;
	private Peak peak;
	private int notValid;
	private int valid;

	public Conflict() {
	}

	public Conflict(Builder builder) {
		this.campaign = builder.campaign;
		this.peak = builder.peak;
		this.notValid = builder.notValid;
		this.valid = builder.valid;
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

	public int getNotValid() {
		return notValid;
	}

	public void setNotValid(int notValid) {
		this.notValid = notValid;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public static class Builder {

		private Campaign campaign;
		private Peak peak;
		private int notValid;
		private int valid;

		public Builder() {
		}

		public Builder withCampaign(Campaign campaign) {
			this.campaign = campaign;
			return this;
		}

		public Builder withPeak(Peak peak) {
			this.peak = peak;
			return this;
		}

		public Builder withNotValid(int notValid) {
			this.notValid = notValid;
			return this;
		}

		public Builder withValid(int valid) {
			this.valid = valid;
			return this;
		}

		public Conflict build() {
			return new Conflict(this);
		}

	}

}
