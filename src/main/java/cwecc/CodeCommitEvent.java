/*
// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
*/

package cwecc;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeCommitEvent {

	public static class Detail {
		private String detail;

		private String event;
		
		private String repositoryName;
		
		private String referenceType;
		
		private String referenceName;
		
		private String commitId;

		private String oldCommitId;

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getEvent() {
			return event;
		}

		public void setEvent(String event) {
			this.event = event;
		}

		public String getRepositoryName() {
			return repositoryName;
		}

		public void setRepositoryName(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		public String getReferenceType() {
			return referenceType;
		}

		public void setReferenceType(String referenceType) {
			this.referenceType = referenceType;
		}

		public String getReferenceName() {
			return referenceName;
		}

		public void setReferenceName(String referenceName) {
			this.referenceName = referenceName;
		}

		public String getCommitId() {
			return commitId;
		}

		public void setCommitId(String commitId) {
			this.commitId = commitId;
		}

		public String getOldCommitId() {
			return oldCommitId;
		}

		public void setOldCommitId(String oldCommitId) {
			this.oldCommitId = oldCommitId;
		}
	}

	private String version;

	private String id;
	
	private String detailType;
	
	private String source;
	
	private String account;
	
	private String time;
	
	private String region;
	
	private List<String> resources;
	
	private Detail detail;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDetailType() {
		return detailType;
	}

	@JsonProperty("detail-type")
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public Detail getDetail() {
		return detail;
	}

	public void setDetail(Detail detail) {
		this.detail = detail;
	}
}
