---
title: Project Roadmap
---

# Check-Ins Platform Roadmap: Boosting Engagement and Development

***This roadmap is subject to change and should not be construed as a commitment to build these features.***

This roadmap prioritizes features focused on enhancing team member and mentor support, white labeling the application,
and preparing it for deployment into multiple environment types.

## Release Clean Up (v0.8.x)

- [x] Documentation Updates
- [x] Other Dependency Upgrades and Security Alert Remediation
- [ ] Bug Smooshing and Tech Debt Remediation

## Configuration UI and Infrastructure Codification (v0.9.0)

1. **Introduce Infrastructure as Code**
   - Create Terraform code for
        - [ ] Local Deployment
        - [ ] GCP Deployment
        - [ ] AWS Deployment (stretch-goal)
   - [ ] Migrate to cloud-based secret management
2. **Configuration Management UI**
    - [ ] Implement feature switches
    - [ ] Move most configuration into the UI and store in the database

### Goals (v0.9.0)

* Simple and repeatable local environment setup
* Simple and repeatable cloud environment setup
* Increased application flexibility
* Ability to right-size features for different organizations

## Professional Development Support (v0.10.0)

1. **Role Library & Assignment**
   - [ ] Add the ability to create and maintain job roles
     - [ ] Create a versioning mechanism for roles in order to provide "moment in time" information
   - [ ] Add the ability to assign and track roles over time
2. **Generative AI Integration**
   - [ ] Develop GenAI integration module
   - [ ] Construct prompts and workflows that analyze notes, feedback, job descriptions, and skills data to provide summarized and actionable growth and performance insights
   - **Construct prompts and workflows to provide personalized suggestions for mentors**
     - [ ] Recommend development goals based on mentee information
     - [ ] Suggest relevant learning resources or training opportunities
     - [ ] Generate actionable development suggestions for increasing desired capabilities
     - [ ] Incorporate generated suggestions into the check-in planning process
3. **Career Timeline**
   - **Create a timeline component to show important events in an employee's journey, including:**
     - [ ] Role changes
     - [ ] Title changes
     - [ ] Joining/leaving a team
     - [ ] Getting awarded a certification
     - [ ] Joining/leaving a guild or community
     - [ ] Completing a training event (stretch goal)

### Goals (v0.10.0)

* Higher engagement with the Check-Ins planning workflows
* Improved employee development and career planning outcomes
* Provide a view of an employee's career and development journey

## Some day...

1. **Revive Onboarding Application:**
   * Reintroduce and refresh the onboarding application