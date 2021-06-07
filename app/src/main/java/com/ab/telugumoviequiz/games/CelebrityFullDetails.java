package com.ab.telugumoviequiz.games;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CelebrityFullDetails {
	
	private List<String> masterNames = new ArrayList<>();
	private List<UpcomingCelebrity> namesList = new ArrayList<>();

	public List<String> getMasterNames() {
		return masterNames;
	}
	public void setMasterNames(List<String> masterNames) {
		this.masterNames = masterNames;
	}
	public List<UpcomingCelebrity> getNamesList() {
		return namesList;
	}
	public void setNamesList(List<UpcomingCelebrity> namesList) {
		this.namesList = namesList;
	}
	
	@NonNull
	@Override
	public String toString() {
		return "CelebrityFullDetails [masterNames=" + masterNames + ", namesList=" + namesList + "]";
	}
}


