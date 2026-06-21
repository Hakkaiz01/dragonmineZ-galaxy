package com.dragonminez.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SkillsConfig {
	public static final int CURRENT_VERSION = 3;

	@Setter
	private int configVersion;

	private final List<String> kiSkills = new ArrayList<>();
	private final List<String> formSkills = new ArrayList<>();
	private final List<String> stackSkills = new ArrayList<>();
	private final List<String> androidBlacklistedForms = new ArrayList<>();
	private final Map<String, SkillCosts> skills = new HashMap<>();
	private final Map<String, List<String>> skillOfferings = new HashMap<>();

	public SkillsConfig() {
		createDefaults();
	}

	private void createDefaults() {
		formSkills.add("superform");
		formSkills.add("legendaryforms");
		formSkills.add("godform");
		formSkills.add("androidforms");

		stackSkills.add("kaioken");
//		stackSkills.add("ultrainstinct");
//		stackSkills.add("ultraego");

		androidBlacklistedForms.add("superform");
		androidBlacklistedForms.add("legendaryforms");

		List<Integer> jumpCosts = new ArrayList<>();
		jumpCosts.add(300);
		jumpCosts.add(600);
		jumpCosts.add(900);
		jumpCosts.add(1200);
		jumpCosts.add(1500);
		jumpCosts.add(1800);
		jumpCosts.add(2100);
		jumpCosts.add(2400);
		jumpCosts.add(2700);
		jumpCosts.add(3000);
		skills.put("jump", new SkillCosts(jumpCosts));

		List<Integer> flyCosts = new ArrayList<>();
		flyCosts.add(1500);
		flyCosts.add(600);
		flyCosts.add(900);
		flyCosts.add(1200);
		flyCosts.add(1500);
		flyCosts.add(1800);
		flyCosts.add(2100);
		flyCosts.add(2400);
		flyCosts.add(2700);
		flyCosts.add(3000);
		skills.put("fly", new SkillCosts(flyCosts));

		List<Integer> potentialUnlockCosts = new ArrayList<>();
		potentialUnlockCosts.add(600);
		potentialUnlockCosts.add(1600);
		potentialUnlockCosts.add(2400);
		potentialUnlockCosts.add(3200);
		potentialUnlockCosts.add(4000);
		potentialUnlockCosts.add(4800);
		potentialUnlockCosts.add(5600);
		potentialUnlockCosts.add(6400);
		potentialUnlockCosts.add(7200);
		potentialUnlockCosts.add(8000);
		potentialUnlockCosts.add(-1);
		potentialUnlockCosts.add(8800);
		potentialUnlockCosts.add(9600);
		skills.put("potentialunlock", new SkillCosts(potentialUnlockCosts));

		List<Integer> meditationCosts = new ArrayList<>();
		meditationCosts.add(300);
		meditationCosts.add(600);
		meditationCosts.add(900);
		meditationCosts.add(1200);
		meditationCosts.add(1500);
		meditationCosts.add(1800);
		meditationCosts.add(2100);
		meditationCosts.add(2400);
		meditationCosts.add(2700);
		meditationCosts.add(3000);
		skills.put("meditation", new SkillCosts(meditationCosts));

		List<Integer> kiControlCosts = new ArrayList<>();
		kiControlCosts.add(300);
		kiControlCosts.add(600);
		kiControlCosts.add(900);
		kiControlCosts.add(1200);
		kiControlCosts.add(1500);
		kiControlCosts.add(1800);
		kiControlCosts.add(2100);
		kiControlCosts.add(2400);
		kiControlCosts.add(2700);
		kiControlCosts.add(3000);
		skills.put("kicontrol", new SkillCosts(kiControlCosts));

		List<Integer> kiSenseCosts = new ArrayList<>();
		kiSenseCosts.add(300);
		kiSenseCosts.add(600);
		kiSenseCosts.add(900);
		kiSenseCosts.add(1200);
		kiSenseCosts.add(1500);
		kiSenseCosts.add(1800);
		kiSenseCosts.add(2100);
		kiSenseCosts.add(2400);
		kiSenseCosts.add(2700);
		kiSenseCosts.add(3000);
		skills.put("kisense", new SkillCosts(kiSenseCosts));

		List<Integer> kiManipulationCosts = new ArrayList<>();
		kiManipulationCosts.add(3600);
		kiManipulationCosts.add(600);
		kiManipulationCosts.add(900);
		kiManipulationCosts.add(1200);
		kiManipulationCosts.add(1500);
		kiManipulationCosts.add(1800);
		kiManipulationCosts.add(2100);
		kiManipulationCosts.add(2400);
		kiManipulationCosts.add(2700);
		kiManipulationCosts.add(3300);
		skills.put("kimanipulation", new SkillCosts(kiManipulationCosts));

		List<Integer> kaiokenCosts = new ArrayList<>();
		kaiokenCosts.add(1000);
		kaiokenCosts.add(1500);
		kaiokenCosts.add(2500);
		kaiokenCosts.add(4000);
		kaiokenCosts.add(7500);
//		kaiokenCosts.add(25000);
		skills.put("kaioken", new SkillCosts(kaiokenCosts));

//		List<Integer> ultraInstinctCosts = new ArrayList<>();
//		ultraInstinctCosts.add(-1);
//		ultraInstinctCosts.add(5000);
//		skills.put("ultrainstinct", new SkillCosts(ultraInstinctCosts));

//		List<Integer> ultraEgoCosts = new ArrayList<>();
//		ultraEgoCosts.add(-1);
//		ultraEgoCosts.add(5000);
//		skills.put("ultraego", new SkillCosts(ultraEgoCosts));

		List<Integer> fusionCosts = new ArrayList<>();
		fusionCosts.add(25000);
		fusionCosts.add(5000);
		fusionCosts.add(10000);
		fusionCosts.add(15000);
		fusionCosts.add(20000);
		skills.put("fusion", new SkillCosts(fusionCosts));

		// MASTER SKILLS OFFERINGS
		List<String> roshiSkills = new ArrayList<>();
		roshiSkills.add("jump");
		roshiSkills.add("meditation");
		roshiSkills.add("kicontrol");
		skillOfferings.put("roshi", roshiSkills);

		List<String> gokuSkills = new ArrayList<>();
		gokuSkills.add("fly");
		gokuSkills.add("kicontrol");
		gokuSkills.add("kisense");
		gokuSkills.add("fusion");
		gokuSkills.add("potentialunlock");
		skillOfferings.put("goku", gokuSkills);

		List<String> kingKaiSkills = new ArrayList<>();
		kingKaiSkills.add("kaioken");
		kingKaiSkills.add("potentialunlock");
		kingKaiSkills.add("kimanipulation");
		kingKaiSkills.add("fusion");
		skillOfferings.put("kingkai", kingKaiSkills);

		List<String> defaultSkills = new ArrayList<>();
		defaultSkills.add("jump");
		skillOfferings.put("default", defaultSkills);
	}

	public SkillCosts getSkillCosts(String skillName) {
		return skills.getOrDefault(skillName.toLowerCase(), new SkillCosts(new ArrayList<>()));
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkillCosts {
		private List<Integer> costs;
	}
}