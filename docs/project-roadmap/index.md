---
title: Project Roadmap
---

# Check-Ins Platform Roadmap: Boosting Engagement and Development

***This roadmap is subject to change and should not be construed as a commitment to build these features.***

This roadmap prioritizes features focused on enhancing the team member review process, integrating the shelved onboarding process, and incorporating generative AI for mentor support.

## Streamlined and Repeatable Reviews (0.8.0)

1. **Review Planning & Validation:**
    * Implement a dedicated review planning phase.
    * Automatically assign reviewers and validate assignments with managers.
    * Enable multi-layered team review structures:
        * Managers review planned assignments for subordinate managers.
        * Subordinate managers incorporate suggestions before final approval.
    * Integrate timeline creation and review launch functionalities for administrators.
2. **Review Implementation:**
    * Streamline self-reviews and manager reviews.
    * Introduce automated reminders to both managers and team members.
    * Provide automated status overview emails to administrators.
    * Provide relevant contextual information during reviews, including:
        * Current title and job description
        * Skills listed in user profiles
        * Feedback received previously
3. **Reporting:**
    * Develop review process reports detailing completion rates (breakdowns by department, etc.).
    * Create manager reports that detail individual and aggregate review outcomes.

### Success Metrics:

* Increased on-time completion rates for employee reviews
* Increased on-time review period start rates
* Improved user satisfaction with the review process

## Onboarding and Modernization (0.9.0)

1. **Modularize Deployment:**
   * Upgrade to Micronaut 4.x.
   * Introduce infrastructure as code.
      * Codify GCP environments with Terraform.
      * Migrate to cloud-based secret management.
   * Segment service deployments.
2. **Revive Onboarding Application:**
    * Rebase and refresh the onboarding application.
3. **Configuration Management UI:**
    * Integrate configuration into the UI to allow for dynamic configuration.

### Success Metrics:

* Increased completion of onboarding tasks before the new employees’ start dates
* Increased user satisfaction with the onboarding process
* Increased application flexibility

## Professional Development Support (0.10.0)

1. **Job Library & Assignment:**
    * Build a comprehensive job library within the platform.
    * Enable defining and assigning specific job roles.
2. **Generative AI Integration:**
    * Develop Google Gemini integration module.
    * Construct prompts that analyze notes, feedback, job descriptions, and skills
      data.
    * Construct prompts to provide personalized suggestions for mentors:
        * Recommend development goals based on mentee information.
        * Suggest relevant learning resources or training opportunities.
        * Generate actionable development suggestions for increasing desired capabilities.
    * Incorporation of generated suggestions into the check-in planning process.

### Success Metrics:

* Higher engagement with the Check-Ins workflows
* Enhanced employee development and career planning outcomes
